package net.onima.onimafaction.armorclass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.minecraft.util.com.google.common.collect.Lists;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimafaction.armorclass.type.BardArmor;
import net.onima.onimafaction.armorclass.utils.BardItem;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.players.FPlayer;

public class Bard extends ArmorClass {
	
	public static final Integer MAXIMUM_BARD_PER_FACTION, MAX_POWER;
	public static final BardItem SATURATION_BUFF, FORCE_BUFF, SPEED_BUFF, JUMP_BUFF, RESISTANCE_BUFF, FIRE_RES_BUFF, REGEN_BUFF, WITHER_BUFF;
	public static final String NAME;
	public static final ChatColor NAME_COLOR;
	
	private static final String description;
	private static final BaseComponent[] classLoadingMessage, classActivatedMessage, classDisabledMessage;
	private static final Collection<PotionEffect> effects;
	private static Map<UUID, List<PotionEffect>> effectsTookFromBarding;
	private static Map<UUID, List<String>> effectsNotToSave;
	
	static {
		MAXIMUM_BARD_PER_FACTION = 3;
		MAX_POWER = 120;
		
		SATURATION_BUFF = new BardItem("bard_buff_saturation", "§e§lSaturation", new ItemStack(Material.WHEAT));
		FORCE_BUFF = new BardItem("bard_buff_force", "§c§lForce", new ItemStack(Material.BLAZE_ROD));
		SPEED_BUFF = new BardItem("bard_buff_speed", "§b§lSpeed", new ItemStack(Material.SUGAR));
		JUMP_BUFF = new BardItem("bard_buff_jump", "§a§lJump", new ItemStack(Material.FEATHER));
		RESISTANCE_BUFF = new BardItem("bard_buff_resistance", "§7§lRésistance", new ItemStack(Material.IRON_INGOT));
		FIRE_RES_BUFF = new BardItem("bard_buff_fireres", "§6§lFire Rrésistance", new ItemStack(Material.MAGMA_CREAM));
		REGEN_BUFF = new BardItem("bard_buff_regen", "§d§lRégénération", new ItemStack(Material.GHAST_TEAR));
		WITHER_BUFF = new BardItem("bard_buff_wither", "§8§lWither", new ItemStack(Material.FERMENTED_SPIDER_EYE));
		
		SATURATION_BUFF.setClickEffect(new PotionEffect(PotionEffectType.SATURATION, 10 * 20, 1));
		FORCE_BUFF.setClickEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 5 * 20, 1));
		SPEED_BUFF.setClickEffect(new PotionEffect(PotionEffectType.SPEED, 5 * 20, 2));
		JUMP_BUFF.setClickEffect(new PotionEffect(PotionEffectType.JUMP, 10 * 20, 4));
		RESISTANCE_BUFF.setClickEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5 * 20, 1));
		FIRE_RES_BUFF.setClickEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 20, 0));
		REGEN_BUFF.setClickEffect(new PotionEffect(PotionEffectType.REGENERATION, 10 * 20, 1));
		WITHER_BUFF.setClickEffect(new PotionEffect(PotionEffectType.WITHER, 5 * 20, 1));
		
		SATURATION_BUFF.setHeldEffect(new PotionEffect(PotionEffectType.SATURATION, 5 * 20, 0));
		FORCE_BUFF.setHeldEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 5 * 20, 0));
		SPEED_BUFF.setHeldEffect(new PotionEffect(PotionEffectType.SPEED, 5 * 20, 1));
		JUMP_BUFF.setHeldEffect(new PotionEffect(PotionEffectType.JUMP, 5 * 20, 1));
		RESISTANCE_BUFF.setHeldEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5 * 20, 0));
		FIRE_RES_BUFF.setHeldEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 5 * 20, 0));
		REGEN_BUFF.setHeldEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 0));
		
		SATURATION_BUFF.setPower(20);
		FORCE_BUFF.setPower(45);
		SPEED_BUFF.setPower(30);
		JUMP_BUFF.setPower(20);
		RESISTANCE_BUFF.setPower(25);
		FIRE_RES_BUFF.setPower(15);
		REGEN_BUFF.setPower(30);
		WITHER_BUFF.setPower(35);
		
		SATURATION_BUFF.setFireworkEffect(FireworkEffect.builder().with(Type.BALL).withColor(Color.YELLOW).build());
		FORCE_BUFF.setFireworkEffect(FireworkEffect.builder().with(Type.BALL).withColor(Color.RED).build());
		SPEED_BUFF.setFireworkEffect(FireworkEffect.builder().with(Type.BALL).withColor(Color.AQUA).build());
		JUMP_BUFF.setFireworkEffect(FireworkEffect.builder().with(Type.BALL).withColor(Color.LIME).build());
		RESISTANCE_BUFF.setFireworkEffect(FireworkEffect.builder().with(Type.BALL).withColor(Color.SILVER).build());
		FIRE_RES_BUFF.setFireworkEffect(FireworkEffect.builder().with(Type.BALL).withColor(Color.ORANGE).build());
		REGEN_BUFF.setFireworkEffect(FireworkEffect.builder().with(Type.BALL).withColor(Color.FUCHSIA).build());
		WITHER_BUFF.setFireworkEffect(FireworkEffect.builder().with(Type.BALL).withColor(Color.BLACK).build());
		
		NAME = "Bard";
		NAME_COLOR = ChatColor.YELLOW;
		
		description = "§8" + ConfigurationService.STAIGHT_LINE + ConfigurationService.STAIGHT_LINE.substring(10) + '\n' 
				+ "§fLa classe " + NAME_COLOR + NAME + " §fpermet un mode de jeu\n"
				+ "§fplus axé sur le support. En effet, plusieurs §2atouts\n"
				+ "§fpermettent d'aider ses coéquipiers et de\n"
				+ "§fles §2favoriser §fpar rapport à l'adversaire.\n\n"
				+ ""
				+ "§fLes effets §2Speed II§f, §2Régénération II §f&\n"
				+ "§2Resistance I §fpermettent de compenser les\n"
				+ "§fdésavantages de l'armure en or.\n\n"
				+ ""
				+ "§fLes §2buffs temporaire §font un cooldown\n"
				+ "§fde §210 §2secondes §fet utilisent du §2power §fsi vous\n"
				+ "§fcliquez dessus. Ils ont un rayon d'action de §220 blocks§f.\n"
				+ "§fSi vous les tenez en main vos §2coéquipiers§f,\n"
				+ "§fauront quand même des effets mais avec\n"
				+ "§fmoins de puissance.\n\n\n"
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
		
		effects = Lists.newArrayList(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1),
				new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 1),
				new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
		
		effectsTookFromBarding = new HashMap<>();
		effectsNotToSave = new HashMap<>();
	}
	
	private int power;
	
	public Bard(FPlayer fPlayer) {
		super(fPlayer);
		classType = new BardArmor();
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
		PlayerFaction faction = fPlayer.getFaction();
		
		if (faction != null && faction.getCountOfActivated(getClass()) >= MAXIMUM_BARD_PER_FACTION) {
			fPlayer.getApiPlayer().sendMessage("§cIl y a déjà §l" + MAXIMUM_BARD_PER_FACTION + " §cbard dans votre faction, vous ne pouvez équiper cette classe !");
			return false;
		}
		
		fPlayer.getApiPlayer().sendMessage(classLoadingMessage);
		return true;
	}
	
	@Override
	public void onEquip() {
		super.onEquip();
		fPlayer.getApiPlayer().sendMessage(classActivatedMessage);
	}
	
	@Override
	public boolean onUnequip() {
		super.onUnequip();
		power = 0;
		fPlayer.getApiPlayer().sendMessage(classDisabledMessage);
		return true;
	}

	public int getPower() {
		return power;
	}
	
	public void setPower(int power) {
		this.power = power;
	}
	
	public void addPower(int power) {
		this.power += power;
	}
	
	public void removePower(int power) {
		this.power -= power;
	}
	
	public boolean hasMorePowerThan(int power) {
		return this.power > power;
	}
	
	public static void setBardEffect(PotionEffect effect, Player player) {
		FPlayer fPlayer = FPlayer.getByUuid(player.getUniqueId());
		Collection<String> bardingEffects = fPlayer.getEffectsFromBard();
		PotionEffectType type = effect.getType();
				
		for (PotionEffect playerEffect : player.getActivePotionEffects()) {
			if (playerEffect.getType().equals(effect.getType())) {
				if (effect.getAmplifier() < playerEffect.getAmplifier())
					return;
				else if (effect.getAmplifier() == playerEffect.getAmplifier()) {
					if (effect.getDuration() < playerEffect.getDuration())
						return;
				}
				
				if (!bardingEffects.contains(type.getName()))
					fPlayer.addEffectTookFromBarding(playerEffect);
				
				player.removePotionEffect(type);
			}
			
			bardingEffects.add(type.getName());
			player.addPotionEffect(effect);
		}
		
	}
	
	public static List<String> getEffectsNotToSave(Player player) {
		return effectsNotToSave.getOrDefault(player.getUniqueId(), new ArrayList<>());
	}
	
	public static List<PotionEffect> getEffectsTookFromBarding(Player player) {
		return effectsTookFromBarding.get(player.getUniqueId());
	}
	
}
