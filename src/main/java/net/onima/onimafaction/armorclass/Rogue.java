package net.onima.onimafaction.armorclass;

import java.util.Collection;

import org.bukkit.ChatColor;
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
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaapi.utils.OSound;
import net.onima.onimafaction.armorclass.type.RogueArmor;
import net.onima.onimafaction.armorclass.utils.Buff;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.players.FPlayer;

public class Rogue extends ArmorClass {

	public static final Buff ROGUE_BACKSTAB;
	public static final Integer MAXIMUM_ROGUE_PER_FACTION;
	public static final double BACKSTAB_DAMAGE;
	public static final String NAME;
	public static final ChatColor NAME_COLOR;
	
	private static final String description;
	private static final BaseComponent[] classLoadingMessage, classActivatedMessage, classDisabledMessage;
	private static final Collection<PotionEffect> effects;
	
	static {
		ROGUE_BACKSTAB = new Buff("rogue_backstab", new ItemStack(Material.GOLD_SWORD), new OSound(Sound.ITEM_BREAK, 1.0F, 1.0F)) {
			
			@Override
			public boolean action(Player player) {
				useSound.play(APIPlayer.getPlayer(player.getUniqueId()));
				Methods.removeOneItem(player);
				return true;
			}
		};
		MAXIMUM_ROGUE_PER_FACTION = 1;
		BACKSTAB_DAMAGE = 4.0D;
		NAME = "Rogue";
		NAME_COLOR = ChatColor.RED;
		
		description = "§8" + ConfigurationService.STAIGHT_LINE + ConfigurationService.STAIGHT_LINE.substring(10) + '\n' 
				+ "§fLa classe " + NAME_COLOR + NAME + " §fpermet un mode de jeu\n"
				+ "§fplus axé sur le mode grosse salope. En effet, un §2atout\n"
				+ "§fpermettent de backstab ses ennemies et de\n"
				+ "§fles §2soulever §fen leur enlevant §23 coeurs §fd'un coup.\n\n"
				+ ""
				+ "§fLes effets §2Speed II§f, §2Régénération II §f&\n"
				+ "§2Jump Boost IV §fpermettent de compenser les\n"
				+ "§fdésavantages de l'armure en chaîne.\n\n"
				+ ""
				+ "§fAvec une épée en or vous pouvez infliger jusqu'à\n"
				+ "§23 coeurs §fde dégâts à un joueur. Après\n"
				+ "§fça votre épée en or se brisera et vous devrez en utiliser\n"
				+ "§fune autre (pensez au combo épée en or mdr ça va niquer des mères).\n\n\n"
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
				new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 3));
	}

	public Rogue(FPlayer fPlayer) {
		super(fPlayer);
		classType = new RogueArmor();
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
		
		if (faction != null && faction.getCountOfActivated(getClass()) >= MAXIMUM_ROGUE_PER_FACTION) {
			fPlayer.getApiPlayer().sendMessage("§cIl y a déjà §l" + MAXIMUM_ROGUE_PER_FACTION + " §crogue dans votre faction, vous ne pouvez équiper cette classe !");
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
