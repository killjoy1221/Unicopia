package com.sollace.unicopia.client.particle;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class EntityMagicFX extends EntityFX {
    private float portalParticleScale;
    private double portalPosX;
    private double portalPosY;
    private double portalPosZ;
	
	public EntityMagicFX(World w, double x, double y, double z, double vX, double vY, double vZ) {
		super(w, x, y, z, vX, vY, vZ);
        motionX = vX;
        motionY = vY;
        motionZ = vZ;
        portalPosX = posX = x;
        portalPosY = posY = y;
        portalPosZ = posZ = z;
        portalParticleScale = particleScale = rand.nextFloat() * 0.2F + 0.5F;
        particleMaxAge = (int)(Math.random() * 10) + 40;
        noClip = true;
        setParticleTextureIndex((int)(Math.random() * 8));
		
        particleRed = particleGreen = particleBlue = 1;
        particleGreen *= 0.3F;
        
        if (rand.nextBoolean()) particleBlue *= 0.4F;
        if (rand.nextBoolean()) particleRed *= 0.9F;
        if (rand.nextBoolean()) particleGreen += 0.5F;
        
        if (rand.nextBoolean()) {
        	particleGreen *= 2F;
        } else if (rand.nextBoolean()) {
        	particleRed *= 3.9F;
        }
    }
	
	public void renderParticle(WorldRenderer renderer, Entity e, float p_70539_2_, float p_70539_3_, float p_70539_4_, float p_70539_5_, float p_70539_6_, float p_70539_7_) {
        float f6 = 1 - ((particleAge + p_70539_2_) / particleMaxAge);
        f6 = 1 - f6 * f6;
        particleScale = portalParticleScale * f6;
        super.renderParticle(renderer, e, p_70539_2_, p_70539_3_, p_70539_4_, p_70539_5_, p_70539_6_, p_70539_7_);
    }

    public int getBrightnessForRender(float p_70070_1_) {
        int i = super.getBrightnessForRender(p_70070_1_);
        float f1 = (float)particleAge / (float)particleMaxAge;
        f1 *= f1;
        f1 *= f1;
        int j = i & 255;
        int k = i >> 16 & 255;
        k += f1 * 15 * 16;
        if (k > 240) k = 240;
        return j | k << 16;
    }
    
    public float getBrightness(float p_70013_1_) {
        float f2 = (float)particleAge / (float)particleMaxAge;
        f2 = f2 * f2 * f2 * f2;
        return super.getBrightness(p_70013_1_) * (1.0F - f2) + f2;
    }
	
    public void onUpdate() {
    	prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        float var1 = (float)particleAge / (float)particleMaxAge;
        var1 = 1 + var1 - var1 * var1 * 2;
        posX = portalPosX + motionX * var1;
        posY = portalPosY + motionY;
        posZ = portalPosZ + motionZ * var1;
        if (particleAge++ >= particleMaxAge) setDead();
    }
    
    public static class Factory implements IParticleFactory {
		@Override
		public EntityFX getEntityFX(int id, World w, double x, double y, double z, double vX, double vY, double vZ, int... args) {
			return new EntityMagicFX(w, x, y, z, vX, vY, vZ);
		}
    }
}
