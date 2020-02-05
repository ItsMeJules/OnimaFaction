package net.onima.onimafaction.faction.struct;

import java.util.List;

import org.bukkit.Material;

import com.google.common.collect.Lists;

public enum EggAdvantageType {

	F_DTR(2500, 0.5, 5, Material.WEB, "§7Booste le DTR de votre faction",
			Lists.newArrayList("§eNiveau : §c%level%",
			"§ePrix : §f§o%price%",
			"§eDescription : §6Ajoute un peu de DTR à votre",
			"§6faction pour chaque niveau acquit.",
			"",
			"§7Niveau §eI §7: §a+0.5",
			"§7Niveau §eII §7: §a+1.0",
			"§7Niveau §eIII §7: §a+1.5",
			"§7Niveau §eIV §7: §a+2.0",
			"§7Niveau §eV §7: §a+2.5",
			"",
			"§3Niveau maximum : §cV")), //Plus de DTR dans la faction
	
	DEATHBAN(10000, 1 / 3, 1, Material.SIGN, "§cRéduction du deathban",
			Lists.newArrayList("§eNiveau : §c%level%",
					"§ePrix : §f§o%price%",
					"§eDescription : §6Réduit le deathban des",
					"§6membres de la faction de §a1/3§6.",
					"",
					"§3Niveau max : §cI")), //Un deathban moins long
	
	CROPS(2000, 2, 3, Material.WHEAT, "§aBooste vos fermes",
			Lists.newArrayList("§eNiveau : §c%level%",
			"§ePrix : §f§o%price%",
			"§eDescription : §6Augmente la vitesse de spawn",
			"§6de vos fermes si ces derniers sont",
			"§6à côté d'un block de diamant.",
			"",
			"§7Niveau §eI §7: §ax2",
			"§7Niveau §eII §7: §ax3",
			"§7Niveau §eIII §7: §ax4",
			"",
			"§3Niveau max : §cIII")),
	
	DTR_PD_IN_CLAIMS(5000, 0.25, 2, Material.REDSTONE_TORCH_ON, "§9Réduit la perte de DTR dans les claims",
			Lists.newArrayList("§eNiveau : §c%level%",
					"§ePrix : §f§o%price%",
					"§eDescription : §6Réduit la perte de DTR",
					"§6pour chaque membre de la faction",
					"§6lorsque vous mourez dans vos claims.",
					"",
					"§7Niveau §eI §7: §a1/4",
					"§7Niveau §eII §7: §a1/2",
					"",
					"§3Niveau max : §cII")), //Moins de perte de DTR par mort
	
	DTR_REGEN(10000, 2, 2, Material.GHAST_TEAR, "§dAugmente la vitesse de regen",
			Lists.newArrayList("§eNiveau : §c%level%",
					"§ePrix : §f§o%price%",
					"§eDescription : §6Augmente la vitesse de regen",
					"§6de DTR de la faction.",
					"",
					"§7Niveau §eI §7: §ax2",
					"§7Niveau §eII §7: §ax3",
					"",
					"§3Niveau max : §cII")), //DTR regen plus vite
	
	KILL_MONEY_MULTP(1000, 0.25, 8, Material.DIAMOND_SWORD, "§bMultiplie l'argent gagné par kills", 
			Lists.newArrayList("§eNiveau : §c%level%",
					"§ePrix : §f§o%price%",
					"§eDescription : §6Augmente le gain d'argent",
					"§6gagné lorsqu'un membre de la faction",
					"§6tue un joueur.",
					"",
					"§7Niveau §eI §7: §ax1.25",
					"§7Niveau §eII §7: §ax1.50",
					"§7Niveau §eIII §7: §ax1.75",
					"§7Niveau §eIV §7: §ax2",
					"§7Niveau §eV §7: §ax2.25",
					"§7Niveau §eVI §7: §ax2.50",
					"§7Niveau §eVII §7: §ax2.75",
					"§7Niveau §eVIII §7: §ax3",
					"",
					"§3Niveau max : §cVIII")); //On gagne plus d'argent par kill.
	
	private double initialPrice;
	private double changer;
	private double maxLevel;
	private Material material;
	private String name;
	private List<String> lore;
	
	private EggAdvantageType(double initialPrice, double changer, double maxLevel, Material material, String name, List<String> lore) {
		this.initialPrice = initialPrice;
		this.changer = changer;
		this.maxLevel = maxLevel;
		this.material = material;
		this.name = name;
		this.lore = lore;
	}

	public double getInitialPrice() {
		return initialPrice;
	}

	public double getChanger() {
		return changer;
	}
	
	public double getMaxLevel() {
		return maxLevel;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public String getName() {
		return name;
	}

	public List<String> getLore() {
		return lore;
	}
}
