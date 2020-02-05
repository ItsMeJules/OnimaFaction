package net.onima.onimafaction.cooldowns;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.onima.onimaapi.cooldown.utils.Cooldown;
import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.players.OfflineAPIPlayer;
import net.onima.onimaapi.utils.InstantFirework;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaapi.utils.OSound;
import net.onima.onimaapi.utils.time.Time;
import net.onima.onimaapi.utils.time.Time.LongTime;
import net.onima.onimafaction.armorclass.Archer;
import net.onima.onimafaction.armorclass.utils.Buff;
import net.onima.onimafaction.players.FPlayer;

public class ArcherSpeedCooldown extends Cooldown implements Listener {
	
	private Buff speedBuff;
	
	{
		speedBuff = new Buff("archer_speed_buff", new ItemStack(Material.SUGAR), new OSound(Sound.ORB_PICKUP, 1, 10)) {
			
			@Override
			public boolean action(Player player) {
				if (getTimeLeft(player.getUniqueId()) > 0L)
					return false;
				else {
					APIPlayer apiPlayer = APIPlayer.getPlayer(player);
					
					Methods.removeOneHandItem(player);
					onStart(apiPlayer);
					
					useSound.play(apiPlayer);
					InstantFirework.spawn(player.getEyeLocation().add(0, -0.5, 0), FireworkEffect.builder().with(Type.BALL).withColor(Color.AQUA).build());
					
					player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5 * 20, 3), true);
					return true;
				}
			}
			
		};
	}

	public ArcherSpeedCooldown() {
		super("archer_speed", (byte) 10, 10 * Time.SECOND);
	}

	@Override
	public String scoreboardDisplay(long timeLeft) {
		return "  §e» Speed §6: §c" + LongTime.setHMSFormat(timeLeft);
	}

	@Override
	public boolean action(OfflineAPIPlayer offline) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@EventHandler
	public void onBuffClick(PlayerInteractEvent event) {
		if (event.getAction().toString().contains("RIGHT") && event.hasItem()) {
			Player player = event.getPlayer();
			
			if (FPlayer.getPlayer(player).getArmorClass(Archer.class).isActivated()) {
				ItemStack item = event.getItem();
				
				if (speedBuff.getItemStack().isSimilar(item)) {
					if (speedBuff.action(player))
						player.sendMessage("§2Speed IV §factivé pour §25 secondes§f.");
					else
						player.sendMessage("§cVous devez attendre " + LongTime.setHMSFormat(getTimeLeft(player.getUniqueId())) + " avant de pouvoir utiliser le speed buff !");
				}
			}
		}
	}

}
