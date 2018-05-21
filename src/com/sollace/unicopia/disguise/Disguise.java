package com.sollace.unicopia.disguise;

import java.util.List;

import com.blazeloader.api.block.ApiBlock;
import com.blazeloader.api.entity.ApiEntity;
import com.blazeloader.util.data.INBTWritable;
import com.blazeloader.util.playerinfo.PlayerIdent;
import com.sollace.unicopia.server.PlayerSpeciesRegister;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
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
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class Disguise implements INBTWritable {
	
	private World worldObj;
	
	private EntityLivingBase disguiseEntity;
	private PlayerIdent disguisePlayer;
	
	public void setWorld(World w) {
		worldObj = w;
	}
	
	public void set(EntityPlayer player, EntityLivingBase entity) {
		if (entity instanceof EntityPlayer) {
			disguiseEntity = null;
			disguisePlayer = PlayerIdent.create((EntityPlayer)entity);
		} else {
			disguisePlayer = null;
			if (entity == null) {
				disguiseEntity = null;
				setSize(player, 0.6F, 1.8F);
				return;
			}
			NBTTagCompound copy = new NBTTagCompound();
			entity.writeToNBT(copy);
			copy.setBoolean("Invulnerable", true);
			disguiseEntity = (EntityLivingBase)ApiEntity.createEntityByID(ApiEntity.getEntityID(entity), player.world);
			disguiseEntity.readFromNBT(copy);
			disguiseEntity.setCustomNameTag(player.getDisplayName().getFormattedText());
			disguiseEntity.setAlwaysRenderNameTag(true);
			disguiseEntity.setEntityId(entity.getEntityId());
			if (disguiseEntity instanceof EntityEnderman) {
				((EntityEnderman) disguiseEntity).setHeldBlockState(Blocks.AIR.getDefaultState());
			}
		}
	}
	
	protected void setSize(EntityPlayer player, float width, float height) {
        if (width != player.width || height != player.height) {
            float oldWidth = player.width;
            player.width = width;
            player.height = height;
            AxisAlignedBB bb = player.getEntityBoundingBox();
            player.setEntityBoundingBox(new AxisAlignedBB(bb.minX, bb.minY, bb.minZ, bb.minX + player.width, bb.minY + player.height, bb.minZ + player.width));
            if (player.width > oldWidth && !player.world.isRemote) {
                //player.moveEntity(oldWidth - player.width, 0, oldWidth - player.width);
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
			disguiseEntity.setWorld(player.world);
			disguiseEntity.setArrowCountInEntity(player.getArrowCountInEntity());
			List<ItemStack> inv = (List<ItemStack>)player.getArmorInventoryList();
			List<ItemStack> disguiseInv = (List<ItemStack>)disguiseEntity.getArmorInventoryList();
			for (int i = 0; i < inv.size(); i++) {
				disguiseInv.set(i, inv.get(i));
			}
			
			disguiseEntity.setHeldItem(EnumHand.MAIN_HAND, player.getHeldItem(EnumHand.MAIN_HAND));
			disguiseEntity.setHeldItem(EnumHand.OFF_HAND, player.getHeldItem(EnumHand.OFF_HAND));
			if (disguiseEntity instanceof EntityLiving) {
				EntityLiving disguise = (EntityLiving) disguiseEntity;
				if (player.isEntityAlive() && disguise.world.rand.nextInt(1000) < disguise.livingSoundTime++) {
		            disguise.livingSoundTime = -disguise.getTalkInterval();
		            disguise.playLivingSound();
		        }
			}
			if (disguiseEntity instanceof EntityEnderman) {
				ItemStack stack = player.getHeldItemMainhand();
				Block b = stack.isEmpty() ? Blocks.AIR : ApiBlock.getBlockByItem(stack.getItem());
				@SuppressWarnings("deprecation")
				IBlockState state = stack.isEmpty() || b == null ? Blocks.AIR.getDefaultState() : b.getStateFromMeta(stack.getMetadata());
				((EntityEnderman) disguiseEntity).setHeldBlockState(state);
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
		if (looked == null) {
			if (!isActive()) return true;
		} else if (isPlayer()) {
			if (looked instanceof EntityPlayer) {
				return ((EntityPlayer) looked).getGameProfile().getId().equals(getPlayer().getUniqueID());
			}
		} else if (isActive()) {
			return looked.equals(disguiseEntity);
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
	
	public Disguise readFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("Entity")) {
			disguiseEntity = (EntityLivingBase)EntityList.createEntityFromNBT(compound.getCompoundTag("Entity"), worldObj);
			disguisePlayer = null;
		} else {
			disguiseEntity = null;
			disguisePlayer = PlayerIdent.create(compound);
		}
		return this;
	}
}
