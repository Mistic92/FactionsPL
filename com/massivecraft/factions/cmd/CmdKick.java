package com.massivecraft.factions.cmd;

import org.bukkit.Bukkit;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;

public class CmdKick extends FCommand
{
	
	public CmdKick()
	{
		super();
		this.aliases.add("kick");
		
		this.requiredArgs.add("player name");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.KICK.node;
		this.disableOnLock = false;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = true;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{	
		FPlayer you = this.argAsBestFPlayerMatch(0);
		if (you == null) return;
		
		if (fme == you)
		{
			msg("<b>Nie mozesz wyrzucic samego siebie, to jak samogwalt!");
			msg("<i>Czy chodzilo ci o: %s", p.cmdBase.cmdLeave.getUseageTemplate(false));
			return;
		}

		Faction yourFaction = you.getFaction();

		// players with admin-level "disband" permission can bypass these requirements
		if ( ! Permission.KICK_ANY.has(sender))
		{
			if (yourFaction != myFaction)
			{
				msg("%s<b> nie jest czlonkiem %s", you.describeTo(fme, true), myFaction.describeTo(fme));
				return;
			}

			if (you.getRole().value >= fme.getRole().value)
			{
				// TODO add more informative messages.
				msg("<b>Twoja stanowisko jest za niskie aby wyrzucic tego mieszkanca.");
				return;
			}

			if ( ! Conf.canLeaveWithNegativePower && you.getPower() < 0)
			{
				msg("<b>Nie mozesz wyrzucic tego mieszkanca dopoki jego energia jest dodatnia.");
				return;
			}
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make sure they can pay
		if ( ! canAffordCommand(Conf.econCostKick, "aby wyrzucic kogos z miasta")) return;

		// trigger the leave event (cancellable) [reason:kicked]
		FPlayerLeaveEvent event = new FPlayerLeaveEvent(you, you.getFaction(), FPlayerLeaveEvent.PlayerLeaveReason.KICKED);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) return;

		// then make 'em pay (if applicable)
		if ( ! payForCommand(Conf.econCostKick, "aby wyrzucic kogos z miasta", "do wyrzucania z miasta")) return;

		yourFaction.msg("%s<i> zeslal %s<i> na wieczna banicje!", fme.describeTo(yourFaction, true), you.describeTo(yourFaction, true));
		you.msg("%s<i> wyrzucil cie z %s<i>!", fme.describeTo(you, true), yourFaction.describeTo(you));
		if (yourFaction != myFaction)
		{
			fme.msg("<i>Zeslales na wieczna banicje %s<i> z miasta %s<i>!", you.describeTo(fme), yourFaction.describeTo(fme));
		}

		if (Conf.logFactionKick)
			P.p.log((senderIsConsole ? "Konsola " : fme.getName())+" wyrzucila "+you.getName()+" z miasta: "+yourFaction.getTag());

		if (you.getRole() == Role.ADMIN)
			yourFaction.promoteNewLeader();

		yourFaction.deinvite(you);
		you.resetFactionData();
	}

}
