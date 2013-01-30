package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;

public class CmdSethome extends FCommand
{
	public CmdSethome()
	{
		this.aliases.add("sethome");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("faction tag", "mine");
		
		this.permission = Permission.SETHOME.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		if ( ! Conf.homesEnabled)
		{
			fme.msg("<b>Wybacz, spawny miast sa wylaczone.");
			return;
		}
		
		Faction faction = this.argAsFaction(0, myFaction);
		if (faction == null) return;
		
		// Can the player set the home for this faction?
		if (faction == myFaction)
		{
			if ( ! Permission.SETHOME_ANY.has(sender) && ! assertMinRole(Role.MODERATOR)) return;
		}
		else
		{
			if ( ! Permission.SETHOME_ANY.has(sender, true)) return;
		}
		
		// Can the player set the faction home HERE?
		if
		(
			! Permission.BYPASS.has(me)
			&&
			Conf.homesMustBeInClaimedTerritory
			&& 
			Board.getFactionAt(new FLocation(me)) != faction
		)
		{
			fme.msg("<b>Wybacz, spawn miasta moze byc ustawiony tylko wewnatrz zajetego regionu.");
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if ( ! payForCommand(Conf.econCostSethome, "aby ustawic spawn miasta", "do ustawiania spawnu miasta")) return;

		faction.setHome(me.getLocation());
		
		faction.msg("%s<i> ustawil spawn miasta.Teraz mozesz uzyc:", fme.describeTo(myFaction, true));
		faction.sendMessage(p.cmdBase.cmdHome.getUseageTemplate());
		if (faction != myFaction)
		{
			fme.msg("<b>Ustawiles spawn miasta dla "+faction.getTag(fme));
		}
	}
	
}
