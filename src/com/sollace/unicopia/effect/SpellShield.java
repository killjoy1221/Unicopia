package com.sollace.unicopia.effect;

import java.util.List;

import com.blazeloader.api.entity.ApiProjectile;
import com.blazeloader.api.particles.ApiParticles;
import com.blazeloader.util.version.Versions;
import com.sollace.unicopia.Race;
import com.sollace.unicopia.Unicopia.Particles;
import com.sollace.unicopia.entity.EntitySpell;
import com.sollace.unicopia.entity.IMagicals;
import com.sollace.unicopia.power.Power;
import com.sollace.unicopia.server.PlayerSpeciesRegister;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class SpellShield extends Spell {
	
	private int strength = 0;
	
	public SpellShield() {
	}
	
	public SpellShield(int type) {
		setStrength(type);
	}
	
	public void setStrength(int level) {
		strength = level;
	}
	
	public int getMaxLevel() {
		return -1;
	}
	
	public void render(Entity source) {
		if (Versions.isClient()) {
			spawnParticles(source.worldObj, source.posX, source.posY, source.posZ, 4 + (strength * 2));
		}
	}
	
	public void renderAt(EntitySpell source, World w, double x, double y, double z, int level) {
		if (Versions.isClient()) {
			if (w.rand.nextInt(4 + level * 4) == 0) {
				spawnParticles(w, x, y, z, 4 + (level * 2));
			}
		}
	}
	
	protected void spawnParticles(World w, double x, double y, double z, int strength) {
		ApiParticles.particleBubble(Particles.unicorn.getData(), w, x, y, z, strength, 1);
	}
	
	public boolean update(Entity source) {
		applyEntities(null, source, source.worldObj, source.posX, source.posY, source.posZ, strength);
		if (source.worldObj.getWorldTime() % 50 == 0) {
			double radius = 4 + (strength * 2);
			if (!Power.TakeFromPlayer((EntityPlayer)source, radius/4)) {
				setDead();
			}
		}
		return !isDead;
	}
	
	public boolean updateAt(EntitySpell source, World w, double x, double y, double z, int level) {
		return applyEntities(source, source.getOwner(), w, x, y, z, level);
    }
	
	public boolean applyEntities(EntitySpell source, Entity owner, World w, double x, double y, double z, int level) {
		double radius = 4 + (level * 2);
		AxisAlignedBB bb = new AxisAlignedBB(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius);
		List<Entity> entities = (List<Entity>)w.getEntitiesWithinAABB(Entity.class, bb);
		for (Entity i : entities) {
			if ((!i.equals(owner) || (owner instanceof EntityPlayer && !PlayerSpeciesRegister.getPlayerSpecies((EntityPlayer)owner).canCast())) && !(i instanceof IMagicals)) {
				double dist = i.getDistance(x, y, z);
				double dist2 = i.getDistance(x, y - i.getEyeHeight(), z);
				boolean projectile = ApiProjectile.isProjectile(i);
				if (dist <= radius || dist2 <= radius) {
					if (projectile) {
						if (!ApiProjectile.isProjectileThrownBy(i, owner)) {
							if (dist < radius/2) {
								i.playSound("mob.zombie.remedy", 0.1f, 1);
								i.setDead();
							} else {
								ricochet(i, x, y, z);
							}
						}
					} else if (i instanceof EntityLivingBase) {
						double force = dist;
						if (i instanceof EntityPlayer) {
							force = calculateForce((EntityPlayer)i);
						}
						i.addVelocity(-(x - i.posX)/force,(-(y - i.posY)/force + (dist < 1 ? dist : 0)),-(z - i.posZ)/force);
					}
				}
			}
		}
		return true;
	}
	
	protected double calculateForce(EntityPlayer player) {
		Race race = PlayerSpeciesRegister.getPlayerSpecies(player);
		double force = 4 * 8;
		if (race.canUseEarth()) {
			if (player.isSneaking()) {
				force *= 16;
			}
		} else if (race.canFly()) {
			force /= 2;
		}
		return force;
	}
	
	private void ricochet(Entity projectile, double x, double y, double z) {
		Vec3 position = new Vec3(projectile.posX, projectile.posY, projectile.posZ);
		Vec3 motion = new Vec3(projectile.motionX, projectile.motionY, projectile.motionZ);
		
		Vec3 normal = position.subtract(x, y, z).normalize();
		Vec3 approach = motion.subtract(normal);
		
		if (approach.lengthVector() >= motion.lengthVector()) {
			ApiProjectile.setThrowableHeading(projectile, normal.xCoord, normal.yCoord, normal.zCoord, (float)motion.lengthVector(), 0);
		}
	}
	
	public void writeToNBT(NBTTagCompound compound) {
		compound.setInteger("spell_strength", strength);
	}
	
	public void readFromNBT(NBTTagCompound compound) {
		strength = compound.getInteger("spell_strength");
	}
}
