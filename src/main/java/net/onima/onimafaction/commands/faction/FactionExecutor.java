package net.onima.onimafaction.commands.faction;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.commands.ArgumentExecutor;
import net.onima.onimafaction.commands.faction.arguments.FactionAllyArgument;
import net.onima.onimafaction.commands.faction.arguments.FactionAnnouncementArgument;
import net.onima.onimafaction.commands.faction.arguments.FactionChatArgument;
import net.onima.onimafaction.commands.faction.arguments.FactionClaimArgument;
import net.onima.onimafaction.commands.faction.arguments.FactionClaimChunkArgument;
import net.onima.onimafaction.commands.faction.arguments.FactionColeaderArgument;
import net.onima.onimafaction.commands.faction.arguments.FactionCreateArgument;
import net.onima.onimafaction.commands.faction.arguments.FactionDeinviteArgument;
import net.onima.onimafaction.commands.faction.arguments.FactionDemoteArgument;
import net.onima.onimafaction.commands.faction.arguments.FactionDepositArgument;
import net.onima.onimafaction.commands.faction.arguments.FactionDisbandArgument;
import net.onima.onimafaction.commands.faction.arguments.FactionGUIArgument;
import net.onima.onimafaction.commands.faction.arguments.FactionHelpArgument;
import net.onima.onimafaction.commands.faction.arguments.FactionHomeArgument;
import net.onima.onimafaction.commands.faction.arguments.FactionInviteArgument;
import net.onima.onimafaction.commands.faction.arguments.FactionInvitesArgument;
import net.onima.onimafaction.commands.faction.arguments.FactionJoinArgument;
import net.onima.onimafaction.commands.faction.arguments.FactionKickArgument;
import net.onima.onimafaction.commands.faction.arguments.FactionLeaderArgument;
import net.onima.onimafaction.commands.faction.arguments.FactionLeaveArgument;
import net.onima.onimafaction.commands.faction.arguments.FactionListArgument;
import net.onima.onimafaction.commands.faction.arguments.FactionMapArgument;
import net.onima.onimafaction.commands.faction.arguments.FactionNameArgument;
import net.onima.onimafaction.commands.faction.arguments.FactionOpenArgument;
import net.onima.onimafaction.commands.faction.arguments.FactionPromoteArgument;
import net.onima.onimafaction.commands.faction.arguments.FactionSetHomeArgument;
import net.onima.onimafaction.commands.faction.arguments.FactionShowArgument;
import net.onima.onimafaction.commands.faction.arguments.FactionStuckArgument;
import net.onima.onimafaction.commands.faction.arguments.FactionUnallyArgument;
import net.onima.onimafaction.commands.faction.arguments.FactionUnclaimArgument;
import net.onima.onimafaction.commands.faction.arguments.FactionWithdrawArgument;
import net.onima.onimafaction.commands.faction.arguments.staff.FactionBypassArgument;
import net.onima.onimafaction.commands.faction.arguments.staff.FactionChatSpyArgument;
import net.onima.onimafaction.commands.faction.arguments.staff.FactionClaimForArgument;
import net.onima.onimafaction.commands.faction.arguments.staff.FactionClearClaimsArgument;
import net.onima.onimafaction.commands.faction.arguments.staff.FactionDeathBanMultiplierArgument;
import net.onima.onimafaction.commands.faction.arguments.staff.FactionDeathbanArgument;
import net.onima.onimafaction.commands.faction.arguments.staff.FactionDtrArgument;
import net.onima.onimafaction.commands.faction.arguments.staff.FactionDtrCooldownArgument;
import net.onima.onimafaction.commands.faction.arguments.staff.FactionDtrLossArgument;
import net.onima.onimafaction.commands.faction.arguments.staff.FactionFlagArgument;
import net.onima.onimafaction.commands.faction.arguments.staff.FactionForceColeaderArgument;
import net.onima.onimafaction.commands.faction.arguments.staff.FactionForceDisbandArgument;
import net.onima.onimafaction.commands.faction.arguments.staff.FactionForceJoinArgument;
import net.onima.onimafaction.commands.faction.arguments.staff.FactionForceKickArgument;
import net.onima.onimafaction.commands.faction.arguments.staff.FactionForceLeaderArgument;
import net.onima.onimafaction.commands.faction.arguments.staff.FactionForceMemberArgument;
import net.onima.onimafaction.commands.faction.arguments.staff.FactionForceOfficerArgument;
import net.onima.onimafaction.commands.faction.arguments.staff.FactionForceUnclaimArgument;
import net.onima.onimafaction.commands.faction.arguments.staff.FactionGiveMoneyArgument;
import net.onima.onimafaction.commands.faction.arguments.staff.FactionLockArgument;
import net.onima.onimafaction.commands.faction.arguments.staff.FactionPermanentArgument;

public class FactionExecutor extends ArgumentExecutor {
	
	private FactionHelpArgument helpArgument;

	public FactionExecutor() {
		super("faction", OnimaPerm.ONIMAFACTION_FACTION_COMMAND);
		
		addArgument(new FactionAllyArgument());
		addArgument(new FactionAnnouncementArgument());
		addArgument(new FactionChatArgument());
		addArgument(new FactionClaimArgument());
		addArgument(new FactionClaimChunkArgument());
		addArgument(new FactionColeaderArgument());
		addArgument(new FactionCreateArgument());
		addArgument(new FactionDeinviteArgument());
		addArgument(new FactionDemoteArgument());
		addArgument(new FactionDepositArgument());
		addArgument(new FactionDisbandArgument());
		addArgument(new FactionGUIArgument());
		addArgument(helpArgument = new FactionHelpArgument(this)); 
		addArgument(new FactionHomeArgument());
		addArgument(new FactionInviteArgument());
		addArgument(new FactionInvitesArgument());
		addArgument(new FactionJoinArgument());
		addArgument(new FactionKickArgument());
		addArgument(new FactionLeaderArgument());
		addArgument(new FactionLeaveArgument());
		addArgument(new FactionListArgument());
		addArgument(new FactionMapArgument());
		addArgument(new FactionNameArgument());
		addArgument(new FactionOpenArgument());
		addArgument(new FactionPromoteArgument());
		addArgument(new FactionSetHomeArgument());
		addArgument(new FactionShowArgument());
		addArgument(new FactionStuckArgument());
		addArgument(new FactionUnallyArgument());
		addArgument(new FactionUnclaimArgument());
		addArgument(new FactionWithdrawArgument());
		
		addArgument(new FactionBypassArgument());
		addArgument(new FactionChatSpyArgument());
		addArgument(new FactionClaimForArgument());
		addArgument(new FactionClearClaimsArgument());
		addArgument(new FactionDeathbanArgument());
		addArgument(new FactionDeathBanMultiplierArgument());
		addArgument(new FactionDtrArgument());
		addArgument(new FactionDtrCooldownArgument());
		addArgument(new FactionDtrLossArgument());
		addArgument(new FactionFlagArgument());
		addArgument(new FactionForceColeaderArgument());
		addArgument(new FactionForceDisbandArgument());
		addArgument(new FactionForceJoinArgument());
		addArgument(new FactionForceKickArgument());
		addArgument(new FactionForceLeaderArgument());
		addArgument(new FactionForceMemberArgument());
		addArgument(new FactionForceOfficerArgument());
		addArgument(new FactionForceUnclaimArgument());
		addArgument(new FactionGiveMoneyArgument());
		addArgument(new FactionLockArgument());
		addArgument(new FactionPermanentArgument());
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length < 1) {
			helpArgument.onCommand(sender, cmd, label, args);
			return true;
		}

		FactionArgument argument = (FactionArgument) getArgument(args[0]);
		
		if (argument != null) {
			OnimaPerm permission = argument.getPermission();

			if (permission == null || permission.has(sender))
				return argument.onCommand(sender, cmd, label, args);
		}

		return helpArgument.onCommand(sender, cmd, label, args);
	}

}
