package com.sollace.unicopia.power;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.sollace.unicopia.PlayerExtension;
import com.sollace.unicopia.Race;
import com.sollace.unicopia.Unicopia;
import com.sollace.unicopia.entity.EntityCloud;

public abstract class PowerCloudBase extends Power<PowerCloudBase.PegasusData> {
	
	public PowerCloudBase(String cat, String name, int key) {
		super(cat, name, key);
	}
	
	public PowerCloudBase(String name, int key) {
		super(name, key);
	}
	
	public int getWarmupTime(PlayerExtension player) {
		return 10;
	}
	
	public int getCooldownTime(PlayerExtension player) {
		return 50;
	}
	
	public boolean canActivate(World w, EntityPlayer player) {
		return true;
	}

	public boolean canUse(Race playerSpecies) {
		return playerSpecies.canInteractWithClouds();
	}

	public PegasusData tryActivate(EntityPlayer player, World w) {
		switch (player.inventory.currentItem) {
    		case 0: return new PegasusData(1);
    		case 1: return new PegasusData(2);
		}
		return null;
	}

	public PegasusData fromBytes(ByteBuf buf) {
		PegasusData result = new PegasusData();
		result.fromBytes(buf);
		return result;
	}

	public void apply(EntityPlayer player, PegasusData data) {
		if (player.isRiding() && player.getRidingEntity() instanceof EntityCloud) {
			switch (data.type) {
				case 1: ((EntityCloud)player.getRidingEntity()).setIsRaining(!((EntityCloud)player.getRidingEntity()).getIsRaining());
					break;
				case 2: ((EntityCloud)player.getRidingEntity()).Thunder();
					break;
			}
		} else {
			for (Entity i : getWithinRange(player, 4)) {
				if (i.getClass() == EntityCloud.class) {
					switch (data.type) {
						case 1: ((EntityCloud)i).setIsRaining(!((EntityCloud)i).getIsRaining());
							break;
						case 2: ((EntityCloud)i).Thunder();
							break;
					}
				}
			}
		}
	}
	
	public void preApply(EntityPlayer player) {}
	
	public void postApply(EntityPlayer player) {
		spawnParticles(Unicopia.Particles.unicorn.getData(), player, 1);
	}
	
	protected class PegasusData implements IData {
		public int type;
		
		public PegasusData() {}
		
		public PegasusData(int t) {
			type = t;
		}
		
		public void toBytes(ByteBuf buf) {
			buf.writeInt(type);
		}
		
		public void fromBytes(ByteBuf buf) {
			type = buf.readInt();
		}
	}
}
