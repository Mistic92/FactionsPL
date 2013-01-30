package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;


public class CmdOwner extends FCommand
{
	
	public CmdOwner()
	{
		super();
		this.aliases.add("owner");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("player name", "you");
		
		this.permission = Permission.OWNER.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}
	
	// TODO: Fix colors!
	
	@Override
	public void perform()
	{
		boolean hasBypass = fme.isAdminBypassing();
		
		if ( ! hasBypass && ! assertHasFaction()) {
			return;
		}

		if ( ! Conf.ownedAreasEnabled)
		{
			fme.msg("<b>Wybacz, wlasne tereny sa wylaczone.");
			return;
		}

		if ( ! hasBypass && Conf.ownedAreasLimitPerFaction > 0 && myFaction.getCountOfClaimsWithOwners() >= Conf.ownedAreasLimitPerFaction)
		{
			fme.msg("<b>Osiagnales serwerowy limit<h> %d <b> chunkow zajetego terenu na jedno miasto.", Conf.ownedAreasLimitPerFaction);
			return;
		}

		if ( ! hasBypass && !assertMinRole(Conf.ownedAreasModeratorsCanSet ? Role.MODERATOR : Role.ADMIN))
		{
			return;
		}

		FLocation flocation = new FLocation(fme);

		Faction factionHere = Board.getFactionAt(flocation);
		if (factionHere != myFaction)
		{
			if ( ! hasBypass)
			{
				fme.msg("<b>Ten teren nie nalezy do twojego miasta wiec nie mozesz zostac jego wlascicielem.");
				return;
			}

			if ( ! factionHere.isNormal())
			{
				fme.msg("<b>Ten teren nie nalezy do twojego miasta, nie mozesz zostac jego wlascicielem.");
				return;
			}
		}

		FPlayer target = this.argAsBestFPlayerMatch(0, fme);
		if (target == null) return;

		String playerName = target.getName();

		if (target.getFaction() != myFaction)
		{
			fme.msg("%s<i> nie jest czlonkiem twojego miasta.", playerName);
			return;
		}

		// if no player name was passed, and this claim does already have owners set, clear them
		if (args.isEmpty() && myFaction.doesLocationHaveOwnersSet(flocation))
		{
			myFaction.clearClaimOwnership(flocation);
			SpoutFeatures.updateOwnerListLoc(flocation);
			fme.msg("<i>Nie jestes juz wlascicielem tego terenu.");
			return;
		}

		if (myFaction.isPlayerInOwnerList(playerName, flocation))
		{
			myFaction.removePlayerAsOwner(playerName, flocation);
			SpoutFeatures.updateOwnerListLoc(flocation);
			fme.msg("<i>Zabrales prawa do terenu mieszkancowi %s<i>.", playerName);
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if ( ! payForCommand(Conf.econCostOwner, "aby ustawic wlasciciela terenu", "do ustawienia wlasciciela terenu")) return;

		myFaction.setPlayerAsOwner(playerName, flocation);
		SpoutFeatures.updateOwnerListLoc(flocation);

		fme.msg("<i>Dodales %s<i> jako wlasciciela tego terenu.", playerName);
	}
}
