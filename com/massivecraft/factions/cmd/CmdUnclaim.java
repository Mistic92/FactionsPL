package com.massivecraft.factions.cmd;

import org.bukkit.Bukkit;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.event.LandUnclaimEvent;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;

public class CmdUnclaim extends FCommand
{
	public CmdUnclaim()
	{
		this.aliases.add("unclaim");
		this.aliases.add("declaim");
		
		//this.requiredArgs.add("");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.UNCLAIM.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		FLocation flocation = new FLocation(fme);
		Faction otherFaction = Board.getFactionAt(flocation);
		
		if (otherFaction.isSafeZone())
		{
			if (Permission.MANAGE_SAFE_ZONE.has(sender))
			{
				Board.removeAt(flocation);
				SpoutFeatures.updateTerritoryDisplayLoc(flocation);
				msg("<i>Strefa bezpieczna zostala zwolniona.");

				if (Conf.logLandUnclaims)
					P.p.log(fme.getName()+" zwolniono teren na ("+flocation.getCoordString()+") z miasta: "+otherFaction.getTag());
			}
			else
			{
				msg("<b>To jest strefa bezpieczna. Potrzebujesz permisji do zwolnienia tego terenu.");
			}
			return;
		}
		else if (otherFaction.isWarZone())
		{
			if (Permission.MANAGE_WAR_ZONE.has(sender))
			{
				Board.removeAt(flocation);
				SpoutFeatures.updateTerritoryDisplayLoc(flocation);
				msg("<i>Strefa walki zostala usunieta.");

				if (Conf.logLandUnclaims)
					P.p.log(fme.getName()+" zwalniono teren ("+flocation.getCoordString()+") z miasta: "+otherFaction.getTag());
			}
			else
			{
				msg("<b>To jest strefa walki. Potrzebujesz permisji do zwolnienia tego terenu.");
			}
			return;
		}
		
		if (fme.isAdminBypassing())
		{
			Board.removeAt(flocation);
			SpoutFeatures.updateTerritoryDisplayLoc(flocation);

			otherFaction.msg("%s<i> zwolnil kawalek twojego terenu.", fme.describeTo(otherFaction, true));
			msg("<i>Zwolniles ten teren.");

			if (Conf.logLandUnclaims)
				P.p.log(fme.getName()+" wycofal teren na ("+flocation.getCoordString()+") z miasta: "+otherFaction.getTag());

			return;
		}
		
		if ( ! assertHasFaction())
		{
			return;
		}
		
		if ( ! assertMinRole(Role.MODERATOR))
		{
			return;
		}
		
		
		if ( myFaction != otherFaction)
		{
			msg("<b>To nie jest twoj teren.");
			return;
		}

		LandUnclaimEvent unclaimEvent = new LandUnclaimEvent(flocation, otherFaction, fme);
		Bukkit.getServer().getPluginManager().callEvent(unclaimEvent);
		if(unclaimEvent.isCancelled()) return;

		if (Econ.shouldBeUsed())
		{
			double refund = Econ.calculateClaimRefund(myFaction.getLandRounded());
			
			if(Conf.bankEnabled && Conf.bankFactionPaysLandCosts)
			{
				if ( ! Econ.modifyMoney(myFaction, refund, "aby zwolnic ten teren", "do zwolnienia tego terenu")) return;
			}
			else
			{
				if ( ! Econ.modifyMoney(fme      , refund, "aby zwolnic ten teren", "do zwolnienia tego terenu")) return;
			}
		}

		Board.removeAt(flocation);
		SpoutFeatures.updateTerritoryDisplayLoc(flocation);
		myFaction.msg("%s<i> zwolnil kawalek terenu.", fme.describeTo(myFaction, true));

		if (Conf.logLandUnclaims)
			P.p.log(fme.getName()+" zwolnil teren na ("+flocation.getCoordString()+") z miasta: "+otherFaction.getTag());
	}
	
}
