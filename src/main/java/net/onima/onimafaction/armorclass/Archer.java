package net.onima.onimafaction.armorclass;

import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.Lists;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimafaction.armorclass.type.ArcherArmor;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.players.FPlayer;

public class Archer extends ArmorClass {

	public static final Integer MAXIMUM_ARCHER_PER_FACTION, TAG_1_MULTIPLIER, TAG_2_MULTIPLIER;
	public static final String NAME;
	public static final ChatColor NAME_COLOR;
	
	private static final String description;
	private static final BaseComponent[] classLoadingMessage, classActivatedMessage, classDisabledMessage;
	private static final Collection<PotionEffect> effects;
	
	static {
		MAXIMUM_ARCHER_PER_FACTION = 3;
		TAG_1_MULTIPLIER = 2;
		TAG_2_MULTIPLIER = 3;
		NAME = "Archer";
		NAME_COLOR = ChatColor.GREEN;
		
		description = "§8" + ConfigurationService.STAIGHT_LINE + ConfigurationService.STAIGHT_LINE.substring(10) + '\n' 
						+ "§fLa classe " + NAME_COLOR + NAME + " §fpermet un mode de jeu\n"
						+ "§fplus axé sur l'arc. En effet, plusieurs §2atouts\n"
						+ "§fpermettent de fuir le combat rapproché et de\n"
						+ "§2favoriser §fle combat à distance.\n\n"
						+ ""
						+ "§fLes effets §2Speed III §f& §2Resistance II\n"
						+ "§fpermettent de compenser les désavantages de\n"
						+ "l'armure en cuir.\n\n"
						+ ""
						+ "§fLes buffs temporaire §2Speed IV §f(§25s§f) &\n"
						+ "§2Jump Boost IV §f(§25s§f) permettent de garder\n"
						+ "§fla distance. §4Attention§f, ils ont chacun un\n"
						+ "§fcooldown de §230 secondes§f.\n\n"
						+ ""
						+ "§fLes armures peuvent aussi être §2colorées §fgrâce\n"
						+ "§fà des §2colorants §fnaturels. En faisant ceci, vous\n"
						+ "§faurez un certain §2% de chance §fde donner des effets\n"
						+ "§4négatifs §fà vos adversaires.\n\n\n"
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
				new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2),
				new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
	}
	
	public Archer(FPlayer fPlayer) {
		super(fPlayer);
		classType = new ArcherArmor();
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
		
		if (faction != null && faction.getCountOfActivated(getClass()) >= MAXIMUM_ARCHER_PER_FACTION) {
			fPlayer.getApiPlayer().sendMessage("§cIl y a déjà §l" + MAXIMUM_ARCHER_PER_FACTION + " §carcher dans votre faction, vous ne pouvez équiper cette classe !");
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
	
}
