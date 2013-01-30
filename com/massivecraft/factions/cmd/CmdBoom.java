package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.struct.Permission;

public class CmdBoom extends FCommand
{
	public CmdBoom()
	{
		super();
		this.aliases.add("noboom");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("on/off", "flip");
		
		this.permission = Permission.NO_BOOM.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = true;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform()
	{
		if ( ! myFaction.isPeaceful())
		{
			fme.msg("<b>Ta komenda ma uzytek tylko dla miast ktore zostaly stworzone jako pokojowe.");
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if ( ! payForCommand(Conf.econCostNoBoom, "aby wlaczyc eksplozje", "do wlaczenia eksplozji")) return;

		myFaction.setPeacefulExplosionsEnabled(this.argAsBool(0, ! myFaction.getPeacefulExplosionsEnabled()));

		String enabled = myFaction.noExplosionsInTerritory() ? "wylaczone" : "wlaczone";

		// Inform
		myFaction.msg("%s<i> ma "+enabled+" eksplozje na terenie miasta.", fme.describeTo(myFaction));
	}
}
