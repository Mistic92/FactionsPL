package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;

public class CmdWarunclaimall extends FCommand
{
	
	public CmdWarunclaimall()
	{
		this.aliases.add("warunclaimall");
		this.aliases.add("wardeclaimall");
		
		//this.requiredArgs.add("");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.MANAGE_WAR_ZONE.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
		
		this.setHelpShort("unclaim all warzone land");
	}
	
	@Override
	public void perform()
	{
		Board.unclaimAll(Factions.i.getWarZone().getId());
		msg("<i>Zwolniles CALY teren pola walki.");

		if (Conf.logLandUnclaims)
			P.p.log(fme.getName()+" zwolnil caly teren pola walki.");
	}
	
}
