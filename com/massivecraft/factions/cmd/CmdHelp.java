package com.massivecraft.factions.cmd;

import java.util.ArrayList;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.P;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;


public class CmdHelp extends FCommand
{
	
	public CmdHelp()
	{
		super();
		this.aliases.add("help");
		this.aliases.add("h");
		this.aliases.add("?");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("page", "1");
		
		this.permission = Permission.HELP.node;
		this.disableOnLock = false;
		
		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}	
	
	@Override
	public void perform()
	{
		if (helpPages == null) updateHelp();
		
		int page = this.argAsInt(0, 1);
		
		sendMessage(p.txt.titleize("Pomoc miasta ("+page+"/"+helpPages.size()+")"));
		
		page -= 1;
		
		if (page < 0 || page >= helpPages.size())
		{
			msg("<b>Ta strona nie istnieje");
			return;
		}
		sendMessage(helpPages.get(page));
	}
	
	//----------------------------------------------//
	// Build the help pages
	//----------------------------------------------//
	
	public ArrayList<ArrayList<String>> helpPages;
	
	public void updateHelp()
	{
		helpPages = new ArrayList<ArrayList<String>>();
		ArrayList<String> pageLines;

		pageLines = new ArrayList<String>();
		pageLines.add( p.cmdBase.cmdHelp.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdList.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdShow.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdPower.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdJoin.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdLeave.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdChat.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdHome.getUseageTemplate(true) );
		pageLines.add( p.txt.parse("<i>Naucz sie jak tworzyc miasto na nastepnych stronach.") );
		helpPages.add(pageLines);
		
		pageLines = new ArrayList<String>();
		pageLines.add( p.cmdBase.cmdCreate.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdDescription.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdTag.getUseageTemplate(true) );
		pageLines.add( p.txt.parse("<i>Mozesz chciec ja zamknac i uzywac zaproszen:" ));
		pageLines.add( p.cmdBase.cmdOpen.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdInvite.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdDeinvite.getUseageTemplate(true) );
		pageLines.add( p.txt.parse("<i>I nie zapomnij ustawic spawnu miasta:" ));
		pageLines.add( p.cmdBase.cmdSethome.getUseageTemplate(true) );
		helpPages.add(pageLines);
		
		if (Econ.isSetup() && Conf.econEnabled && Conf.bankEnabled)
		{
			pageLines = new ArrayList<String>();
			pageLines.add( "" );
                        pageLines.add( p.txt.parse("<i>Twoje miasto posiada bank, ktory jest uzywany do placenia za" ));
                        pageLines.add( p.txt.parse("<i>pewne rzeczy, wiec przydalo by sie zlozyc w nim pieniadze." ));
                        pageLines.add( p.txt.parse("<i>Aby dowiedziec sie wiecej, uzyj komendy money." ));
			pageLines.add( "" );
			pageLines.add( p.cmdBase.cmdMoney.getUseageTemplate(true) );
			pageLines.add( "" );
			pageLines.add( "" );
			pageLines.add( "" );
			helpPages.add(pageLines);
		}
		
		pageLines = new ArrayList<String>();
		pageLines.add( p.cmdBase.cmdClaim.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdAutoClaim.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdUnclaim.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdUnclaimall.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdKick.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdMod.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdAdmin.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdTitle.getUseageTemplate(true) );
                pageLines.add( p.txt.parse("<i>Tytuly sa tylko dla zabawy. Nie sa z nimi powiazane zadne prawa." ));
		helpPages.add(pageLines);
		
		pageLines = new ArrayList<String>();
		pageLines.add( p.cmdBase.cmdMap.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdBoom.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdOwner.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdOwnerList.getUseageTemplate(true) );
                pageLines.add(p.txt.parse("<i>Zajete tereny sa dodatkowo chronione tak, ze"));
                pageLines.add(p.txt.parse("<i>tylko wlasciciel(e), burmistrz miasta i ewentualnie"));
                pageLines.add(p.txt.parse("<i>zastepca burmistrza maja do niej pelen dostep"));
		helpPages.add(pageLines);
		
		pageLines = new ArrayList<String>();
		pageLines.add( p.cmdBase.cmdDisband.getUseageTemplate(true) );
		pageLines.add("");
		pageLines.add( p.cmdBase.cmdRelationAlly.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdRelationNeutral.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdRelationEnemy.getUseageTemplate(true) );
                pageLines.add(p.txt.parse("<i>Ustawia relacje jaka CHCIALBYS miec z innym miastem."));
                pageLines.add(p.txt.parse("<i>Domyslnie relacje z innymi miastami sa neutralne."));
                pageLines.add(p.txt.parse("<i>Jesli OBYDWA miasta wybiora \"ally\" bedziecie sojusznikami."));
                pageLines.add(p.txt.parse("<i>Jesli JEDNO miasto wybierze \"enemy\" bedziecie wrogami."));
		helpPages.add(pageLines);
		
		pageLines = new ArrayList<String>();
                pageLines.add(p.txt.parse("<i>Nigdy nie mozesz zranic czlonkow miasta i sojusznikow."));
                pageLines.add(p.txt.parse("<i>Nie mozesz zranic neutralnych graczy, gdy sa na swoim terytorium."));
                pageLines.add(p.txt.parse("<i>Zawsze mozesz zranic wrogow i graczy bez miasta."));
                pageLines.add("");
                pageLines.add(p.txt.parse("<i>Ginac tracisz energie. Odnawia sie ona z czasem."));
                pageLines.add(p.txt.parse("<i>Energia miasta to suma mocy wszystkich czlonkow."));
                pageLines.add(p.txt.parse("<i>Energia miasta okresla ile terenu moze ono zajac."));
                pageLines.add(p.txt.parse("<i>Mozesz zajmowac teren miast ktore maja zbyt malo energii."));
		helpPages.add(pageLines);
		
		pageLines = new ArrayList<String>();
                pageLines.add(p.txt.parse("<i>Tylko czlonkowie miasta moga niszczyc i budowac na swoim"));
                pageLines.add(p.txt.parse("<i>terytorium. Tak samo jest z korzystaniem z nastepujacych:"));
                pageLines.add(p.txt.parse("<i>przedmiotow: Drzwi, Skrzynki, Piece, Dozowniki, Diode."));
                pageLines.add("");
                pageLines.add(p.txt.parse("<i>Upewnij sie, ze polozyles plytki nasickowe przed drzwiami dla"));
                pageLines.add(p.txt.parse("<i>twoich gosci. Inaczej nie przejda. Mozesz to"));
                pageLines.add(p.txt.parse("<i>wykorzystac do tworzenia miejsc tylko dla czlonkow."));
                pageLines.add(p.txt.parse("<i>Jako ze dozowniki sa chronione, mozesz tworzyc z nich"));
                pageLines.add(p.txt.parse("<i>pulapki, bez obawy ze ktos ukradnie ci ich zawartosc."));
		helpPages.add(pageLines);
		
                pageLines = new ArrayList<String>();
                pageLines.add("W koncu troszke komend dla adminow serwera:");
                pageLines.add( p.cmdBase.cmdBypass.getUseageTemplate(true) );
                pageLines.add(p.txt.parse("<c>/f claim safezone <i>zajmuje teren na Strefe bezpieczna"));
                pageLines.add(p.txt.parse("<c>/f claim warzone <i>zajmuje teren na Strefe walki"));
                pageLines.add(p.txt.parse("<c>/f autoclaim [safezone|warzone] <i>zgadnij ;)"));
                pageLines.add( p.cmdBase.cmdSafeunclaimall.getUseageTemplate(true) );
                pageLines.add( p.cmdBase.cmdWarunclaimall.getUseageTemplate(true) );
                pageLines.add(p.txt.parse("<i>Notka: " + p.cmdBase.cmdUnclaim.getUseageTemplate(false) + P.p.txt.parse("<i>") + " dziala takze na strefach Bezpiecznych/Walki."));
                pageLines.add( p.cmdBase.cmdPeaceful.getUseageTemplate(true) );
                helpPages.add(pageLines);
		
		pageLines = new ArrayList<String>();
                pageLines.add(p.txt.parse("<i>Wiecej komend dla adminow:"));
		pageLines.add( p.cmdBase.cmdChatSpy.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdPermanent.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdPermanentPower.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdPowerBoost.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdConfig.getUseageTemplate(true) );
		helpPages.add(pageLines);
		
		pageLines = new ArrayList<String>();
                pageLines.add(p.txt.parse("<i>I jeszcze wiecej komend dla tych Panow:"));
		pageLines.add( p.cmdBase.cmdLock.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdReload.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdSaveAll.getUseageTemplate(true) );
		pageLines.add( p.cmdBase.cmdVersion.getUseageTemplate(true) );
		helpPages.add(pageLines);
	}
}

