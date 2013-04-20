package com.massivecraft.factions.cmd;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.event.FactionRelationEvent;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;

public abstract class FRelationCommand extends FCommand
{
	public Relation targetRelation;
	
	public FRelationCommand()
	{
		super();
		this.requiredArgs.add("faction tag");
		//this.optionalArgs.put("player name", "you");
		
		this.permission = Permission.RELATION.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = true;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		Faction them = this.argAsFaction(0);
		if (them == null) return;
		
		if ( ! them.isNormal())
		{
			msg("<b>Nope! Nie mozesz :D");
			return;
		}
		
		if (them == myFaction)
		{
			msg("<b>Nope! Nie mozesz zdefiniowac relacji do siebie :)");
			return;
		}

		if (myFaction.getRelationWish(them) == targetRelation)
		{
			msg("<b>Ta relacja jest juz ustawiona do %s.", them.getTag());
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if ( ! payForCommand(targetRelation.getRelationCost(), "aby zmienic relacje", "do zmiany relacji")) return;

		// try to set the new relation
		Relation oldRelation = myFaction.getRelationTo(them, true);
		myFaction.setRelationWish(them, targetRelation);
		Relation currentRelation = myFaction.getRelationTo(them, true);
		ChatColor currentRelationColor = currentRelation.getColor();

		// if the relation change was successful
		if (targetRelation.value == currentRelation.value)
		{
			// trigger the faction relation event
			FactionRelationEvent relationEvent = new FactionRelationEvent(myFaction, them, oldRelation, currentRelation);
			Bukkit.getServer().getPluginManager().callEvent(relationEvent);
                        if(targetRelation.toString()=="sojusznik") //sprawdzenie czy sojusznik
                        {
			them.msg("<i>Twoje miasto jest teraz "+
                                currentRelationColor+
                                "w sojuszu z "+
                                currentRelationColor+
                                myFaction.getTag());
			myFaction.msg("<i>Twoje miasto jest teraz w "+
                                currentRelationColor+
                                "w sojuszu z "+
                                currentRelationColor+them.getTag());
                        }//sprawdzenie czy sojusznik
                        else//jesli wrog
                        {
			them.msg("<i>Twoje miasto jest teraz "+
                                currentRelationColor+
                                "wrogiem "+
                                currentRelationColor+
                                myFaction.getTag());
			myFaction.msg("<i>Twoje miasto jest teraz "+
                                currentRelationColor+
                                "wrogiem "+
                                currentRelationColor+them.getTag());                            
                        }//jesli wrog
		}
		// inform the other faction of your request
		else
		{
			them.msg(currentRelationColor+myFaction.getTag()+"<i> chcialby byc twoim "+targetRelation.getColor()+targetRelation.toString());
			them.msg("<i>Wpisz <c>/f ally "+myFaction.getTag()+"<i> aby zaakceptowac.");
			myFaction.msg(currentRelationColor+them.getTag()+"<i> zostal poinformowany o tym ze chcesz byc "+targetRelation.getColor()+targetRelation);
		}
		
		if ( ! targetRelation.isNeutral() && them.isPeaceful())
		{
			them.msg("<i>To nie ma zadnego efektu gdy twoje miasto ma status pokojowy.");
			myFaction.msg("<i>To nie bedzie miec zadnego efektu gdy ich miasto ma status pokojowy.");
		}
		
		if ( ! targetRelation.isNeutral() && myFaction.isPeaceful())
		{
			them.msg("<i>To nie bedzie miec zadnego efektu gdy ich miasto ma status pokojowy.");
			myFaction.msg("<i>To nie ma zadnego efektu gdy twoje miasto ma status pokojowy.");
		}

		SpoutFeatures.updateAppearances(myFaction, them);
		SpoutFeatures.updateTerritoryDisplayLoc(null);
	}
}
