package com.sollace.unicopia.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
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
    	return Minecraft.getMinecraft().player;
    }
    
    public static RayTraceResult getMousOver() {
    	return Minecraft.getMinecraft().objectMouseOver;
    }
    
    public static float getReach() {
    	return Minecraft.getMinecraft().playerController.getBlockReachDistance();
    }
}
