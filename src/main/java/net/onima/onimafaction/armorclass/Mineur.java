package net.onima.onimafaction.armorclass;

import java.util.Collection;
import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.Lists;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimafaction.armorclass.type.MineurArmor;
import net.onima.onimafaction.players.FPlayer;

public class Mineur extends ArmorClass {
		
	public static final int INVISIBILITY_LAYER;
	public static final PotionEffect INVISIBILITY = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0);
	public static final String NAME;
	public static final ChatColor NAME_COLOR;
	
	private static final String description;
	private static final BaseComponent[] classLoadingMessage, classActivatedMessage, classDisabledMessage;
	private static final Collection<PotionEffect> effects;
		
	static {
		INVISIBILITY_LAYER = 20;
		NAME = "Mineur";
		NAME_COLOR = ChatColor.BLUE;
		
		description = "§8" + ConfigurationService.STAIGHT_LINE + ConfigurationService.STAIGHT_LINE.substring(10) + '\n' 
				+ "§fLa classe " + NAME_COLOR + NAME.toLowerCase() + " §fpermet d'aller dans\n"
				+ "§fles souterrains en étant §2invisible §fà\n"
				+ "§fpartir de la couche §2" + INVISIBILITY_LAYER + ".\n\n"
				+ ""
				+ "§fLes effets §2Haste II§f, §2Fire Resistance I §f&\n"
				+ "§2Night Vision I §frendront le minage plus\n"
				+ "§ffacile.\n\n"
				+ ""
				+ "§4Attention§f, si vous vous faites attaquer\n"
				+ "§fen étant §2invisible§f, vous §4perdrez §fl'effet\n"
				+ "§2d'invinsibilité  §fpendant le temps en combat !\n\n\n"
				+ ""
				+ ""
				+ "§7§oPour plus d'informations, cliquez sur ce message.\n"
				+ "§8" + ConfigurationService.STAIGHT_LINE + ConfigurationService.STAIGHT_LINE.substring(10);	
		
		classLoadingMessage = new ComponentBuilder("§7§oLa classe ")
				.append(NAME_COLOR + NAME)
				.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/menu armorclass"))
				.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(description).create()))
				.append(" §7§oest en chargement...").create();
		classActivatedMessage = new ComponentBuilder("§7§oLa classe ")
				.append(NAME_COLOR + NAME)
				.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/menu armorclass"))
				.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(description).create()))
				.append(" §7§oest §a§oactivé §7§o!").create();
		classDisabledMessage = new ComponentBuilder("§7§oLa classe ")
				.append(NAME_COLOR + NAME)
				.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/menu armorclass"))
				.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(description).create()))
				.append(" §7§oest §c§odésactivé §7§o!").create();
		
		effects = Lists.newArrayList(
				new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1),
				new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0),
				new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
	}
	
	public Mineur(FPlayer fPlayer) {
		super(fPlayer);
		classType = new MineurArmor();
	}
	
	@Override
	public String getNiceName() {
		return NAME_COLOR + NAME;
	}

	@Override
	public Collection<PotionEffect> getEffects() {
		return effects;
	}
	
	@Override
	public boolean onLoad() {
		fPlayer.getApiPlayer().sendMessage(classLoadingMessage);
		return true;
	}

	@Override
	public void onEquip() {
		super.onEquip();
		
		Player player = fPlayer.getApiPlayer().toPlayer();
		
		fPlayer.getApiPlayer().sendMessage(classActivatedMessage);
		
		if (player.getLocation().getBlockY() < INVISIBILITY_LAYER)
			addInvisibility(player, true);
	}
	
	@Override
	public boolean onUnequip() {
		super.onUnequip();
		fPlayer.getApiPlayer().sendMessage(classDisabledMessage);
		removeInvisibility(fPlayer.getApiPlayer().toPlayer(), false);
		return true;
	}
	
	public void removeInvisibility(Player player, boolean notify) {
		PotionEffect effect = Methods.getPotionEffectByType(player.getActivePotionEffects(), PotionEffectType.INVISIBILITY);
		
		if (effect != null && effect.getDuration() > MAXIMUM_DURATION_EFFECT) {
			player.removePotionEffect(PotionEffectType.INVISIBILITY);
			
			Iterator<PotionEffect> iterator = effectsTookFromClass.iterator();
				
			while (iterator.hasNext()) {
				PotionEffect effectTook = iterator.next();
					
				if (effectTook.getType() == PotionEffectType.INVISIBILITY) {
					player.addPotionEffect(effectTook);
					if (notify)
						player.sendMessage("§7L'invisibilité de la classe " + NAME_COLOR + NAME + " §7a été remplacé par votre ancien effet d'invisibilité.");
					iterator.remove();
					return;
				}
			}
			
			if (notify)
				player.sendMessage("§dVous §7n'êtes plus §cinvisible.");
		}
	}
	
	public void addInvisibility(Player player, boolean notify) {
		PotionEffect effect = Methods.getPotionEffectByType(player.getActivePotionEffects(), PotionEffectType.INVISIBILITY);
		
		if (effect != null) {
			effectsTookFromClass.add(effect);
			player.removePotionEffect(PotionEffectType.INVISIBILITY);
		}
		
		player.addPotionEffect(INVISIBILITY);
		player.sendMessage("§dVous §7êtes maintenant §ainvisible");
	}
	
	public void handleInvisibility(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		int fromY = event.getFrom().getBlockY();
		int toY = event.getTo().getBlockY();
		
		if (fromY != toY && equals(fPlayer.getEquippedClass())) {
			boolean isInvisible = player.hasPotionEffect(PotionEffectType.INVISIBILITY);
			
			if (toY > INVISIBILITY_LAYER) {
				if (fromY <= INVISIBILITY_LAYER && isInvisible) 
					removeInvisibility(player, true);
			} else if (!isInvisible)
				addInvisibility(player, true);
		}
	}

}
