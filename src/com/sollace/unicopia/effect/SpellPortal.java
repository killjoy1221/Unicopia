package com.sollace.unicopia.effect;

import java.util.List;

import com.blazeloader.api.particles.ApiParticles;
import com.blazeloader.util.shape.IShape;
import com.blazeloader.util.shape.Sphere;
import com.blazeloader.util.version.Versions;
import com.sollace.unicopia.PlayerExtension;
import com.sollace.unicopia.Unicopia.Particles;
import com.sollace.unicopia.entity.EntitySpell;
import com.sollace.unicopia.entity.IMagicals;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class SpellPortal implements IMagicEffect, IUseAction {
	
	private static final IShape portalZone_X = new Sphere(true, 1, 0, 2, 1);
	private static final IShape portalZone_Z = new Sphere(true, 1, 1, 2, 0);
	
	private int cooldown = 0;
	private SpellPortal sibling = null;
	
	private BlockPos position = null;
	private BlockPos destinationPos = null;
	
	private boolean isDead = false;
	private boolean axis = false;
	
	public int getMaxLevel() {
		return 0;
	}
	
	public boolean update(Entity source) {
		return true;
	}
	
	public void render(Entity source) {
		
	}
	
	public void setDead() {
		isDead = true;
		if (sibling != null) {
			sibling.isDead = true;
		}
	}
	
	public boolean getDead() {
		return isDead;
	}
	
	public ActionResult onUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			position = pos.offset(side);
			PlayerExtension prop = PlayerExtension.get(player);
			IMagicEffect other = prop.getEffect();
			if (other instanceof SpellPortal && other != this && !((SpellPortal)other).isDead) {
				((SpellPortal)other).notifyMatched(this);
				prop.addEffect(null);
			} else {
				prop.addEffect(this);
			}
		}
		EnumFacing playerFacing = player.getHorizontalFacing();
		axis = playerFacing.getAxis() == EnumFacing.Axis.X;
		return ActionResult.PLACE;
	}
	
	public ActionResult onUse(ItemStack stack, EntityPlayer player, World world, Entity hitEntity) {
		return ActionResult.NONE;
	}
	
	public void notifyMatched(SpellPortal other) {
		if (sibling == null) {
			sibling = other;
			other.sibling = this;
			sibling.destinationPos = position;
			destinationPos = sibling.position;
		}
	}
	
	public boolean updateAt(EntitySpell source, World w, double x, double y, double z, int level) {
		if (!w.isRemote) {
			if (cooldown > 0) cooldown--;
			if (cooldown <= 0) {
				if (destinationPos != null) {
					SpellPortal dest = getDestinationPortal(w);
					if (dest != null && !dest.isDead) {
						if (teleportNear(w, x, y, z, level)) {
							dest.cooldown = 30;
							cooldown = 30;
						}
					}
				}
			}
		}
		return false;
	}
	
	public void renderAt(EntitySpell source, World w, double x, double y, double z, int level) {
		if (Versions.isClient()) {
			ApiParticles.spawnParticleShape(Particles.unicorn.getData(), w, x, y, z, getPortalZone(), 1);
		}
	}
	
	public IShape getPortalZone() {
		if (axis) {
			return portalZone_X;
		}
		return portalZone_Z;
	}
	
	private boolean teleportNear(World w, double x, double y, double z, int level) {
		boolean result = false;
		AxisAlignedBB bb = new AxisAlignedBB(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 3, z + 0.5);
		List<Entity> entities = (List<Entity>)w.getEntitiesWithinAABB(Entity.class, bb);
		for (Entity i : entities) {
			if (!(i instanceof IMagicals) && i.timeUntilPortal == 0) {
				EnumFacing offset = i.getHorizontalFacing();
				double destX = destinationPos.getX() + (i.posX - x) + offset.getFrontOffsetX();
				double destY = destinationPos.getY() + (i.posY - y) + 0.5f;
				double destZ = destinationPos.getZ() + (i.posZ - z) + offset.getFrontOffsetZ();
				i.timeUntilPortal = i.getPortalCooldown();
				i.worldObj.playSoundEffect(i.posX, i.posY, i.posZ, "random.pop", 1.0F, 1.0F);
				i.setPositionAndUpdate(destX, destY, destZ);
				i.worldObj.playSoundEffect(i.posX, i.posY, i.posZ, "random.pop", 1.0F, 1.0F);
			}
		}
		return result;
	}
	
	public SpellPortal getDestinationPortal(World w) {
		if (sibling == null) {
			AxisAlignedBB box = AxisAlignedBB.fromBounds(destinationPos.getX(), destinationPos.getY(), destinationPos.getZ(), destinationPos.getX() + 1, destinationPos.getY() + 1, destinationPos.getZ() + 1);
			List<EntitySpell> entities = w.getEntitiesWithinAABB(EntitySpell.class, box.expand(0.5, 0.5, 0.5));
			for (EntitySpell i : entities) {
				if (i.getEffect() instanceof SpellPortal) {
					return sibling = (SpellPortal)i.getEffect();
				}
			}
		}
		return sibling;
	}
	
	public void writeToNBT(NBTTagCompound compound) {
		if (destinationPos != null) {
			NBTTagCompound dest = new NBTTagCompound();
			dest.setInteger("X", destinationPos.getX());
			dest.setInteger("Y", destinationPos.getY());
			dest.setInteger("Z", destinationPos.getZ());
			compound.setTag("destination", dest);
		}
		compound.setInteger("portal_cooldown", cooldown);
		compound.setBoolean("facing_X", axis);
	}
	
	public void readFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("destination")) {
			NBTTagCompound dest = compound.getCompoundTag("destination");
			destinationPos = new BlockPos(
					dest.getInteger("X"),
					dest.getInteger("Y"),
					dest.getInteger("Z"));
		}
		cooldown = compound.getInteger("portal_cooldown");
		axis = compound.getBoolean("facing_X");
	}
}
