package com.massivecraft.factions.cmd;

import org.bukkit.Bukkit;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;

public class CmdAdmin extends FCommand
{	
	public CmdAdmin()
	{
		super();
		this.aliases.add("admin");
		
		this.requiredArgs.add("player name");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.ADMIN.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		FPlayer fyou = this.argAsBestFPlayerMatch(0);
		if (fyou == null) return;

		boolean permAny = Permission.ADMIN_ANY.has(sender, false);
		Faction targetFaction = fyou.getFaction();

		if (targetFaction != myFaction && !permAny)
		{
			msg("%s<i> nie jest czlonkiem twojego miasta.", fyou.describeTo(fme, true));
			return;
		}

		if (fme != null && fme.getRole() != Role.ADMIN && !permAny)
		{
			msg("<b>Nie jestes burmistrzem!");
			return;
		}

		if (fyou == fme && !permAny)
		{
			msg("<b>Nie mozesz byc celem, to tak jakbys na siebie naplul...");
			return;
		}

		// only perform a FPlayerJoinEvent when newLeader isn't actually in the faction
		if (fyou.getFaction() != targetFaction)
		{
			FPlayerJoinEvent event = new FPlayerJoinEvent(FPlayers.i.get(me),targetFaction,FPlayerJoinEvent.PlayerJoinReason.LEADER);
			Bukkit.getServer().getPluginManager().callEvent(event);
			if (event.isCancelled()) return;
		}

		FPlayer admin = targetFaction.getFPlayerAdmin();

		// if target player is currently admin, demote and replace him
		if (fyou == admin)
		{
			targetFaction.promoteNewLeader();
			msg("<i>Zdegradowales %s<i> z pozycji burmistrza.", fyou.describeTo(fme, true));
			fyou.msg("<i>Zostales zdegradowany z pozycji burmistrza przez %s<i>.", senderIsConsole ? "czerwonego uja aka wlasciciel serwera" : fme.describeTo(fyou, true));
			return;
		}

		// promote target player, and demote existing admin if one exists
		if (admin != null)
			admin.setRole(Role.MODERATOR);
		fyou.setRole(Role.ADMIN);
		msg("<i>Awansowales %s<i> do pozycji burmistrza!", fyou.describeTo(fme, true));

		// Inform all players
		for (FPlayer fplayer : FPlayers.i.getOnline())
		{
			fplayer.msg("%s<i> dal %s<i>  przywodztwo nad miastem %s<i>.", senderIsConsole ? "Czerwony goblin" : fme.describeTo(fplayer, true), fyou.describeTo(fplayer), targetFaction.describeTo(fplayer));
		}
	}
	
}
