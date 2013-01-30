package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;

public class CmdAutoClaim extends FCommand
{
	public CmdAutoClaim()
	{
		super();
		this.aliases.add("autoclaim");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("faction", "your");
		
		this.permission = Permission.AUTOCLAIM.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform()
	{
		Faction forFaction = this.argAsFaction(0, myFaction);
		if (forFaction == null || forFaction == fme.getAutoClaimFor())
		{
			fme.setAutoClaimFor(null);
			msg("<i>Auto-zajmowanie terenu wylaczone.");
			return;
		}

		if (! fme.canClaimForFaction(forFaction))
		{
			if (myFaction == forFaction)
				msg("<b>Musisz byc <h>%s<b> aby zajmowac teren.", Role.MODERATOR.toString());
			else
				msg("<b>Nie mozesz zajac terenu dla <h>%s<b>.", forFaction.describeTo(fme));

			return;
		}
		
		fme.setAutoClaimFor(forFaction);
		
		msg("<i>Automatyczne zajmowanie terenu dla <h>%s<i>.", forFaction.describeTo(fme));
		fme.attemptClaim(forFaction, me.getLocation(), true);
	}
	
}