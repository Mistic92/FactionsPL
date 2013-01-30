package com.massivecraft.factions.cmd;

import org.bukkit.Bukkit;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.P;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.struct.Permission;

public class CmdJoin extends FCommand
{
	public CmdJoin()
	{
		super();
		this.aliases.add("join");
		
		this.requiredArgs.add("faction name");
		this.optionalArgs.put("player", "you");
		
		this.permission = Permission.JOIN.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		Faction faction = this.argAsFaction(0);
		if (faction == null) return;

		FPlayer fplayer = this.argAsBestFPlayerMatch(1, fme, false);
		boolean samePlayer = fplayer == fme;

		if (!samePlayer  && ! Permission.JOIN_OTHERS.has(sender, false))
		{
			msg("<b>Hah, nie, nie mozesz przenosic graczy do miasta :3");
			return;
		}

		if ( ! faction.isNormal())
		{
			msg("<b>Gracze moga dolaczyc tylko do normalnych miast. Tak dziala ten plugin...");
			return;
		}

		if (faction == fplayer.getFaction())
		{
			msg("<b>%s %s jest juz czlonkiem %s", fplayer.describeTo(fme, true), (samePlayer ? "jest" : "jest"), faction.getTag(fme));
			return;
		}

		if (Conf.factionMemberLimit > 0 && faction.getFPlayers().size() >= Conf.factionMemberLimit)
		{
			msg(" <b>!<white> Miasto %s ma limit %d czlonkow, wiec %s nie mozesz dolaczyc.", faction.getTag(fme), Conf.factionMemberLimit, fplayer.describeTo(fme, false));
			return;
		}

		if (fplayer.hasFaction())
		{
			msg("<b>%s najpierw musisz opuscic %s aktualne miasto.", fplayer.describeTo(fme, true), (samePlayer ? "twoje" : "ich"));
			return;
		}

		if (!Conf.canLeaveWithNegativePower && fplayer.getPower() < 0)
		{
			msg("<b>%s nie mozesz dolaczyc do miasta z ujemnym poziomem energii.", fplayer.describeTo(fme, true));
			return;
		}

		if( ! (faction.getOpen() || faction.isInvited(fplayer) || fme.isAdminBypassing() || Permission.JOIN_ANY.has(sender, false)))
		{
			msg("<i>To miasto wymaga zaproszen.");
			if (samePlayer)
				faction.msg("%s<i> chcial dolaczyc do twojego miasta.", fplayer.describeTo(faction, true));
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make sure they can pay
		if (samePlayer && ! canAffordCommand(Conf.econCostJoin, "aby dolaczyc do miasta")) return;

		// trigger the join event (cancellable)
		FPlayerJoinEvent joinEvent = new FPlayerJoinEvent(FPlayers.i.get(me),faction,FPlayerJoinEvent.PlayerJoinReason.COMMAND);
		Bukkit.getServer().getPluginManager().callEvent(joinEvent);
		if (joinEvent.isCancelled()) return;

		// then make 'em pay (if applicable)
		if (samePlayer && ! payForCommand(Conf.econCostJoin, "aby dolaczyc do miasta", "do dolaczania do miasta")) return;

		fme.msg("<i>%s dolaczyl do %s.", fplayer.describeTo(fme, true), faction.getTag(fme));

		if (!samePlayer)
			fplayer.msg("<i>%s przeniosl cie do miasta %s.", fme.describeTo(fplayer, true), faction.getTag(fplayer));
		faction.msg("<i>%s dolaczyl do twojego miasta.", fplayer.describeTo(faction, true));

		fplayer.resetFactionData();
		fplayer.setFaction(faction);
		faction.deinvite(fplayer);

		if (Conf.logFactionJoin)
		{
			if (samePlayer)
				P.p.log("%s dolaczyl do miasta %s.", fplayer.getName(), faction.getTag());
			else
				P.p.log("%s przeniosl %s do miasta %s.", fme.getName(), fplayer.getName(), faction.getTag());
		}
	}
}
