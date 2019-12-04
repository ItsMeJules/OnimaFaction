package net.onima.onimafaction.cooldowns;

import java.util.SplittableRandom;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.onima.onimaapi.OnimaAPI;
import net.onima.onimaapi.cooldown.utils.Cooldown;
import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.players.OfflineAPIPlayer;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaapi.utils.time.Time;
import net.onima.onimaapi.utils.time.Time.IntegerTime;
import net.onima.onimaapi.utils.time.Time.LongTime;
import net.onima.onimafaction.armorclass.Archer;
import net.onima.onimafaction.armorclass.utils.ColoredArmor;
import net.onima.onimafaction.events.armorclass.archer.ArcherTagPlayerEvent;
import net.onima.onimafaction.players.FPlayer;

public class ArcherMarkCooldown extends Cooldown implements Listener {
	
	private SplittableRandom random;
	
	{
		random = new SplittableRandom();
	}

	public ArcherMarkCooldown() {
		super("archer_mark", (byte) 9, 10 * Time.SECOND);
	}

	@Override
	public String scoreboardDisplay(long timeLeft) {
		return "§6Archer tag %mark-number% §6: §c" + LongTime.setHMSFormat(timeLeft);
	}
	
	@Override
	public boolean action(OfflineAPIPlayer offline) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void onExpire(OfflineAPIPlayer offline) {
		if (offline.isOnline()) {
			((APIPlayer) offline).sendMessage("§aVous n'êtes plus taggé par un archer !");
			FPlayer.getPlayer(offline.getUUID()).setArcherTag(0);
		}
		
		super.onExpire(offline);
	}
	
	@EventHandler
	public void onDamageWhileArcherTagged(EntityDamageByEntityEvent event) {
		Entity entity = event.getEntity();
		
		if (entity instanceof Player && getTimeLeft(entity.getUniqueId()) > 0L && Methods.getLastAttacker(event) != null)
			event.setDamage((double) event.getDamage() * getMultiplier(FPlayer.getPlayer(entity.getUniqueId())));
	}
	
	@EventHandler
	public void onPullBow(EntityShootBowEvent event) {
		Entity entity = event.getEntity();
		
		if (entity instanceof Player) {
			Entity projectile = event.getProjectile();
			
			if (!FPlayer.getPlayer(entity.getUniqueId()).getArmorClass(Archer.class).isActivated()) return;
			
			if (projectile instanceof Arrow) {
				if (event.getForce() >= 1.0)
					projectile.setMetadata("full-charged", new FixedMetadataValue(OnimaAPI.getInstance(), "jestizbg"));
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onArcherTag(EntityDamageByEntityEvent event) {
		Entity entity = event.getEntity();
		
		if (!event.getDamager().hasMetadata("full-charged")) return;
		
		if (entity instanceof Player) {
			Player damaged = (Player) entity;
			Player shooter = Methods.getLastAttacker(event);
			
			if (shooter == null || shooter.equals(entity)) return;
			
			FPlayer fPlayer = FPlayer.getPlayer(shooter);
			
			if (fPlayer.getArmorClass(Archer.class).isActivated()) {
				FPlayer fDamaged = FPlayer.getPlayer(damaged);
				
				fDamaged.incrementTag();
				onStart(fDamaged.getOfflineApiPlayer());
				
				double distance = Double.valueOf(Methods.round("0.0", shooter.getLocation().distance(damaged.getLocation())));
				int tag = fDamaged.getArcherTag();
				String damagedName = damaged.hasPotionEffect(PotionEffectType.INVISIBILITY) ? "???" : fDamaged.getApiPlayer().getDisplayName();
				String shooterName = damaged.hasPotionEffect(PotionEffectType.INVISIBILITY) ? "???" : fPlayer.getApiPlayer().getDisplayName();
				
				damaged.sendMessage("§7[§9Distance §7(§c" + distance + "§7)] §c§lVous avez été tag par §e" + shooterName + " §7(Dégâts multiplié par " + getMultiplier(fDamaged) + ").");
				shooter.sendMessage("§7[§9Distance §7(§c" + distance + "§7)] §7Vous avez tag §e" + damagedName + " §f[§a" + tag + "§f] §7(Dégâts multiplié par " + getMultiplier(fDamaged) + "). (§e" + Methods.round("0.0", (((Damageable) damaged).getHealth() - event.getFinalDamage()) / 2) + " §4❤§7)");
				
				Color hColor = ((LeatherArmorMeta) shooter.getInventory().getHelmet().getItemMeta()).getColor();
				Color cColor = ((LeatherArmorMeta) shooter.getInventory().getChestplate().getItemMeta()).getColor();
				Color lColor = ((LeatherArmorMeta) shooter.getInventory().getLeggings().getItemMeta()).getColor();
				Color bColor = ((LeatherArmorMeta) shooter.getInventory().getBoots().getItemMeta()).getColor();
				ColoredArmor colouredArmor = ColoredArmor.getFromRGB(hColor.getRed(), hColor.getGreen(), hColor.getBlue());
				double nextDouble = random.nextDouble();
				
				Bukkit.getPluginManager().callEvent(new ArcherTagPlayerEvent(fPlayer.getArmorClass(Archer.class), fPlayer, fDamaged, tag, distance, nextDouble, colouredArmor));

				if (colouredArmor != null && random.nextDouble() <= colouredArmor.getChance()) {
					Color color = colouredArmor.getColor();
						
					if (color.equals(cColor) && color.equals(lColor) && color.equals(bColor)) {
						PotionEffect effect = colouredArmor.getEffect();
							
						damaged.addPotionEffect(effect, true);
						shooter.sendMessage("§7Comme vous avez une armure §ecolorée§7, §7vous avez donné à §e" + damagedName + " §7l'effet §e" + ConfigurationService.EFFECTS_NICE_NAME.get(effect.getType()) + ' ' + Methods.toRomanNumber(effect.getAmplifier() + 1) + " §7pour §e" + IntegerTime.setYMDWHMSFormat(effect.getDuration() / 20 * 1000) + "§7.");
						damaged.sendMessage("§7Comme l'armure de §e" + shooterName + " §7est §ecolorée§7, §7il vous a donné l'effet §e" + ConfigurationService.EFFECTS_NICE_NAME.get(effect.getType()) + ' ' + Methods.toRomanNumber(effect.getAmplifier() + 1) + " §7pour " + IntegerTime.setYMDWHMSFormat(effect.getDuration() / 20 * 1000) + "§7.");
					}
				}
			}
		}
	}
	
	private int getMultiplier(FPlayer fPlayer) {
		return fPlayer.getArcherTag() == 1 ? Archer.TAG_1_MULTIPLIER : Archer.TAG_2_MULTIPLIER;
	}
	
}
