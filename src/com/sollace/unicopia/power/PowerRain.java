package com.sollace.unicopia.power;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class PowerRain extends PowerCloudBase {

	public PowerRain(String name, int key) {
		super(name, key);
	}
	
	public PegasusData tryActivate(EntityPlayer player, World w) {
		return new PegasusData(1);
	}
}
