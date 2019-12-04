package net.onima.onimafaction.armorclass;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.minecraft.util.com.google.common.collect.Lists;
import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaapi.utils.OEffect;
import net.onima.onimaapi.utils.OSound;
import net.onima.onimaapi.utils.time.Time.LongTime;
import net.onima.onimafaction.armorclass.type.ReaperArmor;
import net.onima.onimafaction.armorclass.utils.Buff;
import net.onima.onimafaction.cooldowns.ReaperModeCooldown;
import net.onima.onimafaction.cooldowns.ReaperPowerCooldown;
import net.onima.onimafaction.cooldowns.ReaperStealthCooldown;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.players.FPlayer;

public class Reaper extends ArmorClass {

	public static final Integer MAXIMUM_REAPER_PER_FACTION;
	public static final Integer STEALTH_DAMAGE_ALERT_RADIUS;
	public static final Buff REAPER_BUFF;
	public static final String NAME;
	public static final ChatColor NAME_COLOR;
	
	private static final String description;
	private static final BaseComponent[] classLoadingMessage, classActivatedMessage, classDisabledMessage;
	private static final Collection<PotionEffect> effects, stealthEffects, powerEffects;
	private static final OEffect hideEffect;
	
	static {
		MAXIMUM_REAPER_PER_FACTION = 2;
		STEALTH_DAMAGE_ALERT_RADIUS = 25;
		REAPER_BUFF = new Buff("reaper_mode_changer", new ItemStack(Material.QUARTZ), new OSound(Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F)) {
			
			@Override
			public boolean action(Player player) {
				APIPlayer apiPlayer = APIPlayer.getPlayer(player.getUniqueId());

				if (apiPlayer.getTimeLeft(ReaperModeCooldown.class) > 0L)
					return false;
				else {
					Location loc = player.getLocation();
					
					Methods.removeOneItem(player);
					circleParticles(1, 1, 0.1, loc.clone().add(0, 0.25, 0));
					circleParticles(1, 1, 0.1, loc.clone().add(0, 1, 0));
					circleParticles(1, 1, 0.1, loc.clone().add(0, 1.75, 0));
					useSound.play(loc);
					
					for (APIPlayer online : APIPlayer.getOnlineAPIPlayers()) {
						if (!OnimaPerm.ONIMAAPI_VANISH_COMMAND.has(online.toPlayer()))
							online.toPlayer().hidePlayer(player);
					}
					
					apiPlayer.startCooldown(ReaperModeCooldown.class);
					Methods.clearEffects(player);
					player.addPotionEffects(stealthEffects);
					
					apiPlayer.startCooldown(ReaperStealthCooldown.class);
					return true;
				}
			}
		};
		NAME = "Reaper";
		NAME_COLOR = ChatColor.WHITE;
		
		description = "§8" + ConfigurationService.STAIGHT_LINE + ConfigurationService.STAIGHT_LINE.substring(10) + '\n' 
				+ "§fLa classe " + NAME_COLOR + NAME + " §fpermet un mode de jeu\n"
				+ "§fplus axé sur la discretion. En effet, plusieurs §2atouts\n"
				+ "§fpermettent de se §2cacher §fet d'attaquer l'ennemi\n"
				+ "§fpar surprise.\n\n"
				+ ""
				+ "§fLes effets §2Speed II§f, §2Fire résistance I §f&\n"
				+ "§2Resistance II §fpermettent de compenser les\n"
				+ "§fdésavantages de l'armure en or et en fer.\n\n"
				+ ""
				+ "§fLe reaper a plusieurs mode.\n"
				+ "§f  - §aPassif §f(§7§oPermet de jouer normalement§f)\n"
				+ "§f  - §5Furtif §f(§7§oTotalement invisible avec l'armure§f)\n"
				+ "§f  - §cPuissance §f(§7§oAvec Force II pour un max de dégât§f)\n\n"
				+ ""
				+ "§fQuand vous utilisez votre buff, vous avez §245 secondes§f de\n"
				+ "§fdélai avant de pouvoir le réutiliser. Vous entrez donc en\n"
				+ "§fmode §5furtif §fpour §27 secondes§f. Vous passerez automatiquement\n"
				+ "§fen mode §cpuissance §fsi vous attaquez un joueur ou à la fin du temps.\n"
				+ "§fEn mode §cpuissance§f, vous êtes un peu comme un §2tank§f, puissant, encaisseur\n"
				+ "§fmais plus lent. §fA la fin du mode §cpuissance §fvous repasserez en mode\n"
				+ "§afurtif§f.\n\n\n"
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
				new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0),
				new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
		stealthEffects = Lists.newArrayList(new PotionEffect(PotionEffectType.SPEED, 7 * 20, 2),
				new PotionEffect(PotionEffectType.ABSORPTION, 7 * 20, 0),
				new PotionEffect(PotionEffectType.INVISIBILITY, 7 * 20, 1));
		powerEffects = Lists.newArrayList(new PotionEffect(PotionEffectType.SPEED, 7 * 20, 0),
				new PotionEffect(PotionEffectType.ABSORPTION, 7 * 20, 2),
				new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 7 * 20, 1),
				new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 7 * 20, 0));
		
		hideEffect = new OEffect(Effect.LAVADRIP, 1);
	}
	
	private ReaperStage reaperStage;
	
	{
		reaperStage = ReaperStage.PASSIVE_MODE;
	}
	
	public Reaper(FPlayer fPlayer) {
		super(fPlayer);
		
		classType = new ReaperArmor();
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
		
		if (faction != null && faction.getCountOfActivated(getClass()) >= MAXIMUM_REAPER_PER_FACTION) {
			fPlayer.getApiPlayer().sendMessage("§cIl y a déjà §l" + MAXIMUM_REAPER_PER_FACTION + " §creaper dans votre faction, vous ne pouvez équiper cette classe !");
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
		fPlayer.getApiPlayer().sendMessage(classDisabledMessage);
		return true;
	}
	
	public boolean start(ReaperStage reaperStage, boolean force) {
		switch (reaperStage) {
		case PASSIVE_MODE:
			return passive();
		case POWER_MODE:
			return power(force);
		case STEALTH_MODE:
			return stealth();
		default:
			return false;
		}
	}
	
	public boolean start(ReaperStage reaperStage) {
		return start(reaperStage, false);
	}
	
	private boolean passive() {
		Player player = fPlayer.getApiPlayer().toPlayer();
		reaperStage = ReaperStage.PASSIVE_MODE;
		
		player.sendMessage("§c" + ConfigurationService.STAIGHT_LINE);
		player.sendMessage("§eVous §7êtes maintenant en mode §apassif§7.");
		Methods.clearEffects(player);
		player.addPotionEffects(effects);
		return true;
	}

	private boolean stealth() {
		Player player = fPlayer.getApiPlayer().toPlayer();
		
		if (REAPER_BUFF.action(player)) {
			player.sendMessage("§c" + ConfigurationService.STAIGHT_LINE);
			player.sendMessage("§eVous §7venez d'entrer en mode §5furtif §7pour §e7 secondes§7.");
			reaperStage = ReaperStage.STEALTH_MODE;
			return true;
		} else {
			player.sendMessage("§cVous devez attendre " + LongTime.setHMSFormat(fPlayer.getApiPlayer().getTimeLeft(ReaperModeCooldown.class)) + " avant de pouvoir passer en mode §5furtif §c!");
			return false;
		}
	}
	
	private boolean power(boolean force) {
		if (reaperStage != ReaperStage.PASSIVE_MODE) {
			Player player = fPlayer.getApiPlayer().toPlayer();
			APIPlayer apiPlayer = fPlayer.getApiPlayer();
			Location loc = player.getLocation();
			
			circleParticles(1, 1, 0.1, loc.clone().add(0, 0.25, 0));
			circleParticles(1, 1, 0.1, loc.clone().add(0, 1, 0));
			circleParticles(1, 1, 0.1, loc.clone().add(0, 1.75, 0));
			
			for (Player online : Bukkit.getOnlinePlayers()) {
				if (!online.canSee(player) && !OnimaPerm.ONIMAAPI_VANISH_COMMAND.has(player))
					online.showPlayer(player);
			}
			
			Methods.clearEffects(player);
			player.addPotionEffects(powerEffects);
			
			apiPlayer.startCooldown(ReaperPowerCooldown.class);
			reaperStage = ReaperStage.POWER_MODE;
			player.sendMessage(force ? "§eVous §7avez été mis en mode §cpuissance §7de force pour §e7 secondes§7." : "§eVous §7êtes maintenant en mode §cpuissance §7pour §e7 secondes§7.");
			return true;
		}
		return false;
	}
	
	public ReaperStage getReaperStage() {
		return reaperStage;
	}

	private static void circleParticles(int scaleX, int scaleZ, double density, Location loc) {
		for (double i = 0; i < 2 * Math.PI; i += density) {
		     double x = Math.cos(i) * scaleX;
		     double z = Math.sin(i) * scaleZ;
		     
		     hideEffect.play(loc.clone().add(x, 0, z));
		}
	}
	
	
	public static enum ReaperStage {
		POWER_MODE,
		STEALTH_MODE,
		PASSIVE_MODE;
	}
	
}
