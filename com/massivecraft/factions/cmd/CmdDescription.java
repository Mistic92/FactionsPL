package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TextUtil;

public class CmdDescription extends FCommand
{
	public CmdDescription()
	{
		super();
		this.aliases.add("desc");
		
		this.requiredArgs.add("desc");
		this.errorOnToManyArgs = false;
		//this.optionalArgs
		
		this.permission = Permission.DESCRIPTION.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = true;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if ( ! payForCommand(Conf.econCostDesc, "aby zmienic opis miasta", "do zmiany opisu miasta")) return;

		myFaction.setDescription(TextUtil.implode(args, " ").replaceAll("(&([a-f0-9]))", "& $2"));  // since "&" color tags seem to work even through plain old FPlayer.sendMessage() for some reason, we need to break those up

		if ( ! Conf.broadcastDescriptionChanges)
		{
			fme.msg("Zmieniles opis miasta <h>%s<i> na:", myFaction.describeTo(fme));
			fme.sendMessage(myFaction.getDescription());
			return;
		}

		// Broadcast the description to everyone
		for (FPlayer fplayer : FPlayers.i.getOnline())
		{
			fplayer.msg("<i>Miasto %s<i> zmienilo swoj opis na:", myFaction.describeTo(fplayer));
			fplayer.sendMessage(myFaction.getDescription());  // players can inject "&" or "`" or "<i>" or whatever in their description; &k is particularly interesting looking
		}
	}
	
}