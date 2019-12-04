package net.onima.onimafaction.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import net.md_5.bungee.api.chat.BaseComponent;
import net.onima.onimaapi.caching.UUIDCache;
import net.onima.onimaapi.gui.PacketMenu;
import net.onima.onimaapi.gui.PacketStaticMenu;
import net.onima.onimaapi.gui.buttons.BackButton;
import net.onima.onimaapi.gui.buttons.DisplayButton;
import net.onima.onimaapi.gui.buttons.MenuOpenerButton;
import net.onima.onimaapi.gui.buttons.utils.Button;
import net.onima.onimaapi.gui.menu.AnvilInputMenu;
import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.utils.BetterItem;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimafaction.OnimaFaction;
import net.onima.onimafaction.commands.faction.arguments.FactionListArgument;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.manager.CommandManager;
import net.onima.onimafaction.players.FPlayer;

public class FactionGUIMenu extends PacketMenu implements PacketStaticMenu {//TODO Performcommand ne retourne pas ce que je spécifie dans les args

	private static CommandManager commandManager;
	
	static {
		commandManager = OnimaFaction.getInstance().getCommandManager();
	}
	
	public FactionGUIMenu() {
		super("faction_gui", "§dMenu §6faction", MAX_SIZE, true);
	}

	@Override
	public void registerItems() { //Creer un menu pour les invitations de joueurs
		FactionGUIButton allyButton = new FactionGUIButton(true, new BetterItem(Material.LAPIS_BLOCK, 1, 0, "§6Alliez vous à une faction.", "§d§oCliquez puis entrez le nom d'une", "§d§ofaction dans pour vous allier."));
		FactionGUIButton coLeadButton = new FactionGUIButton(true, new BetterItem(Material.GOLD_HELMET, 1, 0, "§6Désignez un co-leader.", "§d§oCliquez pour désigner un co-leader."));
		FactionGUIButton createFac = new FactionGUIButton(true, new BetterItem(Material.WORKBENCH, 1, 0, "§6Créez votre faction.", "§d§oCliquez puis entrez le nom", "§d§opour créer une faction."));
		FactionGUIButton depositMoney = new FactionGUIButton(true, new BetterItem(Material.EMERALD, 1, 0, "§6Déposez de l'argent.", "§d§oCliquez puis entrez la somme d'argent", "§d§oà déposer."));
		FactionGUIButton inviteButton = new FactionGUIButton(true, new BetterItem(Material.BED, 1, 0, "§6Invitez un joueur.", "§d§oCliquez pour inviter un joueur à rejoindre la faction."));
		FactionGUIButton kickButton = new FactionGUIButton(true, new BetterItem(Material.EYE_OF_ENDER, 1, 0, "§6Kickez un joueur.", "§d§oCliquez pour kick un joueur."));
		FactionGUIButton leaderButton = new FactionGUIButton(true, new BetterItem(Material.DIAMOND_CHESTPLATE, 1, 0, "§6Désignez un leader.", "§d§oCliquez pour désigner le leader", "§d§ode votre faction."));
		FactionGUIButton unrankButton = new FactionGUIButton(true, new BetterItem(Material.WEB, 1, 0, "§6Dégrade un joueur.", "§d§oCliquez ici pour dégrader un joueur."));
		FactionGUIButton nameButton = new FactionGUIButton(true, new BetterItem(Material.NAME_TAG, 1, 0, "§6Changez de nom de faction.", "§d§oCliquez puis entrez un nouveau nom."));
		FactionGUIButton promoteButton = new FactionGUIButton(true, new BetterItem(Material.NETHER_STAR, 1, 0, "§6Promouvoir un joueur.", "§d§oCliquez ici pour promouvoir un joueur."));
		FactionGUIButton unallyButton = new FactionGUIButton(true, new BetterItem(Material.REDSTONE_BLOCK, 1, 0, "§6Supprimez une alliance.", "§d§oCliquez puis entrez le nom d'une", "§d§ofaction dans le chat pour supprimer une alliance."));
		FactionGUIButton withdrawButton = new FactionGUIButton(true, new BetterItem(Material.GOLD_INGOT, 1, 0, "§6Récupérez de l'argent.", "§d§oCliquez puis entrez la somme d'argent", "§d§oà récupérer de la banque", "§d§ode votre faction."));
		
		MenuOpenerButton removeInvite = new MenuOpenerButton(new BetterItem(Material.REDSTONE_TORCH_ON, 1, 0, "§6Supprimez une invitation.", "§d§oCliquez pour supprimer les invitations", "§d§ode joueurs dans votre faction."), new InviteMenu("§cSupprimez une invitation"));
		MenuOpenerButton joinFaction = new MenuOpenerButton(new BetterItem(Material.CAKE, 1, 0, "§6Rejoindre une faction.", "§d§oCliquez pour rejoindre une faction."), new JoinFactionMenu("Choisissez une faction à rejoindre"));
		MenuOpenerButton listFaction = new MenuOpenerButton(new BetterItem(Material.HOPPER, 1, 0, "§6Liste des factions.", "§d§oCliquez pour afficher la liste des factions."), new ListFactionMenu(/*Bukkit.getPlayer(viewers.get(viewers.size() - 1)), */"Liste des factions"));
		
		allyButton.setCommand("faction", new String[] {"ally", "%value%"});
		allyButton.setMenuAndItemName("Alliez vous à une faction", "Nom de faction/joueur");
		coLeadButton.setCommand("faction", new String[] {"coleader", "%value%"});
		coLeadButton.setMenuAndItemName("Désignez un co-leader", "Nom du joueur");
		createFac.setCommand("faction", new String[] {"create", "%value%"});
		createFac.setMenuAndItemName("Tapez le nom de votre faction", "Nom de faction");
		depositMoney.setCommand("faction", new String[] {"deposit", "%value%"});
		depositMoney.setMenuAndItemName("Déposez de l'argent", "Entrez un nombre");
		inviteButton.setCommand("faction", new String[] {"invite", "%value%"});
		inviteButton.setMenuAndItemName("Invitez un joueur", "Nom du joueur");
		kickButton.setCommand("faction", new String[] {"kick", "%value%"});
		kickButton.setMenuAndItemName("Kickez un joueur", "Nom du joueur");
		leaderButton.setCommand("faction", new String[] {"leader", "%value%"});
		leaderButton.setMenuAndItemName("Désigner un leader", "Nom du joueur");
		unrankButton.setCommand("faction", new String[] {"demote", "%value%"});
		unrankButton.setMenuAndItemName("Dérank un joueur", "Nom du joueur");
		nameButton.setCommand("faction", new String[] {"name", "%value%"});
		nameButton.setMenuAndItemName("Renommer sa faction", "Nom de faction");
		promoteButton.setCommand("faction", new String[] {"promote", "%value%"});
		promoteButton.setMenuAndItemName("Promouvoir un joueur", "Nom du joueur");
		unallyButton.setCommand("faction", new String[] {"unally", "%value%"});
		unallyButton.setMenuAndItemName("Se déallier à un faction", "Nom de faction/joueur");
		withdrawButton.setCommand("faction", new String[] {"withdraw", "%value%"});
		withdrawButton.setMenuAndItemName("Retirer de l'argent", "Entrez un nombre");
		
		FactionGUIButton claimChunkButton = new FactionGUIButton(false, new BetterItem(Material.JACK_O_LANTERN, 1, 0, "§6Claim un chunk.", "§d§oCliquez pour claim un chunk (16x16)."));
		FactionGUIButton chatChangerButton = new FactionGUIButton(false, new BetterItem(Material.PAPER, 1, 0, "§6Changez de chat.", "§d§oCliquez pour changer de chat."));
		FactionGUIButton claimRodButton = new FactionGUIButton(false, new BetterItem(Material.BLAZE_ROD, 1, 0, "§6Récupérez une " + ConfigurationService.CLAIMING_WAND_NAME.toLowerCase() + "§6.", "§d§oVous recevrez une baguette de claim qui", "§d§ovous permet de claim."));
		FactionGUIButton disbandButton = new FactionGUIButton(false, new BetterItem(Material.TNT, 1, 0, "§6Dissoudre votre faction.", "§d§oCliquez pour supprimer votre faction."));
		FactionGUIButton fHomeButton = new FactionGUIButton(false, new BetterItem(Material.ENDER_PORTAL, 1, 0, "§6Se téléporter au f home.", "§d§oCliquez pour vous téléporter à votre home."));
		FactionGUIButton leaveButton = new FactionGUIButton(false, new BetterItem(Material.WOOD_DOOR, 1, 0, "§6Quittez votre faction.", "§d§oCliquez pour quitter votre faction."));
		FactionGUIButton mapButton = new FactionGUIButton(false, new BetterItem(Material.BEACON, 1, 0, "§6Faction map.", "§d§oCliquez pour afficher le faction map."));
		FactionGUIButton openButton = new FactionGUIButton(false, new BetterItem(Material.BOAT, 1, 0, "§6Ouvre/Ferme votre faction.", "§d§oCliquez ici pour ouvrir/fermer votre faction."));
		FactionGUIButton sethomeButton = new FactionGUIButton(false, new BetterItem(Material.NOTE_BLOCK, 1, 0, "§6Défini l'home de votre faction.", "§d§oCliquez ici pour définir l'home", "§d§ode votre faction."));
		FactionGUIButton stuckButton = new FactionGUIButton(false, new BetterItem(Material.COMPASS, 1, 0, "§6Vous téléporte à un endroit en sécurité.", "§d§oCliquez ici pour vous téléporter", "§d§oa des coordonées non claim."));
		FactionGUIButton unclaimButton = new FactionGUIButton(false, new BetterItem(Material.GOLD_SPADE, 1, 0, "§6Supprimez un claim.", "§d§oCliquez pour supprimer le claim sur", "§d§olequel vous vous trouvez."));
		
		chatChangerButton.setCommand("faction", new String[] {"chat"});
		claimRodButton.setCommand("faction", new String[] {"claim"});
		claimChunkButton.setCommand("faction", new String[] {"claimchunk"});
		disbandButton.setCommand("faction", new String[] {"disband"});
		fHomeButton.setCommand("faction", new String[] {"home"});
		leaveButton.setCommand("faction", new String[] {"leave"});
		mapButton.setCommand("faction", new String[] {"map"});
		openButton.setCommand("faction", new String[] {"open"});
		sethomeButton.setCommand("faction", new String[] {"sethome"});
		stuckButton.setCommand("faction", new String[] {"stuck"});
		unclaimButton.setCommand("faction", new String[] {"unclaim"});
		
		buttons.put(35, allyButton);
		buttons.put(9, coLeadButton);
		buttons.put(3, chatChangerButton);
		buttons.put(52, claimRodButton);
		buttons.put(22, createFac);
		buttons.put(53, claimChunkButton);
		buttons.put(8, removeInvite);
		buttons.put(0, depositMoney);
		buttons.put(7, inviteButton);
		buttons.put(31, disbandButton);
		buttons.put(46, fHomeButton);
		buttons.put(16, joinFaction);
		buttons.put(17, kickButton);
		buttons.put(10, leaderButton);
		buttons.put(39, leaveButton);
		buttons.put(51, listFaction);
		buttons.put(49, mapButton);
		buttons.put(27, unrankButton);
		buttons.put(5, nameButton);
		buttons.put(28, promoteButton);
		buttons.put(13, openButton);
		buttons.put(45, sethomeButton);
		buttons.put(47, stuckButton);
		buttons.put(34, unallyButton);
		buttons.put(41, unclaimButton);
		buttons.put(1, withdrawButton);
		buttons.put(2, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 5, "§6")));
		buttons.put(4, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 5, "§6")));
		buttons.put(6, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 5, "§6")));
		buttons.put(11, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 5, "§6")));
		buttons.put(12, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 5, "§6")));
		buttons.put(14, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 5, "§6")));
		buttons.put(15, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 5, "§6")));
		buttons.put(18, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 2, "§6")));
		buttons.put(19, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 2, "§6")));
		buttons.put(20, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 2, "§6")));
		buttons.put(21, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 2, "§6")));
		buttons.put(23, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 14, "§6")));
		buttons.put(24, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 14, "§6")));
		buttons.put(25, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 14, "§6")));
		buttons.put(26, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 14, "§6")));
		buttons.put(29, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 2, "§6")));
		buttons.put(30, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 2, "§6")));
		buttons.put(32, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 14, "§6")));
		buttons.put(33, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 14, "§6")));
		buttons.put(36, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 2, "§6")));
		buttons.put(37, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 2, "§6")));
		buttons.put(38, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 2, "§6")));
		buttons.put(40, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 3, "§6")));
		buttons.put(42, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 14, "§6")));
		buttons.put(43, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 14, "§6")));
		buttons.put(44, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 14, "§6")));
		buttons.put(48, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 3, "§6")));
		buttons.put(50, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 3, "§6")));
	}
	
	@Override
	public void setup() {
		inventory.clear();
		for (Entry<Integer, Button> entry : buttons.entrySet())
			inventory.setItem(entry.getKey(), createItemStack(null, entry.getValue()));
	}
	
	public class FactionGUIButton extends DisplayButton {

		private boolean openMenu;
		private String command, menuName, itemName;
		private String[] args;
		
		public FactionGUIButton(boolean openMenu, BetterItem item) {
			super(item);
			
			this.openMenu = openMenu;
		}
		
		public void setCommand(String command, String[] args) {
			this.command = command;
			this.args = args;
		}
		
		public void setMenuAndItemName(String menuName, String itemName) {
			openMenu = true;
			this.menuName = menuName;
			this.itemName = itemName;
		}
		
		@Override
		public void click(PacketMenu menu, Player clicker, ItemStack current, InventoryClickEvent event) {
			event.setCancelled(true);
			if (openMenu) {
				new AnvilInputMenu(clicker.getUniqueId(), "LE SEXE", menuName, itemName) {
					@Override
					public void onEvent(AnvilClickEvent event) {
						boolean destroy = false;
						
						if (event.getSlot() == AnvilSlot.OUTPUT) {
							String[] argsCloned = Arrays.copyOf(args, args.length);

							for (int i = 0; i < args.length; i++) {
								if (args[i].equalsIgnoreCase("%value%"))
									argsCloned[i] = args[i].replace("%value%", event.getInput());
							}
							
							destroy = commandManager.forceCommand(event.getClicker(), command, argsCloned);
							FactionGUIMenu.this.open(APIPlayer.getPlayer(clicker));
							event.setWillDestroy(destroy);
						}
					}
				}.open(APIPlayer.getPlayer(clicker));
			} else
				commandManager.forceCommand(clicker, command, args);
		}
		
	}
	
	private class InviteMenu extends PacketMenu {
		
		private PlayerFaction faction;

		public InviteMenu(String title) {
			super("invite_menu_" + title, title, MAX_SIZE, true);
		}

		@Override
		public void open(APIPlayer apiPlayer) {
			super.open(apiPlayer);

			faction = FPlayer.getPlayer(apiPlayer.getUUID()).getFaction();
		}
		
		@Override
		public void registerItems() {
			List<String> invites = faction.getInvitedPlayers();
			
			Collections.sort(invites);
			
			for (String invited : invites) {
				OfflinePlayer offline = Bukkit.getOfflinePlayer(UUIDCache.getUUID(invited));
				String realName = Methods.getRealName(offline);
				FactionGUIButton button = new FactionGUIButton(false, new BetterItem(1, realName, Lists.newArrayList("§7§oCliquez pour supprimer l'invitation de §d" + realName), offline));
				
				button.setCommand("faction", new String[] {"deinvite", realName});
				
				if (buttons.size() - 1 < size)
					buttons.put(buttons.size(), button);
			}
			buttons.put(size - 1, new BackButton(FactionGUIMenu.this));
		}
	}
	
	private class JoinFactionMenu extends PacketMenu {
		
		private String playerName;
		
		public JoinFactionMenu(String title) {
			super("faction_join_" + title, title, MAX_SIZE, true);
		}
		
		@Override
		public void open(APIPlayer apiPlayer) {
			super.open(apiPlayer);
			
			playerName = apiPlayer.getName();
		}

		@Override
		public void registerItems() {
			List<String> factions = PlayerFaction.getPlayersFaction().values().stream().filter(faction -> faction.getInvitedPlayers().contains(playerName)).map(PlayerFaction::getName).collect(Collectors.toCollection(() -> new ArrayList<String>(PlayerFaction.getPlayersFaction().size())));
			
			Collections.sort(factions);
			
			for (String faction : factions) {
				FactionGUIButton button = new FactionGUIButton(false, new BetterItem(Material.BEACON, 1, 0, faction, "§f§oCliquez pour rejoindre §d§o" + faction));
				
				button.setCommand("faction", new String[] {"join", faction});
				
				if (buttons.size() - 1 < size)
					buttons.put(buttons.size(), button);
			}
			buttons.put(size - 1, new BackButton(FactionGUIMenu.this));
		}
		
	}
	
	private class ListFactionMenu extends PacketMenu {
		
//		private Player viewer;
		
		public ListFactionMenu(/*Player viewer, */String title) {
			super("faction_list_" + title, title, MIN_SIZE * 4, true);
		}

		@Override
		public void registerItems() {
			for (int i = 0; i < (int) Math.ceil(Double.valueOf(PlayerFaction.getPlayersFaction().size()) / Double.valueOf(FactionListArgument.MAX_FACTION_PER_PAGE)); i++) {
				BetterItem item = new BetterItem(Material.PAPER, 1, 0, "Liste des faction " + i);
				FactionGUIButton button = new FactionGUIButton(false, item);
				
				button.setCommand("faction", new String[] {"list", String.valueOf(i)});
				
				for (BaseComponent[] components : ((FactionListArgument) commandManager.getFactionExecutor().getArgument("list")).factionList(null, i)) {
					StringBuilder builder = new StringBuilder();
					
					for (BaseComponent comp : components)
						builder.append(comp.toPlainText());
					
					item.addLore(builder.toString());
				}
				
				if (buttons.size() - 1 < size)
					buttons.put(buttons.size(), button);
			}
			buttons.put(size - 1, new BackButton(FactionGUIMenu.this));
		}
		
	}
	
}
