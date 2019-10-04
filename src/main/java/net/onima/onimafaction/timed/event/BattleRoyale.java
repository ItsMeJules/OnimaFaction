package net.onima.onimafaction.timed.event;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;

import net.onima.onimaapi.OnimaAPI;
import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaapi.utils.OSound;
import net.onima.onimaapi.utils.WorldBorder;
import net.onima.onimaapi.utils.time.Time;
import net.onima.onimafaction.OnimaFaction;
import net.onima.onimafaction.cooldowns.InsideStormCooldown;
import net.onima.onimafaction.events.battle_royale.BattleRoyalePhaseFreezeEvent;
import net.onima.onimafaction.events.battle_royale.BattleRoyalePhaseShrinkEvent;
import net.onima.onimafaction.events.battle_royale.BattleRoyalePhaseStartEvent;
import net.onima.onimafaction.events.battle_royale.BattleRoyalePhaseStopEvent;
import net.onima.onimafaction.events.battle_royale.BattleRoyaleStartEvent;
import net.onima.onimafaction.events.battle_royale.BattleRoyaleStopEvent;
import net.onima.onimafaction.events.server_event.FactionEventServerStartEvent;
import net.onima.onimafaction.timed.FactionServerEvent;

public class BattleRoyale extends BukkitRunnable implements FactionServerEvent {

	private static OnimaAPI plugin;

	static {
		plugin = OnimaAPI.getInstance();
	}
	
	private List<PotionEffect> stormEffects;
	private List<UUID> playersInStorm;
	private List<Phase> phases;
	private OSound enteringStormSound, insideStormSound, leavingStormSound;
	private Phase runningPhase;
	private int amplifierIncreaser;
	
	{
		stormEffects = Lists.newArrayList(new PotionEffect(PotionEffectType.WITHER, 999999, 0), new PotionEffect(PotionEffectType.HUNGER, 99999, 1));
		playersInStorm = new ArrayList<>();
		phases = Lists.newArrayList(new Phase(1, 10 * Time.MINUTE, 1.25), new Phase(2, 9 * Time.MINUTE, 1000 / (9 * Time.MINUTE)), new Phase(3, 7 * Time.MINUTE, 1500 / (7 * Time.MINUTE)), new Phase(4, 4 * Time.MINUTE, 1750 / (4 * Time.MINUTE)));
		enteringStormSound = new OSound(Sound.PORTAL_TRIGGER, 2F, 1F);
		leavingStormSound = new OSound(Sound.ZOMBIE_UNFECT, 0.1F, 2F);
		insideStormSound = new OSound(Sound.MINECART_INSIDE, 2F, 0.05F);
		amplifierIncreaser = 1;
	}
	
	public void start() {
		FactionEventServerStartEvent startEvent = new FactionEventServerStartEvent(this);
		Bukkit.getPluginManager().callEvent(startEvent);
		
		if (startEvent.isCancelled())
			return;
		
		OnimaFaction.getInstance().setFactionServerEvent(this);
		runTaskTimerAsynchronously(plugin, 0L, 20L);
		
		BattleRoyaleStartEvent event = new BattleRoyaleStartEvent();
		Bukkit.getPluginManager().callEvent(event);
		
		if (event.isCancelled()) {
			stop();
			return;
		}
		
		HandlerList.unregisterAll(plugin.getListenerManager().getWorldBorderListener());
		
		phases = sortPhases();
		runningPhase = phases.get(0);
	}
	
	public void stop() {
		OnimaFaction.getInstance().setFactionServerEvent(null);
		cancel();
	}
	
	@Override
	public void run() {
		if (runningPhase != null) {
			if (!runningPhase.freeze) {
				runningPhase.shrink();
				for (APIPlayer apiPlayer : APIPlayer.getOnlineAPIPlayers()) {
					
					if (apiPlayer.getRank().getRankType().hasPermission(OnimaPerm.WORLD_BORDER_BYPASS))
						continue;
					
					boolean inStorm = playersInStorm.contains(apiPlayer.getUUID());
					
					if (WorldBorder.border(apiPlayer.toPlayer().getLocation()) && !inStorm) {
						playersInStorm.add(apiPlayer.getUUID());
						apiPlayer.startCooldown(InsideStormCooldown.class);
						
						enteringStormSound.play(apiPlayer);
						
						apiPlayer.toPlayer().addPotionEffects(stormEffects);
						apiPlayer.sendMessage("§d§oVous §cêtes dans la tempête, sortez-en au plus vite !");
					} else if (!WorldBorder.border(apiPlayer.toPlayer().getLocation()) && inStorm) {
						playersInStorm.remove(apiPlayer.getUUID());
						apiPlayer.removeCooldown(InsideStormCooldown.class);
						
						leavingStormSound.play(apiPlayer);
						
						apiPlayer.sendMessage("§d§oVous §aêtes sortit de la tempête, bonne chance !");
						
						stormEffects.stream().forEach(effect -> apiPlayer.toPlayer().removePotionEffect(effect.getType()));
					}
					
					if (inStorm && apiPlayer.getTimeLeft(InsideStormCooldown.class) <= 0L) insideStormSound.play(apiPlayer); 
				}
			}
			
			runningPhase.decreaseTime();
			
			if (runningPhase.getTimeLeft() <= 0) {
				if (runningPhase.started)
					runningPhase.freeze();
				else if (runningPhase.freeze && runningPhase.nextPhase != phases.size()) {
					BattleRoyalePhaseStopEvent event = new BattleRoyalePhaseStopEvent(runningPhase);
					Bukkit.getPluginManager().callEvent(event);
					
					if (event.isCancelled()) return;
					
					ListIterator<PotionEffect> iterator = stormEffects.listIterator();
					
					while (iterator.hasNext()) {
						PotionEffect effect = iterator.next();
						
						iterator.remove();
						iterator.add(new PotionEffect(effect.getType(), effect.getDuration(), effect.getAmplifier() + amplifierIncreaser));
					}
					
					runningPhase = phases.get(runningPhase.nextPhase);
					
					BattleRoyalePhaseStartEvent eventS = new BattleRoyalePhaseStartEvent(runningPhase, stormEffects);
					Bukkit.getPluginManager().callEvent(eventS);
					
					stormEffects = eventS.getEffects();
					
					runningPhase.started = true;
				} else if (runningPhase.freeze) {
					BattleRoyaleStopEvent event = new BattleRoyaleStopEvent();
					Bukkit.getPluginManager().callEvent(event);
					
					if (event.isCancelled()) return;
					
					Bukkit.getPluginManager().registerEvents(plugin.getListenerManager().getWorldBorderListener(), plugin);
					cancel();
				}
			}
		}
	}
	
	public List<Phase> sortPhases() {
		List<Phase> sorted = phases.parallelStream().sorted(Comparator.comparing(Phase::getTime).reversed()).collect(Collectors.toCollection((() -> new ArrayList<>(phases.size()))));
		for (int i = 0; i < sorted.size(); i++)
			sorted.get(i).nextPhase = i+1;
		
		return sorted;
	}
	
	public List<PotionEffect> getStormEffects() {
		return stormEffects;
	}
	
	public List<Phase> getPhases() {
		return phases;
	}
	
	public Optional<Phase> getPhase(int phase) {
		return phases.parallelStream().filter(x -> x.getPhase() == phase).findFirst();
	}
	
	public long phasesDuration() {
		long time = 0;
		
		for (Phase phase : phases)
			time += phase.getTime();
		
		return time * 2;
	}
	
	public double totalBlocksTraveledInShrinks() {
		double total = 0;
		
		for (Phase phase : phases)
			total += phase.blocksPerSecond * (phase.time / 1000);
		
		return total;
	}
	
	public boolean isStarted() {
		return runningPhase != null;
	}
	
	public List<UUID> getPlayersInStorm() {
		return playersInStorm;
	}
	
	public Phase getRunningPhase() {
		return runningPhase;
	}

	public static class Phase {
		
		private boolean freeze, started;
		private int phase, nextPhase;
		private double blocksPerSecond;
		private long time, startedTime, timeLeft;
		
		public Phase(int phase, long time, double blocksPerSecond) {
			this.phase = phase;
			this.time = time;
			this.blocksPerSecond = blocksPerSecond;
		}
		
		public Phase(int phase, long time) {
			this(phase, time, 0);
		}
		
		public Phase(int phase) {
			this(phase, 0);
		}
		
		public void shrink() {
			BattleRoyalePhaseShrinkEvent event = new BattleRoyalePhaseShrinkEvent(this);
			Bukkit.getPluginManager().callEvent(event);
			
			if (event.isCancelled()) return;
			
			if (!started) started = true;
			
			if (startedTime <= 0)
				startedTime = System.currentTimeMillis();
				
			for (Entry<String, Double> border : WorldBorder.getBorders().entrySet())
				border.setValue(border.getValue() - blocksPerSecond);
			
		}
		
		public void decreaseTime() {
			timeLeft = time + startedTime - System.currentTimeMillis();
		}
		
		public void freeze() {
			BattleRoyalePhaseFreezeEvent event = new BattleRoyalePhaseFreezeEvent(this);
			Bukkit.getPluginManager().callEvent(event);
			
			if (event.isCancelled()) return;
			
			if (!freeze) freeze = true;
			started = false;
			startedTime = 0;
			
			if (startedTime <= 0)
				startedTime = System.currentTimeMillis();
		}
		
		public boolean isFrozen() {
			return freeze;
		}
		
		public boolean isStarted() {
			return started;
		}
		
		public int getPhase() {
			return phase;
		}
		
		public long getTime() {
			return time;
		}
		
		public void setTime(long time) {
			this.time = time;
		}
		
		public long getStartedTime() {
			return startedTime;
		}
		
		public long getTimeLeft() {
			return timeLeft;
		}
		
		public double getBlocksPerSecond() {
			return blocksPerSecond;
		}

		public void setBlocksPerSecond(double blocksPerSecond) {
			this.blocksPerSecond = blocksPerSecond;
		}
		
		@Override
		public String toString() {
			return "Phase/"+phase+'/'+time+'/'+blocksPerSecond;
		}
		
		public static Phase fromString(String str) {
			String[] splitted = str.split("/");
			return new Phase(Methods.toInteger(splitted[1]), Methods.toInteger(splitted[2]), Methods.toDouble(splitted[3]));
		}
		
	}
	
}
