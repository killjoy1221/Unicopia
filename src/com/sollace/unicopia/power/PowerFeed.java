package com.sollace.unicopia.power;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

import com.blazeloader.api.particles.ApiParticles;
import com.blazeloader.api.particles.ParticleData;
import com.sollace.unicopia.PlayerExtension;
import com.sollace.unicopia.Race;
import com.sollace.util.MagicalDamageSource;

public class PowerFeed extends Power<Power.EmptyData> {

	public PowerFeed(String name, int key) {
		super(name, key);
	}
	
	public int getWarmupTime(PlayerExtension player) {
		return 20;
	}
	
	public int getCooldownTime(PlayerExtension player) {
		return 50;
	}
	
	public boolean canActivate(World w, EntityPlayer player) {
		return true; 
	}
	
	public boolean canUse(Race playerSpecies) {
		return playerSpecies == Race.CHANGELING;
	}

	public EmptyData tryActivate(EntityPlayer player, World w) {
		if (player.getHealth() < player.getMaxHealth() || player.canEat(false)) {
			Entity i = getLookedAtEntity(player, 10);
			if (i != null && canDrain(i)) {
				return new EmptyData();
			}
		}
		return null;
	}

	public EmptyData fromBytes(ByteBuf buf) {
		return new EmptyData();
	}
	
	private boolean canDrain(Entity e) {
		return e instanceof EntityCow || e instanceof EntityVillager || e instanceof EntityPlayer || EnumCreatureType.MONSTER.getCreatureClass().isAssignableFrom(e.getClass());
	}
	
	public void apply(EntityPlayer player, EmptyData data) {
		List<Entity> list = new ArrayList();
		for (Entity i : getWithinRange(player, 3)) {
			if (canDrain(i)) list.add(i);
		}
		Entity looked = getLookedAtEntity(player, 10);
		if (looked != null && !list.contains(looked)) {
			list.add(looked);
		}
		float lostHealth = player.getMaxHealth() - player.getHealth();
		if (lostHealth > 0 || player.canEat(false)) {
			float totalDrained = (lostHealth < 2 ? lostHealth : 2);
			float drained = totalDrained / list.size();
			for (Entity i : list) {
				DamageSource d = MagicalDamageSource.causePlayerDamage("feed", player);
				if (EnumCreatureType.CREATURE.getCreatureClass().isAssignableFrom(i.getClass()) || player.worldObj.rand.nextFloat() > 0.95f) {
					i.attackEntityFrom(d, Integer.MAX_VALUE);
				} else {
					i.attackEntityFrom(d, drained);
				}
			}
			if (lostHealth > 0) {
				player.getFoodStats().addStats(3, 0.125f);
				player.heal(totalDrained);
			} else {
				player.getFoodStats().addStats(3, 0.25f);
			}
			if (player.worldObj.rand.nextFloat() > 0.9f) {
				player.addPotionEffect(new PotionEffect(Potion.wither.id, 20, 1));
			}
			if (player.worldObj.rand.nextFloat() > 0.4f) {
				player.removePotionEffect(Potion.confusion.id);
			}
		}
	}

	public void preApply(EntityPlayer player) {}

	public void postApply(EntityPlayer player) {
		ParticleData particle = ParticleData.get(EnumParticleTypes.HEART, true);
		for (int i = 0; i < 10; i++) {
			particle.setPos(
					player.posX + player.worldObj.rand.nextFloat()*2 - 1,
					(player.posY) + player.worldObj.rand.nextFloat()*2 - 1,
					player.posZ + player.worldObj.rand.nextFloat()*2 - 1);
			particle.setVel(0, 0.25, 0);
			ApiParticles.spawnParticle(particle, player.worldObj);
		}
	}
}
