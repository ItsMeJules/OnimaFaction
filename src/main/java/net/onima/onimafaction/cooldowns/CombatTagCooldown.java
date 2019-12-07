package net.onima.onimafaction.cooldowns;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import net.onima.onimaapi.cooldown.utils.Cooldown;
import net.onima.onimaapi.event.region.PlayerRegionChangeEvent;
import net.onima.onimaapi.fakeblock.FakeType;
import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.players.OfflineAPIPlayer;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaapi.utils.time.Time;
import net.onima.onimaapi.utils.time.Time.LongTime;
import net.onima.onimaapi.zone.struct.Flag;
import net.onima.onimaapi.zone.type.Region;
import net.onima.onimafaction.events.FactionPlayerJoinEvent;
import net.onima.onimafaction.events.FactionPlayerLeaveEvent;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.claim.Claim;
import net.onima.onimafaction.faction.struct.Relation;
import net.onima.onimafaction.players.FPlayer;
import net.onima.onimafaction.players.OfflineFPlayer;

public class CombatTagCooldown extends Cooldown implements Listener {

	public CombatTagCooldown() {
		super("combat_tag", (byte) 1, 30 * Time.SECOND);
	}

	@Override
	public String scoreboardDisplay(long timeLeft) {
		return "§cCombat tag §6: §c" + LongTime.setHMSFormat(timeLeft);
	}
	
	@Override
	public void onStart(OfflineAPIPlayer offline) {
		super.onStart(offline);
		
		if (offline.isOnline() && getTimeLeft(offline.getUUID()) <= 0)
			((APIPlayer) offline).sendMessage("§eVous êtes maintenant en §ccombat §epour §c" + LongTime.setYMDWHMSFormat(duration) + " §e!");
	}
	
	@Override
	public void onExpire(OfflineAPIPlayer offline) {
		if (offline.isOnline()) {
			APIPlayer apiPlayer = (APIPlayer) offline;
			
			apiPlayer.sendMessage("§eVous n'êtes plus en §ccombat §e!");
			apiPlayer.removeFakeBlockByType(FakeType.COMBAT_REGION_BORDER);
		}
		
		super.onExpire(offline);
	}
	
	
	@Override
	public void onCancel(OfflineAPIPlayer offline) {
		if (offline.isOnline())
			((APIPlayer) offline).removeFakeBlockByType(FakeType.COMBAT_REGION_BORDER);
		
		super.onCancel(offline);
	}

	@Override
	public boolean action(OfflineAPIPlayer offline) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@EventHandler
	public void onFactionJoin(FactionPlayerJoinEvent event) {
		OfflineFPlayer offline = event.getOfflineFPlayer();
		long remaining = getTimeLeft(offline.getOfflineApiPlayer().getUUID());
		
		if (remaining > 0L && !event.isForceJoin()) {
			event.setCancelled(true);
			
			if (offline.getOfflineApiPlayer().isOnline())
				((FPlayer) offline).getApiPlayer().sendMessage("§cVous ne pouvez pas rejoindre de faction tant que vous êtes en combat ! Il reste " + LongTime.setHMSFormat(remaining) + '.');
		}
	}
	
	@EventHandler
	public void onFactionLeave(FactionPlayerLeaveEvent event) {
		if (event.getLeaveReason() == FactionPlayerLeaveEvent.LeaveReason.DISBAND)
			return;
		
		OfflineFPlayer offline = event.getOfflineFPlayer();
		long remaining = getTimeLeft(offline.getOfflineApiPlayer().getUUID());
		
		if (remaining > 0L) {
			event.setCancelled(true);
			
			if (event.getKicker() != null)
				event.getKicker().sendMessage("§cVous ne pouvez pas kick " + offline.getOfflineApiPlayer().getName() + " de la faction car il est en combat ! Il reste " + LongTime.setHMSFormat(remaining) + '.');
			else if (offline.getOfflineApiPlayer().isOnline())
				((FPlayer) offline).getApiPlayer().sendMessage("§cVous ne pouvez pas quitter votre faction tant que vous êtes en combat ! Il reste " + LongTime.setHMSFormat(remaining) + '.');
		}
		
	}
	
	@EventHandler
	public void onRegionEnter(PlayerRegionChangeEvent event) {
		APIPlayer apiPlayer = event.getAPIPlayer();
		
		if (getTimeLeft(apiPlayer.getUUID()) <= 0L)
			return;
		
		Region to = event.getNewRegion();
		
		if (to.hasFlag(Flag.COMBAT_TAG_DENY_ENTRY)) {
			Region from = event.getRegion();
			
			if (!from.hasFlag(Flag.COMBAT_TAG_DENY_ENTRY)) {
				event.setCancelled(true);
				apiPlayer.sendMessage("§cVous ne pouvez pas entrer dans " + to.getDisplayName(apiPlayer.toPlayer()) + " §ctant que vous êtes en combat ! Il reste " + LongTime.setHMSFormat(getTimeLeft(apiPlayer.getUUID())) + '.');
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		Player attacker = Methods.getLastAttacker(event);
		Entity entity;
		
		if (attacker != null && (entity = event.getEntity()) instanceof Player && !attacker.equals(entity)) {
			if (FPlayer.getPlayer(attacker).hasFactionBypass())
				return;
			
			if (Claim.getClaimAndRegionAt(attacker.getLocation()).hasFlag(Flag.COMBAT_SAFE)) {
				attacker.sendMessage("§cVous ne pouvez pas attaquer de joueur tant que vous êtes dans une zone où le combat est interdit !");
				event.setCancelled(true);
				return;
			} else if (Claim.getClaimAndRegionAt(entity.getLocation()).hasFlag(Flag.COMBAT_SAFE)) {
				attacker.sendMessage("§cVous ne pouvez pas attaquer " + APIPlayer.getPlayer(entity.getUniqueId()).getDisplayName() + " car il est dans une zone où le combat est interdit !");
				event.setCancelled(true);
				return;
			}
			
			FPlayer fPlayer = FPlayer.getPlayer((Player) entity);
			FPlayer fAttacker = FPlayer.getPlayer(attacker);
			PlayerFaction playerFaction = fPlayer.getFaction();
			PlayerFaction attackerFaction = fAttacker.getFaction();
				
			if (playerFaction != null && attackerFaction != null) {
				if (playerFaction.getRelation(attackerFaction) == Relation.MEMBER) {
					attacker.sendMessage("§d§oVous §7ne pouvez pas attaquer " + Relation.MEMBER.getColor() + "§o" + fPlayer.getRole().getRole() + fPlayer.getApiPlayer().getDisplayName() + "§7.");
					event.setCancelled(true);
					return;
				} else if (playerFaction.getRelation(attackerFaction) == Relation.ALLY)
					attacker.sendMessage("§d§oVous §7avez attaqué " + Relation.ALLY.getColor() + "§o" + fPlayer.getRole().getRole() + fPlayer.getApiPlayer().getDisplayName() + "§7, il est votre allié.");
			}
			
			onStart(APIPlayer.getPlayer(attacker));
			onStart(APIPlayer.getPlayer(entity.getUniqueId()));
		}
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		onCancel(APIPlayer.getPlayer(event.getPlayer()));
	}

}
