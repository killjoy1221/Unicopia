package com.sollace.unicopia.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;

import com.blazeloader.api.client.ApiClient;
import com.blazeloader.api.client.particles.ApiParticlesClient;
import com.blazeloader.api.client.render.ApiRenderItem;
import com.blazeloader.api.client.render.ApiRenderPlayer;
import com.mumfrey.liteloader.util.ModUtilities;
import com.sollace.unicopia.PlayerExtension;
import com.sollace.unicopia.Unicopia.Particles;
import com.sollace.unicopia.client.particle.EntityMagicFX;
import com.sollace.unicopia.client.particle.EntityRaindropFX;
import com.sollace.unicopia.client.render.RenderChangeling;
import com.sollace.unicopia.client.render.RenderCloud;
import com.sollace.unicopia.client.render.RenderGem;
import com.sollace.unicopia.client.render.RenderSpellbook;
import com.sollace.unicopia.entity.EntityCloud;
import com.sollace.unicopia.entity.EntitySpell;
import com.sollace.unicopia.entity.EntitySpellbook;

public class ClientSide {
    public static void registerRenderers() {
    	ApiParticlesClient.registerParticleFactory(Particles.unicorn, new EntityMagicFX.Factory());
		ApiParticlesClient.registerParticleFactory(Particles.rain, new EntityRaindropFX.Factory());
		
		PlayerExtension.skinType = ApiRenderPlayer.setPlayerRenderer("disguised", new RenderChangeling(ApiClient.getRenderManager()));
		ApiRenderItem.registerBuiltInTextures(new ResourceLocation("unicopia:items/empty_slot_gem"));
    }
    
    public static void RegisterEntityRenderers() {
    	ModUtilities.addRenderer(EntityCloud.class, new RenderCloud(ApiClient.getRenderManager()));
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
