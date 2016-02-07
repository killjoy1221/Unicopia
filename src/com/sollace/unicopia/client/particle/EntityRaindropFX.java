package com.sollace.unicopia.client.particle;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityRainFX;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.world.World;

public class EntityRaindropFX extends EntityRainFX {
	
	public EntityRaindropFX(World w, double x, double y, double z) {
		super(w, x, y, z);
		motionY = -0.1;
		particleMaxAge += 19;
    }
	
	public static class Factory implements IParticleFactory {
		@Override
		public EntityFX getEntityFX(int id, World w, double x, double y, double z, double vX, double vY, double vZ, int... args) {
			return new EntityRaindropFX(w, x, y, z);
		}
		
	}
}
