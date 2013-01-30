package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Permission;

public class CmdInvite extends FCommand
{
	public CmdInvite()
	{
		super();
		this.aliases.add("invite");
		this.aliases.add("inv");
		
		this.requiredArgs.add("player name");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.INVITE.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = true;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		FPlayer you = this.argAsBestFPlayerMatch(0);
		if (you == null) return;
		
		if (you.getFaction() == myFaction)
		{
			msg("%s<i> jest juz czlonkiem %s", you.getName(), myFaction.getTag());
			msg("<i>Czy chodzilo ci o: " +  p.cmdBase.cmdKick.getUseageTemplate(false));
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if ( ! payForCommand(Conf.econCostInvite, "aby kogos zaprosic", "do zapraszania")) return;

		myFaction.invite(you);
		
		you.msg("%s<i> zaprosil cie do %s", fme.describeTo(you, true), myFaction.describeTo(you));
		myFaction.msg("%s<i> zaprosil %s<i> do miasta.", fme.describeTo(myFaction, true), you.describeTo(myFaction));
	}
	
}
