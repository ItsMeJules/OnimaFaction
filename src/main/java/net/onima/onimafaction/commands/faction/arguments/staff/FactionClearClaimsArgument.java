package net.onima.onimafaction.commands.faction.arguments.staff;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimafaction.OnimaFaction;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.faction.PlayerFaction;

public class FactionClearClaimsArgument extends FactionArgument {

	public FactionClearClaimsArgument() {
		super("clearclaims", OnimaPerm.ONIMAFACTION_CLEARCLAIMS_ARGUMENT);
		
		usage = new JSONMessage("§c/f " + name + " <faction | all>", "§d§oClear tous les claims d'une faction \n§dou de tout le serveur.");
		
		needFaction = false;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 2, true))
			return false;
		
		if (args[1].equalsIgnoreCase("all")) {
			if (sender.hasPermission(OnimaPerm.ONIMAFACTION_CLEARCLAIMS_ALL.getPermission())) {
				Conversable conversable = (Conversable) sender;
				conversable.beginConversation(new ConversationFactory(OnimaFaction.getInstance()).withFirstPrompt(new ClearClaimAllPrompt()).withEscapeSequence("/no").withTimeout(10).withModality(false).withLocalEcho(true).buildConversation(conversable));
				return true;
			} else {
				sender.sendMessage(OnimaPerm.ONIMAFACTION_CLEARCLAIMS_ALL.getMissingMessage());
				return false;
			}
		}
		
		Faction faction = null;
		
		if ((faction = Faction.getFaction(args[1])) == null) {
			sender.sendMessage("§cImpossible de trouver la faction ou le joueur " + args[1]);
			return false;
		}
		
		if (faction instanceof PlayerFaction)
			((PlayerFaction) faction).broadcast("§d§o" + sender.getName() + " §7a clear tous vos claims !");
		
		sender.sendMessage("§d§oVous §7avez clear tous les claims de la faction §d§o" + faction.getName());
		faction.clearClaims();
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length != 2) 
			return Collections.emptyList();
		
		return Faction.getFactions().parallelStream().filter(faction -> StringUtil.startsWithIgnoreCase(faction.getName(), args[1])).map(Faction::getName).collect(Collectors.toList());
	}

	private class ClearClaimAllPrompt extends StringPrompt {
		
		@Override
		public Prompt acceptInput(ConversationContext context, String input) {
			Conversable conversable = context.getForWhom();
			
			if (input.equalsIgnoreCase("oui")) {
				Faction.getFactions().forEach(faction -> faction.clearClaims());
				Bukkit.broadcastMessage("§c§lTOUS LES CLAIMS ONT ETE SUPPRIME PAR " + ((Player) context.getForWhom()).getName());
				return Prompt.END_OF_CONVERSATION;
			} else if(input.equalsIgnoreCase("non")) {
				conversable.sendRawMessage("§cVous avez abandonné le fait de supprimer tous les claims du serveur.");
				return Prompt.END_OF_CONVERSATION;
			} else {
				conversable.sendRawMessage("§cNous n'avons pas reconnu la réponse donnée, abandon de la suppression de tous les claims.");
				return Prompt.END_OF_CONVERSATION;
			}
		}

		@Override
		public String getPromptText(ConversationContext context) {
			return "§7Voulez-vous vraiment supprimer tous les claims du serveur ? Tapez §d§ooui §7ou §d§onon §7dans le chat pour confirmer.";
		}

	}
	
}
