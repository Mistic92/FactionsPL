package com.massivecraft.factions.cmd;

import org.bukkit.Bukkit;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.event.FactionDisbandEvent;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;


public class CmdDisband extends FCommand
{
	public CmdDisband()
	{
		super();
		this.aliases.add("disband");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("faction tag", "yours");
		
		this.permission = Permission.DISBAND.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		// The faction, default to your own.. but null if console sender.
		Faction faction = this.argAsFaction(0, fme == null ? null : myFaction);
		if (faction == null) return;
		
		boolean isMyFaction = fme == null ? false : faction == myFaction;
		
		if (isMyFaction)
		{
			if ( ! assertMinRole(Role.ADMIN)) return;
		}
		else
		{
			if ( ! Permission.DISBAND_ANY.has(sender, true))
			{
				return;
			}
		}

		if (! faction.isNormal())
		{
			msg("<i>Nie mozesz rozwiazac dziczy, strefy bezpiecznej lub strefy walki.");
			return;
		}
		if (faction.isPermanent())
		{
			msg("<i>To miasto jest ustawione jako wieczne, nie mozna go rozwiazac!");
			return;
		}

		FactionDisbandEvent disbandEvent = new FactionDisbandEvent(me, faction.getId());
		Bukkit.getServer().getPluginManager().callEvent(disbandEvent);
		if(disbandEvent.isCancelled()) return;

		// Send FPlayerLeaveEvent for each player in the faction
		for ( FPlayer fplayer : faction.getFPlayers() )
		{
			Bukkit.getServer().getPluginManager().callEvent(new FPlayerLeaveEvent(fplayer, faction, FPlayerLeaveEvent.PlayerLeaveReason.DISBAND));
		}

		// Inform all players
		for (FPlayer fplayer : FPlayers.i.getOnline())
		{
			String who = senderIsConsole ? "Burmistrz" : fme.describeTo(fplayer);
			if (fplayer.getFaction() == faction)
			{
				fplayer.msg("<h>%s<i> rozwiazal twoje miasto.", who);
			}
			else
			{
				fplayer.msg("<h>%s<i> rozwiazal miasto %s.", who, faction.getTag(fplayer));
			}
		}
		if (Conf.logFactionDisband)
			P.p.log("Miasto "+faction.getTag()+" ("+faction.getId()+") zostalo rozwiazane przez "+(senderIsConsole ? "konsola" : fme.getName())+".");

		if (Econ.shouldBeUsed() && ! senderIsConsole)
		{
			//Give all the faction's money to the disbander
			double amount = Econ.getBalance(faction.getAccountId());
			Econ.transferMoney(fme, faction, fme, amount, false);
			
			if (amount > 0.0)
			{
				String amountString = Econ.moneyString(amount);
				msg("<i>Otrzymales %s pieniedzy z rozwiazanego miasta.", amountString);
				P.p.log(fme.getName() + " otrzymal "+amountString+" pieniedzy z banku "+faction.getTag()+" po jego rozwiazaniu.");
		}
		}		
		
		faction.detach();

		SpoutFeatures.updateAppearances();
	}
}
