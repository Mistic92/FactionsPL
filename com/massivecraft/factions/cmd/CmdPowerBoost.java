package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;

public class CmdPowerBoost extends FCommand
{
	public CmdPowerBoost()
	{
		super();
		this.aliases.add("powerboost");
		
		this.requiredArgs.add("p|f|player|faction");
		this.requiredArgs.add("name");
		this.requiredArgs.add("#");
		
		this.permission = Permission.POWERBOOST.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		String type = this.argAsString(0).toLowerCase();
		boolean doPlayer = true;
		if (type.equals("f") || type.equals("faction"))
		{
			doPlayer = false;
		}
		else if (!type.equals("p") && !type.equals("player"))
		{
			msg("<b>Musisz okreslic \"p\" lub \"player\" aby wybrac gracza lub \"f\" badz \"faction\" aby wybrac miasto.");
			msg("<b>ex. /f powerboost p JakisGracz 0.5  -lub-  /f powerboost f JakiesMiasto -5");
			return;
		}
		
		Double targetPower = this.argAsDouble(2);
		if (targetPower == null)
		{
			msg("<b>Musisz okreslic poprawna wartosc liczbowa dla bonusu/kary do energi.");
			return;
		}

		String target;

		if (doPlayer)
		{
			FPlayer targetPlayer = this.argAsBestFPlayerMatch(1);
			if (targetPlayer == null) return;
			targetPlayer.setPowerBoost(targetPower);
			target = "Gracz \""+targetPlayer.getName()+"\"";
		}
		else
		{
			Faction targetFaction = this.argAsFaction(1);
			if (targetFaction == null) return;
			targetFaction.setPowerBoost(targetPower);
			target = "Miasto \""+targetFaction.getTag()+"\"";
		}

		msg("<i>"+target+" ma teraz bonus/kare "+targetPower+" do min/max poziomu energi.");
		if (!senderIsConsole)
			P.p.log(fme.getName()+" has set the power bonus/penalty for "+target+" to "+targetPower+".");
	}
}
