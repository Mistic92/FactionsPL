package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.struct.Permission;

public class CmdOpen extends FCommand
{
	public CmdOpen()
	{
		super();
		this.aliases.add("open");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("yes/no", "flip");
		
		this.permission = Permission.OPEN.node;
		this.disableOnLock = false;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = true;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if ( ! payForCommand(Conf.econCostOpen, "aby otworzyc lub zamknac miasto", "do otwierania lub zamykania miast")) return;

		myFaction.setOpen(this.argAsBool(0, ! myFaction.getOpen()));
		
		String open = myFaction.getOpen() ? "otwarte" : "zamkniete";
		
		// Inform
		myFaction.msg("%s<i> zmienil status miasta na <h>%s<i>.", fme.describeTo(myFaction, true), open);
		for (Faction faction : Factions.i.get())
		{
			if (faction == myFaction)
			{
				continue;
			}
			faction.msg("<i>Miasto %s<i> jest teraz %s", myFaction.getTag(faction), open);
		}
	}
	
}
