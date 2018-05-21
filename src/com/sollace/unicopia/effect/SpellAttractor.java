package com.sollace.unicopia.effect;

import java.util.List;

import com.blazeloader.api.particles.ApiParticles;
import com.blazeloader.api.particles.ParticleData;
import com.blazeloader.util.shape.Cone;
import com.blazeloader.util.shape.IShape;
import com.blazeloader.util.version.Versions;
import com.sollace.unicopia.Unicopia.Particles;
import com.sollace.unicopia.entity.EntitySpell;
import com.sollace.unicopia.entity.IMagicals;
import com.sollace.unicopia.server.PlayerSpeciesRegister;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SpellAttractor extends SpellShield {
	
	public SpellAttractor() {
	}
	
	public SpellAttractor(int type) {
		super(type);
	}
	
	public void renderAt(EntitySpell source, World w, double x, double y, double z, int level) {
		if (Versions.isClient()) {
			spawnParticles(w, x, y, z, 4 + (level * 2));
		}
	}
	
	protected void spawnParticles(World w, double x, double y, double z, int strength) {
		IShape cone = new Cone(true, 2, 2);
		if (w.rand.nextBoolean()) {
			cone.setRotation(0, (float)Math.PI/2);
		} else {
			cone.setRotation(0, -(float)Math.PI/2);
		}
		
		ParticleData data = Particles.unicorn.getData();
		for (int i = 0; i < 10; i++) {
			Vec3d pos = cone.computePoint(w.rand);
			data.setVel(pos.x / 3, pos.y / 3, pos.z / 3);
			data.setPos(pos.addVector(x, y, z));
			ApiParticles.spawnParticle(data, w);
		}
	}
	
	public boolean applyEntities(EntitySpell source, Entity owner, World w, double x, double y, double z, int level) {
		double radius = 4 + (level * 2);
		AxisAlignedBB bb = new AxisAlignedBB(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius);
		List<Entity> entities = (List<Entity>)w.getEntitiesWithinAABB(Entity.class, bb);
		for (Entity i : entities) {
			if ((!i.equals(source.getOwner()) || (source.getOwner() instanceof EntityPlayer && !PlayerSpeciesRegister.getPlayerSpecies((EntityPlayer)source.getOwner()).canCast())) && !(i instanceof IMagicals)) {
				double dist = i.getDistance(x, y, z);
				double dist2 = i.getDistance(x, y - i.getEyeHeight(), z);
				if (dist <= radius || dist2 <= radius) {
					double force = dist;
					if (i instanceof EntityPlayer) {
						force = calculateForce((EntityPlayer)i);
					}
					i.addVelocity((x - i.posX)/force,((y - i.posY)/force + (dist < 1 ? dist : 0)),(z - i.posZ)/force);
					float maxVel = 2f;
					
					if (i.motionX > maxVel) i.motionX = maxVel;
					if (i.motionX < -maxVel) i.motionX = -maxVel;
					if (i.motionY > maxVel) i.motionY = maxVel;
					if (i.motionY < -maxVel) i.motionY = -maxVel;
					if (i.motionZ > maxVel) i.motionZ = maxVel;
					if (i.motionZ < -maxVel) i.motionZ = -maxVel;
				}
				if (i instanceof EntityItem) {
					((EntityItem)i).setAgeToCreativeDespawnTime();
				}
			}
		}
		return true;
	}
}
