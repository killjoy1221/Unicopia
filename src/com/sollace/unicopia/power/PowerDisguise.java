package com.sollace.unicopia.power;

import com.blazeloader.api.particles.ApiParticles;
import com.blazeloader.api.particles.ParticleData;
import com.sollace.unicopia.PlayerExtension;
import com.sollace.unicopia.Race;
import com.sollace.unicopia.disguise.Disguise;
import com.sollace.unicopia.server.PlayerSpeciesRegister;
import com.sollace.util.Util;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class PowerDisguise extends Power<Power.EmptyData> {

	public PowerDisguise(String name, int key) {
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
		Entity looked = getLookedAtEntity(player, 10);
		if (Util.DISGUISABLE.apply(looked) && !PlayerExtension.get(player).getDisguise().match(looked)) {
			return new EmptyData();
		}
		return null;
	}

	public EmptyData fromBytes(ByteBuf buf) {
		return new EmptyData();
	}
	
	public void apply(EntityPlayer player, EmptyData data) {
		//PlayerExtension.get(player).getDisguise().setPlayer(PlayerIdent.create("Notch"));
		Entity looked = getLookedAtEntity(player, 10);
		if (Util.DISGUISABLE.apply(looked) && !PlayerExtension.get(player).getDisguise().match(looked)) {
			if (looked instanceof EntityPlayer) {
				if ((looked = resolvePlayer(player, (EntityPlayer)looked)) == null) {
					return;
				}
			}
			PlayerExtension.get(player).setDisguise((EntityLivingBase)looked);
			player.world.playSound(player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.PLAYERS, 0.5f, 0.5f, true);
		}
	}
	
	private EntityLivingBase resolvePlayer(EntityPlayer player, EntityPlayer looked) {
		if (PlayerSpeciesRegister.getPlayerSpecies(looked) == Race.CHANGELING) {
			Disguise disguise = PlayerExtension.get(looked).getDisguise();
			if (disguise.isActive()) {
				if (!disguise.isPlayer()) {
					return disguise.getEntity();
				} else if (!disguise.getPlayer().getGameProfile().getName().equalsIgnoreCase(player.getName())) {
					PlayerExtension.get(player).getDisguise().setPlayer(disguise.getPlayer());
					player.world.playSound(player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.PLAYERS, 0.5f, 0.5f, true);
					return null;
				}
			}
		}
		return looked;
	}

	public void preApply(EntityPlayer player) {}

	public void postApply(EntityPlayer player) {
		if (player.world.rand.nextInt(200) < 120) {
			ParticleData particle = ParticleData.get(EnumParticleTypes.CRIT_MAGIC, true);
			for (int i = 0; i < 5; i++) {
				particle.setPos(
						player.posX + player.world.rand.nextFloat()*2 - 1,
						(player.posY) + player.world.rand.nextFloat()*2 - 1,
						player.posZ + player.world.rand.nextFloat()*2 - 1);
				particle.setVel(0, 0.25, 0);
				ApiParticles.spawnParticle(particle, player.world);
			}
			particle = ParticleData.get(EnumParticleTypes.FLAME, true);
			for (int i = 0; i < 5; i++) {
				particle.setPos(
						player.posX + player.world.rand.nextFloat()*2 - 1,
						(player.posY) + player.world.rand.nextFloat()*2 - 1,
						player.posZ + player.world.rand.nextFloat()*2 - 1);
				particle.setVel(0, 0.25, 0);
				ApiParticles.spawnParticle(particle, player.world);
			}
		}
	}
}
