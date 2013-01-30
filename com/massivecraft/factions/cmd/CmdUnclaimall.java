package com.massivecraft.factions.cmd;

import org.bukkit.Bukkit;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.P;
import com.massivecraft.factions.event.LandUnclaimAllEvent;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.struct.Permission;

public class CmdUnclaimall extends FCommand
{	
	public CmdUnclaimall()
	{
		this.aliases.add("unclaimall");
		this.aliases.add("declaimall");
		
		//this.requiredArgs.add("");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.UNCLAIM_ALL.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = true;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		if (Econ.shouldBeUsed())
		{
			double refund = Econ.calculateTotalLandRefund(myFaction.getLandRounded());
			if(Conf.bankEnabled && Conf.bankFactionPaysLandCosts)
			{
				if ( ! Econ.modifyMoney(myFaction, refund, "aby zwolnic caly teren miasta", "do zwolnienia calego terenu miasta")) return;
			}
			else
			{
				if ( ! Econ.modifyMoney(fme      , refund, "aby zwolnic caly teren miasta", "do zwolnienia calego terenu miasta")) return;
			}
		}

		LandUnclaimAllEvent unclaimAllEvent = new LandUnclaimAllEvent(myFaction, fme);
		Bukkit.getServer().getPluginManager().callEvent(unclaimAllEvent);
		// this event cannot be cancelled

		Board.unclaimAll(myFaction.getId());
		myFaction.msg("%s<i> zwolnil CALY teren miasta.", fme.describeTo(myFaction, true));
		SpoutFeatures.updateTerritoryDisplayLoc(null);

		if (Conf.logLandUnclaims)
			P.p.log(fme.getName()+" zwolnil caly teren miasta: "+myFaction.getTag());
	}
	
}
