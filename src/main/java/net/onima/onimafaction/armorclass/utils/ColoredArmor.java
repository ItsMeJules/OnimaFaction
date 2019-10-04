package net.onima.onimafaction.armorclass.utils;

import java.util.List;

import org.bukkit.Color;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.Lists;

public class ColoredArmor {
	
	private static List<ColoredArmor> armors;
	
	static {
		armors = Lists.newArrayList(new ColoredArmor(Color.fromRGB(102, 127, 51), new PotionEffect(PotionEffectType.POISON, 20 * 3, 1), 0.5D),
				new ColoredArmor(Color.fromRGB(25, 25, 25), new PotionEffect(PotionEffectType.WITHER, 20 * 6, 0), 0.3D),
				new ColoredArmor(Color.fromRGB(51, 76, 178), new PotionEffect(PotionEffectType.SLOW, 20 * 3, 1), 0.6D),
				new ColoredArmor(Color.fromRGB(76, 76, 76), new PotionEffect(PotionEffectType.BLINDNESS, 20 * 6, 0), 0.4D));
	}
	
	private Color color;
	private PotionEffect effect;
	private double chance;
	
	public ColoredArmor(Color color, PotionEffect effect, double chance) {
		this.color = color;
		this.effect = effect;
		this.chance = chance;
	}

	public Color getColor() {
		return color;
	}

	public PotionEffect getEffect() {
		return effect;
	}
	
	public double getChance() {
		return chance;
	}
	
	public static ColoredArmor getFromRGB(int red, int green, int blue) {
		for (ColoredArmor colorEffect : armors) {
			Color color = colorEffect.getColor();
			
			if (color.getRed() == red && color.getGreen() == green && color.getBlue() == blue)
				return colorEffect;
		}
		return null;
	}

}
