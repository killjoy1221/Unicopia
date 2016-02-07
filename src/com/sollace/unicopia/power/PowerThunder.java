package com.sollace.unicopia.power;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class PowerThunder extends PowerCloudBase {

	public PowerThunder(String name, int key) {
		super(name, key);
	}
	
	public PegasusData tryActivate(EntityPlayer player, World w) {
		return new PegasusData(2);
	}
}
