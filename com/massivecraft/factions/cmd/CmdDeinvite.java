package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Permission;

public class CmdDeinvite extends FCommand
{
	
	public CmdDeinvite()
	{
		super();
		this.aliases.add("deinvite");
		this.aliases.add("deinv");
		
		this.requiredArgs.add("player name");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.DEINVITE.node;
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
			msg("<i>Czy chodzilo ci o: %s", p.cmdBase.cmdKick.getUseageTemplate(false));
			return;
		}
		
		myFaction.deinvite(you);
		
		you.msg("%s<i> odnowil twoje zaproszenie do <h>%s<i>.", fme.describeTo(you), myFaction.describeTo(you));
		
		myFaction.msg("%s<i> odnowil %s's<i> zaproszenie.", fme.describeTo(myFaction), you.describeTo(myFaction));
	}
	
}
