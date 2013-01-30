package com.massivecraft.factions.cmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.integration.EssentialsFeatures;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.util.SmokeUtil;


public class CmdHome extends FCommand
{
	
	public CmdHome()
	{
		super();
		this.aliases.add("home");
		
		//this.requiredArgs.add("");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.HOME.node;
		this.disableOnLock = false;
		
		senderMustBePlayer = true;
		senderMustBeMember = true;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		// TODO: Hide this command on help also.
		if ( ! Conf.homesEnabled)
		{
			fme.msg("<b>Wybacz, spawn miasta jest wylaczony na tym serwerze.");
			return;
		}

		if ( ! Conf.homesTeleportCommandEnabled)
		{
			fme.msg("<b>Wybacz, mozliwosc teleportacji do miasta jest wylaczona.");
			return;
		}
		
		if ( ! myFaction.hasHome())
		{
			fme.msg("<b>Twoje miasto nie ma ustawionego spawnu. " + (fme.getRole().value < Role.MODERATOR.value ? "<i> Powiadom o tym burmistrza:" : "<i>Powinienes wpisac:"));
			fme.sendMessage(p.cmdBase.cmdSethome.getUseageTemplate());
			return;
		}
		
		if ( ! Conf.homesTeleportAllowedFromEnemyTerritory && fme.isInEnemyTerritory())
		{
			fme.msg("<b>Nie mozesz teleportowac sie na spawn miasta kiedy jestes na terytorium wroga.");
			return;
		}
		
		if ( ! Conf.homesTeleportAllowedFromDifferentWorld && me.getWorld().getUID() != myFaction.getHome().getWorld().getUID())
		{
			fme.msg("<b>Nie mozesz teleportowac sie na spawn miasta kiedy jestes na innej mapie!");
			return;
		}
		
		Faction faction = Board.getFactionAt(new FLocation(me.getLocation()));
		Location loc = me.getLocation().clone();
		
		// if player is not in a safe zone or their own faction territory, only allow teleport if no enemies are nearby
		if
		(
			Conf.homesTeleportAllowedEnemyDistance > 0
			&&
			! faction.isSafeZone()
			&&
			(
				! fme.isInOwnTerritory()
				||
				(
					fme.isInOwnTerritory()
					&&
					! Conf.homesTeleportIgnoreEnemiesIfInOwnTerritory
				)
			)
		)
		{
			World w = loc.getWorld();
			double x = loc.getX();
			double y = loc.getY();
			double z = loc.getZ();

			for (Player p : me.getServer().getOnlinePlayers())
			{
				if (p == null || !p.isOnline() || p.isDead() || p == me || p.getWorld() != w)
					continue;

				FPlayer fp = FPlayers.i.get(p);
				if (fme.getRelationTo(fp) != Relation.ENEMY)
					continue;

				Location l = p.getLocation();
				double dx = Math.abs(x - l.getX());
				double dy = Math.abs(y - l.getY());
				double dz = Math.abs(z - l.getZ());
				double max = Conf.homesTeleportAllowedEnemyDistance;

				// box-shaped distance check
				if (dx > max || dy > max || dz > max)
					continue;

				fme.msg("<b>Nie mozesz teleportowac sie na spawn miasta kiedy wrog jest w odleglosci mniejszej niz " + Conf.homesTeleportAllowedEnemyDistance + " blokow od ciebie.");
				return;
			}
		}

		// if Essentials teleport handling is enabled and available, pass the teleport off to it (for delay and cooldown)
		if (EssentialsFeatures.handleTeleport(me, myFaction.getHome())) return;

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if ( ! payForCommand(Conf.econCostHome, "aby przeniesc sie na spawn miasta", "do przenoszenia sie na spawn miasta")) return;

		// Create a smoke effect
		if (Conf.homesTeleportCommandSmokeEffectEnabled)
		{
			List<Location> smokeLocations = new ArrayList<Location>();
			smokeLocations.add(loc);
			smokeLocations.add(loc.add(0, 1, 0));
			smokeLocations.add(myFaction.getHome());
			smokeLocations.add(myFaction.getHome().clone().add(0, 1, 0));
			SmokeUtil.spawnCloudRandom(smokeLocations, Conf.homesTeleportCommandSmokeEffectThickness);
		}
		
		me.teleport(myFaction.getHome());
	}
	
}
