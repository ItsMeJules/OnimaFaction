package net.onima.onimafaction.armorclass.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.FireworkEffect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import net.onima.onimaapi.saver.Saver;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.InstantFirework;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaapi.utils.time.Time.IntegerTime;
import net.onima.onimaapi.zone.struct.Flag;
import net.onima.onimaapi.zone.type.Region;
import net.onima.onimafaction.armorclass.Bard;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.players.FPlayer;

public class BardItem implements Saver {
	
	protected static Collection<BardItem> bardItems;
	
	static {
		bardItems = new ArrayList<>();
	}

	private String id, name;
	private ItemStack itemStack;
	private PotionEffect clickEffect, heldEffect;
	private FireworkEffect fireworkEffect;
	private int power;
	
	public BardItem(String id, String name, ItemStack itemStack) {
		this.id = id;
		this.name = name;
		this.itemStack = itemStack;
		save();
	}

	public BardItemUseFinality use(Bard bard, boolean isClick, boolean notify) {	
		FPlayer fPlayer = bard.getFPlayer();
		Region region = fPlayer.getRegionOn();
		Player player = fPlayer.getApiPlayer().toPlayer();
		
		if (region.hasFlag(Flag.NO_BARDING)) {
			if (notify)
				player.sendMessage("§cVous ne pouvez pas donner d'effets depuis " + region.getDisplayName(player) + "§c.");
			return BardItemUseFinality.DENY_ZONE;
		}
		
		if (isClick) {
			if (!bard.hasMorePowerThan(power)) {
				if (notify)
					player.sendMessage("§cIl vous manque §a" + (power - bard.getPower()) + " §cde power pour pouvoir utiliser le buff de " + name + "§c.");
				return BardItemUseFinality.DENY_POWER;
			}
		}
		
		
		PotionEffect effect = isClick ? clickEffect : heldEffect;
		
		if (effect != null) {
			boolean debuff = ConfigurationService.DEBUFF_EFFECTS.contains(effect.getType());
			List<Player> targets = new ArrayList<>(debuff ? 50 : ConfigurationService.FACTION_MAX_MEMBERS);
			
			PlayerFaction faction = null;
			
			if ((faction = fPlayer.getFaction()) != null) {
				targets.add(player);
				
				for (Entity nearbyEntity : player.getNearbyEntities(ConfigurationService.BARD_BARDING_DISTANCE, ConfigurationService.BARD_BARDING_DISTANCE, ConfigurationService.BARD_BARDING_DISTANCE)) {
					if(!(nearbyEntity instanceof Player)) continue;
					
					Player target = (Player) nearbyEntity;
					boolean inFaction = faction.getMembers().containsKey(target.getUniqueId());
					
					if (!debuff) {
						if (inFaction) 
							targets.add(target);
					} else if (!inFaction)
						targets.add(target);
				}
			} else if (!debuff)
				targets.add(player);
			else {
				for (Entity nearbyEntity : player.getNearbyEntities(ConfigurationService.BARD_BARDING_DISTANCE, ConfigurationService.BARD_BARDING_DISTANCE, ConfigurationService.BARD_BARDING_DISTANCE)) {
					if(!(nearbyEntity instanceof Player)) continue;
					
					targets.add((Player) nearbyEntity);
				}
			}
			
			String effectNiceName = ConfigurationService.EFFECTS_NICE_NAME.get(effect.getType()), time = IntegerTime.setYMDWHMSFormat(effect.getDuration() / 20 * 1000);
			int amplifier = effect.getAmplifier();
			
			if (!targets.isEmpty() && isClick) {
				player.sendMessage("§6" + ConfigurationService.STAIGHT_LINE);
				player.sendMessage(name + " §eutilisé :\n" 
						+ " §e» §7Effet : §6" + effectNiceName + ' ' + Methods.toRomanNumber(amplifier + 1) + " §7pour §6" + time + "§7.\n"
						+ " §e» §7Coût en énergie : §6" + power + "§7.\n"
						+ " §e» §7Joueurs affectés : §c" + targets.size());
				bard.removePower(power);
				Methods.removeOneItem(player);
			}
			
			for (Player target : targets) {
				Bard.setBardEffect(effect, target);
				
				if (isClick) {
					InstantFirework.spawn(target.getEyeLocation().add(0, -0.5, 0), fireworkEffect);
					target.sendMessage("§6" + ConfigurationService.STAIGHT_LINE);
					target.sendMessage("§6" + effectNiceName + ' ' + Methods.toRomanNumber(amplifier + 1) + " §7activé pour §6" + time + "§7.");
				}
			}
		} else return BardItemUseFinality.DENY_EFFECT_NULL;
		
		return BardItemUseFinality.SUCCESS;
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public ItemStack getItemStack() {
		return itemStack;
	}
	
	public PotionEffect getClickEffect() {
		return clickEffect;
	}

	public void setClickEffect(PotionEffect clickEffect) {
		this.clickEffect = clickEffect;
	}

	public PotionEffect getHeldEffect() {
		return heldEffect;
	}

	public void setHeldEffect(PotionEffect heldEffect) {
		this.heldEffect = heldEffect;
	}

	public FireworkEffect getFireworkEffect() {
		return fireworkEffect;
	}

	public void setFireworkEffect(FireworkEffect fireworkEffect) {
		this.fireworkEffect = fireworkEffect;
	}

	public int getPower() {
		return power;
	}

	public void setPower(int power) {
		this.power = power;
	}
	
	@Override
	public void save() {
		bardItems.add(this);
	}

	@Override
	public void remove() {
		bardItems.remove(this);
	}

	@Override
	public boolean isSaved() {
		return bardItems.contains(this);
	}
	
	public static BardItem fromName(String id) {
		return bardItems.stream().filter(buff -> buff.id.equalsIgnoreCase(id)).findFirst().orElse(null);
	}
	
	public static BardItem fromItemStack(ItemStack itemStack) {
		return bardItems.stream().filter(buff -> Methods.isSimilar(buff.itemStack, itemStack)).findFirst().orElse(null);
	}

	public static enum BardItemUseFinality {
		DENY_ZONE,
		DENY_POWER,
		DENY_EFFECT_NULL,
		SUCCESS;
	}
	
}
