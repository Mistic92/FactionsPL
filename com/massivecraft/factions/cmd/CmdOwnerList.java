package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.struct.Permission;


public class CmdOwnerList extends FCommand
{
	
	public CmdOwnerList()
	{
		super();
		this.aliases.add("ownerlist");
		
		//this.requiredArgs.add("");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.OWNERLIST.node;
		this.disableOnLock = false;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		boolean hasBypass = fme.isAdminBypassing(); 

		if ( ! hasBypass && ! assertHasFaction())
		{
			return;
		}

		if ( ! Conf.ownedAreasEnabled)
		{
			fme.msg("<b>Zajmowanie terenu jest wylaczone.");
			return;
		}

		FLocation flocation = new FLocation(fme);

		if (Board.getFactionAt(flocation) != myFaction)
		{
			if (!hasBypass)
			{
				fme.msg("<b>Ten teren nie jest zajety przez twoje miasto.");
				return;
			}

			myFaction = Board.getFactionAt(flocation);
			if (!myFaction.isNormal())
			{
				fme.msg("<i>Ten teren nie jest zajety przez zadne miasto wiec nie ma wlascicieli.");
				return;
			}
		}

		String owners = myFaction.getOwnerListString(flocation);

		if (owners == null || owners.isEmpty())
		{
			fme.msg("<i>Brak wlascicieli; kazdy z miasta ma dostep.");
			return;
		}

		fme.msg("<i>Aktualny/(i) wlasciciel(e) terenu %s", owners);
	}
}
