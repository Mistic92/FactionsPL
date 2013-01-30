package com.massivecraft.factions.cmd;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.MCommand;


public abstract class FCommand extends MCommand<P>
{
	public boolean disableOnLock;
	
	public FPlayer fme;
	public Faction myFaction;
	public boolean senderMustBeMember;
	public boolean senderMustBeModerator;
	public boolean senderMustBeAdmin;
	
	public boolean isMoneyCommand;
	
	public FCommand()
	{
		super(P.p);
		
		// Due to safety reasons it defaults to disable on lock.
		disableOnLock = true;
		
		// The money commands must be disabled if money should not be used.
		isMoneyCommand = false;
		
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void execute(CommandSender sender, List<String> args, List<MCommand<?>> commandChain)
	{
		if (sender instanceof Player)
		{
			this.fme = FPlayers.i.get((Player)sender);
			this.myFaction = this.fme.getFaction();
		}
		else
		{
			this.fme = null;
			this.myFaction = null;
		}
		super.execute(sender, args, commandChain);
	}
	
	@Override
	public boolean isEnabled()
	{
		if (p.getLocked() && this.disableOnLock)
		{
			msg("<b>Plugin zostal zablokowany przez admina. Sprobuj ponownie pozniej.");
			return false;
		}
		
		if (this.isMoneyCommand && ! Conf.econEnabled)
		{
			msg("<b>Ekonomia Factions jest wylaczona na tym serwerze.");
			return false;
		}
		
		if (this.isMoneyCommand && ! Conf.bankEnabled)
		{
			msg("<b>System bankow Factions jest wylaczony na tym serwerze.");
			return false;
		}
		
		return true;
	}
	
	@Override
	public boolean validSenderType(CommandSender sender, boolean informSenderIfNot)
	{
		boolean superValid = super.validSenderType(sender, informSenderIfNot);
		if ( ! superValid) return false;
		
		if ( ! (this.senderMustBeMember || this.senderMustBeModerator || this.senderMustBeAdmin)) return true;
		
		if ( ! (sender instanceof Player)) return false;
		
		FPlayer fplayer = FPlayers.i.get((Player)sender);
		
		if ( ! fplayer.hasFaction())
		{
			sender.sendMessage(p.txt.parse("<b>Nie jestes czlonkiem zadnego z miast."));
			return false;
		}
		
		if (this.senderMustBeModerator && ! fplayer.getRole().isAtLeast(Role.MODERATOR))
		{
			sender.sendMessage(p.txt.parse("<b>Tylko moderatorzy miast moga %s.", this.getHelpShort()));
			return false;
		}
		
		if (this.senderMustBeAdmin && ! fplayer.getRole().isAtLeast(Role.ADMIN))
		{
			sender.sendMessage(p.txt.parse("<b>Tylko administratorzy miast moga %s.", this.getHelpShort()));
			return false;
		}
			
		return true;
	}
	
	// -------------------------------------------- //
	// Assertions
	// -------------------------------------------- //

	public boolean assertHasFaction()
	{
		if (me == null) return true;
		
		if ( ! fme.hasFaction())
		{
			sendMessage("Nie jestes czlonkiem zadnego z miast.");
			return false;
		}
		return true;
	}

	public boolean assertMinRole(Role role)
	{
		if (me == null) return true;
		
		if (fme.getRole().value < role.value)
		{
			msg("<b>Musisz byc "+role+"<b> aby "+this.getHelpShort()+".");
			return false;
		}
		return true;
	}
	
	// -------------------------------------------- //
	// Argument Readers
	// -------------------------------------------- //
	
	// FPLAYER ======================
	public FPlayer strAsFPlayer(String name, FPlayer def, boolean msg)
	{
		FPlayer ret = def;
		
		if (name != null)
		{
			FPlayer fplayer = FPlayers.i.get(name);
			if (fplayer != null)
			{
				ret = fplayer;
			}
		}
		
		if (msg && ret == null)
		{
			this.msg("<b>Brak gracza \"<p>%s<b>\" nie mozna go znalezc.", name);			
		}
		
		return ret;
	}
	public FPlayer argAsFPlayer(int idx, FPlayer def, boolean msg)
	{
		return this.strAsFPlayer(this.argAsString(idx), def, msg);
	}
	public FPlayer argAsFPlayer(int idx, FPlayer def)
	{
		return this.argAsFPlayer(idx, def, true);
	}
	public FPlayer argAsFPlayer(int idx)
	{
		return this.argAsFPlayer(idx, null);
	}
	
	// BEST FPLAYER MATCH ======================
	public FPlayer strAsBestFPlayerMatch(String name, FPlayer def, boolean msg)
	{
		FPlayer ret = def;
		
		if (name != null)
		{
			FPlayer fplayer = FPlayers.i.getBestIdMatch(name);
			if (fplayer != null)
			{
				ret = fplayer;
			}
		}
		
		if (msg && ret == null)
		{
			this.msg("<b>Brak pasujacego gracza do \"<p>%s<b>\".", name);
		}
		
		return ret;
	}
	public FPlayer argAsBestFPlayerMatch(int idx, FPlayer def, boolean msg)
	{
		return this.strAsBestFPlayerMatch(this.argAsString(idx), def, msg);
	}
	public FPlayer argAsBestFPlayerMatch(int idx, FPlayer def)
	{
		return this.argAsBestFPlayerMatch(idx, def, true);
	}
	public FPlayer argAsBestFPlayerMatch(int idx)
	{
		return this.argAsBestFPlayerMatch(idx, null);
	}
	
	// FACTION ======================
	public Faction strAsFaction(String name, Faction def, boolean msg)
	{
		Faction ret = def;
		
		if (name != null)
		{
			Faction faction = null;
			
			// First we try an exact match
			if (faction == null)
			{
				faction = Factions.i.getByTag(name);
			}
			
			// Next we match faction tags
			if (faction == null)
			{
				faction = Factions.i.getBestTagMatch(name);
			}
				
			// Next we match player names
			if (faction == null)
			{
				FPlayer fplayer = FPlayers.i.getBestIdMatch(name);
				if (fplayer != null)
				{
					faction = fplayer.getFaction();
				}
			}
			
			if (faction != null)
			{
				ret = faction;
			}
		}
		
		if (msg && ret == null)
		{
			this.msg("<b>Miasto lub gracz \"<p>%s<b>\" nie zostalo znalezione.", name);
		}
		
		return ret;
	}
	public Faction argAsFaction(int idx, Faction def, boolean msg)
	{
		return this.strAsFaction(this.argAsString(idx), def, msg);
	}
	public Faction argAsFaction(int idx, Faction def)
	{
		return this.argAsFaction(idx, def, true);
	}
	public Faction argAsFaction(int idx)
	{
		return this.argAsFaction(idx, null);
	}
	
	// -------------------------------------------- //
	// Commonly used logic
	// -------------------------------------------- //
	
	public boolean canIAdministerYou(FPlayer i, FPlayer you)
	{
		if ( ! i.getFaction().equals(you.getFaction()))
		{
			i.sendMessage(p.txt.parse("%s <b>nie jest w tym samym miescie co ty.",you.describeTo(i, true)));
			return false;
		}
		
		if (i.getRole().value > you.getRole().value || i.getRole().equals(Role.ADMIN) )
		{
			return true;
		}
		
		if (you.getRole().equals(Role.ADMIN))
		{
			i.sendMessage(p.txt.parse("<b>Tylko admin miasta moze to zrobic."));
		}
		else if (i.getRole().equals(Role.MODERATOR))
		{
			if ( i == you )
			{
				return true; //Moderators can control themselves
			}
			else
			{
				i.sendMessage(p.txt.parse("<b>Moderator nie moze kontrolowac innych..."));
			}
		}
		else
		{
			i.sendMessage(p.txt.parse("<b>Musisz byc moderatorem miasta aby to zrobic."));
		}
		
		return false;
	}
	
	// if economy is enabled and they're not on the bypass list, make 'em pay; returns true unless person can't afford the cost
	public boolean payForCommand(double cost, String toDoThis, String forDoingThis)
	{
		if ( ! Econ.shouldBeUsed() || this.fme == null || cost == 0.0 || fme.isAdminBypassing()) return true;

		if(Conf.bankEnabled && Conf.bankFactionPaysCosts && fme.hasFaction())
			return Econ.modifyMoney(myFaction, -cost, toDoThis, forDoingThis);
		else
			return Econ.modifyMoney(fme, -cost, toDoThis, forDoingThis);
	}

	// like above, but just make sure they can pay; returns true unless person can't afford the cost
	public boolean canAffordCommand(double cost, String toDoThis)
	{
		if ( ! Econ.shouldBeUsed() || this.fme == null || cost == 0.0 || fme.isAdminBypassing()) return true;

		if(Conf.bankEnabled && Conf.bankFactionPaysCosts && fme.hasFaction())
			return Econ.hasAtLeast(myFaction, cost, toDoThis);
		else
			return Econ.hasAtLeast(fme, cost, toDoThis);
	}
}
