package com.sollace.unicopia.disguise;

import com.blazeloader.util.data.INBTWritable;
import com.blazeloader.util.playerinfo.PlayerIdent;
import com.sollace.unicopia.server.PlayerSpeciesRegister;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class Disguise implements INBTWritable {
	
	private EntityLivingBase disguiseEntity;
	private PlayerIdent disguisePlayer;
	
	public void set(EntityPlayer player, EntityLivingBase entity) {
		if (entity instanceof EntityPlayer) {
			disguiseEntity = null;
			disguisePlayer = PlayerIdent.create((EntityPlayer)entity);
		} else {
			disguisePlayer = null;
			if (entity == null) {
				disguiseEntity = null;
				return;
			}
			NBTTagCompound copy = new NBTTagCompound();
			entity.writeToNBT(copy);
			copy.setBoolean("Invulnerable", true);
			disguiseEntity = (EntityLivingBase)EntityList.createEntityByID(EntityList.getEntityID(entity), player.worldObj);
			disguiseEntity.copyDataFromOld(entity);
			disguiseEntity.setCustomNameTag(player.getDisplayName().getFormattedText());
			disguiseEntity.setAlwaysRenderNameTag(true);
			if (disguiseEntity instanceof EntityEnderman) {
				((EntityEnderman) disguiseEntity).setHeldBlockState(Blocks.air.getDefaultState());
			}
		}
	}
	
	public void tick(EntityPlayer player) {
		if (!isPlayer()) {
			disguiseEntity.isDead = player.isDead;
			disguiseEntity.setHealth(player.getHealth());
			disguiseEntity.setAir(player.getAir());
			disguiseEntity.setFire(player.isBurning() ? 1 : 0);
			disguiseEntity.posY = player.posY;
			disguiseEntity.onGround = player.onGround;
			disguiseEntity.fallDistance = 0;
			disguiseEntity.setWorld(player.worldObj);
			disguiseEntity.setArrowCountInEntity(player.getArrowCountInEntity());
			ItemStack[] inv = player.getInventory();
			for (int i = 0; i < inv.length; i++) {
				disguiseEntity.setCurrentItemOrArmor(i + 1, inv[i]);
			}
			disguiseEntity.setCurrentItemOrArmor(0, player.getHeldItem());
			if (disguiseEntity instanceof EntityLiving) {
				EntityLiving disguise = (EntityLiving) disguiseEntity;
				if (player.isEntityAlive() && disguise.worldObj.rand.nextInt(1000) < disguise.livingSoundTime++) {
		            disguise.livingSoundTime = -disguise.getTalkInterval();
		            disguise.playLivingSound();
		        }
			}
			disguiseEntity.setPosition(player.posX, -30, player.posZ);
			disguiseEntity.onUpdate();
		}
	}
	
	public void unset() {
		disguiseEntity = null;
		disguisePlayer = null;
	}
	
	public void setPlayer(PlayerIdent ident) {
		disguisePlayer = ident;
		disguiseEntity = null;
	}
	
	public EntityLivingBase getEntity() {
		return disguiseEntity;
	}
	
	public PlayerIdent getPlayer() {
		return disguisePlayer;
	}
	
	public boolean isPlayer() {
		return disguisePlayer != null;
	}
	
	public boolean isActive() {
		return disguiseEntity != null || isPlayer();
	}
	
	public boolean canFly() {
		if (isPlayer()) {
			return PlayerSpeciesRegister.getPlayerSpecies(getPlayer().getUniqueID()).canFly();
		}
		return disguiseEntity == null || disguiseEntity instanceof EntityFlying || disguiseEntity instanceof EntityDragon;
	}
	
	public boolean match(Entity looked) {
		if (looked == null && isActive()) return false;
		if (looked instanceof EntityPlayer && isPlayer()) {
			return ((EntityPlayer) looked).getGameProfile().getId().equals(getPlayer().getUniqueID());
		}
		return false;
	}
	
	public void writeToNBT(NBTTagCompound compound) {
		if (isPlayer()) {
			disguisePlayer.writeToNBT(compound);
		} else {
			NBTTagCompound entity = new NBTTagCompound();
			disguiseEntity.writeToNBTOptional(entity);
			compound.setTag("Entity", entity);
		}
	}
	
	public void readFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("Entity")) {
			disguiseEntity = (EntityLivingBase)EntityList.createEntityFromNBT(compound.getCompoundTag("Entity"), null);
			disguisePlayer = null;
		} else {
			disguiseEntity = null;
			disguisePlayer = PlayerIdent.create(compound);
		}
	}
}
