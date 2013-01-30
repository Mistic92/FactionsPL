package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;

public class CmdMod extends FCommand
{
	
	public CmdMod()
	{
		super();
		this.aliases.add("mod");
		
		this.requiredArgs.add("player name");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.MOD.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		FPlayer you = this.argAsBestFPlayerMatch(0);
		if (you == null) return;

		boolean permAny = Permission.MOD_ANY.has(sender, false);
		Faction targetFaction = you.getFaction();

		if (targetFaction != myFaction && !permAny)
		{
			msg("%s<b> nie jest czlonkiem twojego miasta.", you.describeTo(fme, true));
			return;
		}

		if (fme != null && fme.getRole() != Role.ADMIN && !permAny)
		{
			msg("<b>Nie jestes adminem miasta.");
			return;
		}

		if (you == fme && !permAny)
		{
			msg("<b>Nie mozesz byc celem.");
			return;
		}

		if (you.getRole() == Role.ADMIN)
		{
			msg("<b>Wybrany gracz jest adminem miasta, najpierw go zdegraduj.");
			return;
		}

		if (you.getRole() == Role.MODERATOR)
		{
			// Revoke
			you.setRole(Role.NORMAL);
			targetFaction.msg("%s<i> nie jest juz moderatorem miasta.", you.describeTo(targetFaction, true));
			msg("<i>Usunales status moderatora miasta z %s<i>.", you.describeTo(fme, true));
		}
		else
		{
			// Give
			you.setRole(Role.MODERATOR);
			targetFaction.msg("%s<i> zostal awansowany do rangi moderatora miasta.", you.describeTo(targetFaction, true));
			msg("<i>Awansowales %s<i> do moderatora.", you.describeTo(fme, true));
		}
	}
	
}
