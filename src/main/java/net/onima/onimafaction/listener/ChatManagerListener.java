package net.onima.onimafaction.listener;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.onima.onimaapi.OnimaAPI;
import net.onima.onimaapi.cooldown.ChatSlowCooldown;
import net.onima.onimaapi.event.chat.ChatEvent;
import net.onima.onimaapi.event.chat.PrivateMessageEvent;
import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.time.Time.LongTime;
import net.onima.onimafaction.events.FactionChatEvent;
import net.onima.onimafaction.faction.struct.Chat;

public class ChatManagerListener implements Listener {

	@EventHandler
	public void onChatMute(ChatEvent event) {
		if (event instanceof PrivateMessageEvent || (event instanceof FactionChatEvent && ((FactionChatEvent) event).getChat() != Chat.GLOBAL))
			return;
		
		CommandSender sender = event.getSender();
		
		if (sender instanceof ConsoleCommandSender || sender.hasPermission(OnimaPerm.CHAT_RESTRICTION_BYPASS.getPermission()))
			return;
		
		if (OnimaAPI.getInstance().getChatManager().isMuted()) {
			sender.sendMessage("§cLe chat est actuellement muté §7§o(" + OnimaAPI.getInstance().getChatManager().getMuter() + ").");
			event.setCancelled(true);
		} else if (OnimaAPI.getInstance().getChatManager().isSlowed()) {
			APIPlayer apiPlayer = APIPlayer.getPlayer((Player) sender);
			long timeLeft = apiPlayer.getTimeLeft(ChatSlowCooldown.class);
			
			if (timeLeft > 0L) {
				sender.sendMessage("§c" + OnimaAPI.getInstance().getChatManager().getSlower() + " a slow le chat, vous devez attendre " + LongTime.setHMSFormatOnlySeconds(timeLeft));
				event.setCancelled(true);
			} else
				apiPlayer.startCooldown(ChatSlowCooldown.class, OnimaAPI.getInstance().getChatManager().getDelay() * 1000);
		}
	}
	
}