package com.sollace.unicopia.client;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.blazeloader.api.client.ApiClient;
import com.mumfrey.liteloader.util.ModUtilities;
import com.sollace.unicopia.client.particle.EntityMagicFX;
import com.sollace.unicopia.client.particle.EntityRaindropFX;
import com.sollace.unicopia.client.render.RenderCloud;
import com.sollace.unicopia.client.render.RenderGem;
import com.sollace.unicopia.client.render.RenderSpellbook;
import com.sollace.unicopia.entity.EntityCloud;
import com.sollace.unicopia.entity.EntitySpell;
import com.sollace.unicopia.entity.EntitySpellbook;

public class ClientSide {
    public static void spawnParticle(String particleType, World world, double x, double y, double z, double velX, double velY, double velZ) {
    	EntityFX fx = null;
    	if ("unicorn".contentEquals(particleType)) {
    		fx = new EntityMagicFX(world, x, y, z, velX, velY, velZ);
    	} else if ("rain".contentEquals(particleType)) {
    		fx = new EntityRaindropFX(world, x, y, z);
    	}
    	
    	if (fx != null) {
			addParticleToRenderer(fx);
    	}
    }
    
    protected static void spawnDigginFX(World w, double x, double y, double z, double vX, double vY, double vZ, IBlockState blockState, float multScale, float multVel) {
    	((WorldServer)w).spawnParticle(EnumParticleTypes.BLOCK_CRACK, false, x, y, z, 1, 0, 0, 0, Math.sqrt(vX * vX + vY * vY + vZ * vZ) * multVel, Block.getStateId(blockState));
    }
    
    
    public static void particleBubble(String particleType, World world, double x, double y, double z, double radius, double velX, double velY, double velZ) {
    	particleBubble(particleType, world, x, y, z, radius, velX, velY, velZ, 64);
    }
    
    public static void particleBubble(String particleType, World world, double x, double y, double z, double radius, double velX, double velY, double velZ, int total) {
    	total *= (4 * Math.PI * radius * radius);
    	for (int i = 0; i < total; i++) {
    		double X = MathHelper.getRandomDoubleInRange(world.rand, x - radius, x + radius);
    		double Z = MathHelper.getRandomDoubleInRange(world.rand, z - radius, z + radius);
    		double Y = Math.sqrt((radius*radius) - ((x - X)*(x - X) + (z - Z)*(z - Z)));
    		Y = Math.random() > 0.5 ? y - Y : y + Y;
    		spawnParticle(particleType, world, X, Y, Z, velX, velY, velZ);
    	}
    }
    
    private static void addParticleToRenderer(EntityFX fx) {
    	if (fx != null) {
    		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
    	}
    }
    
    public static void RegisterRenderers() {
    	ModUtilities.addRenderer(EntityCloud.class, new RenderCloud(ApiClient.getRenderManager()));
		//MinecraftForgeClient.registerItemRenderer(Unicopia.Items.cloud, new CloudItemRenderer());
    	ModUtilities.addRenderer(EntitySpell.class, new RenderGem(ApiClient.getRenderManager()));
    	ModUtilities.addRenderer(EntitySpellbook.class, new RenderSpellbook(ApiClient.getRenderManager()));
    }
    
    public static EntityPlayer thePlayer() {
    	return Minecraft.getMinecraft().thePlayer;
    }
    
    public static MovingObjectPosition getMousOver() {
    	return Minecraft.getMinecraft().objectMouseOver;
    }
    
    public static void sendBlockPlacement(EntityPlayer player, double fX, double fY, double fZ, double x, double y, double z, EnumFacing side) {
    	Minecraft.getMinecraft().playerController.func_178892_a(player.worldObj, null).sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(x, y, z), side.getIndex(), player.inventory.getCurrentItem(), (float)(x - fX), (float)(y - fY), (float)(z - fZ)));
    }
    
    public static float getReach() {
    	return Minecraft.getMinecraft().playerController.getBlockReachDistance();
    }
}
