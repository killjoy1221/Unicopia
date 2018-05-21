package com.sollace.unicopia.client.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRain;
import net.minecraft.world.World;

public class EntityRaindropFX extends ParticleRain {
	
	public EntityRaindropFX(World w, double x, double y, double z) {
		super(w, x, y, z);
		motionY = -0.1;
		particleMaxAge += 19;
    }
	
	public static class Factory implements IParticleFactory {
		@Override
		public Particle createParticle(int id, World w, double x, double y, double z, double vX, double vY, double vZ, int... args) {
			return new EntityRaindropFX(w, x, y, z);
		}
		
	}
}
