package com.sollace.unicopia.client.render;

import com.blazeloader.api.client.ApiClient;
import com.sollace.unicopia.PlayerExtension;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;

public class RenderChangeling extends RenderPlayer {
	
	private static final GenericArrowLayer arrows = new GenericArrowLayer();
	
	private final ChangelingItemRenderer itemRenderer = new ChangelingItemRenderer(this);
	
	public RenderChangeling(RenderManager renderManager) {
		super(renderManager);
	}
	
	public void doRender(AbstractClientPlayer clientPlayer, double x, double y, double z, float entityYaw, float partialTicks) {
		EntityLivingBase renderedEntity = PlayerExtension.get(clientPlayer).getDisguise().getEntity();
		if (renderedEntity != null) {
			copyAnglesTo(renderedEntity, clientPlayer);
			RendererLivingEntity r = (RendererLivingEntity)ApiClient.getRenderManager().getEntityRenderObject(renderedEntity);
			r.layerRenderers.add(arrows);
			arrows.setRenderer(r);
			r.doRender(renderedEntity, x, y, z, entityYaw, partialTicks);
			r.layerRenderers.remove(arrows);
		}
	}
	
	public void renderRightArm(AbstractClientPlayer clientPlayer) {
		EntityLivingBase renderedEntity = PlayerExtension.get(clientPlayer).getDisguise().getEntity();
		if (renderedEntity != null) {
			RendererLivingEntity r = ((RendererLivingEntity)ApiClient.getRenderManager().getEntityRenderObject(renderedEntity));
			ModelBase m = r.getMainModel();
			if (m instanceof ModelBiped) {
				preRenderArm(r, (ModelBiped)m, renderedEntity);
				renderArm((ModelBiped)m, getPlayerModel().bipedRightArm, ((ModelBiped) m).bipedRightArm);
			}
		}
		if (renderedEntity instanceof EntityZombie || renderedEntity instanceof EntitySkeleton) {
			itemRenderer.renderLeftArm(clientPlayer);
		}
    }
	
	public void renderLeftArm(AbstractClientPlayer clientPlayer) {
		
	}
		
    public void _renderLeftArm(AbstractClientPlayer clientPlayer) {
		EntityLivingBase renderedEntity = PlayerExtension.get(clientPlayer).getDisguise().getEntity();
		if (renderedEntity != null) {
			RendererLivingEntity r = ((RendererLivingEntity)ApiClient.getRenderManager().getEntityRenderObject(renderedEntity));
			ModelBase m = r.getMainModel();
			if (m instanceof ModelBiped) {
				preRenderArm(r, (ModelBiped)m, renderedEntity);
				renderArm((ModelBiped)m, getPlayerModel().bipedLeftArm, ((ModelBiped) m).bipedLeftArm);
			}
		}
    }
    
    private void preRenderArm(RendererLivingEntity r, ModelBiped m, EntityLivingBase renderedEntity) {
    	GlStateManager.color(1, 1, 1);
		r.bindEntityTexture(renderedEntity);
		m.swingProgress = 0;
		if (m instanceof ModelBiped) {
			ModelBiped biped = (ModelBiped)m;
			biped.isSneak = false;
		}
		m.setRotationAngles(0, 0, 0, 0, 0, 0.0625f, renderedEntity);
    }
    
    private void renderArm(ModelBiped m, ModelRenderer playerArm, ModelRenderer arm) {
		float rX = arm.rotationPointX,
		rY = arm.rotationPointY,
		rZ = arm.rotationPointZ;
		ModelBase.copyModelAngles(playerArm, arm);
		if (m instanceof ModelPlayer) {
			((ModelPlayer)m).renderRightArm();
		} else {
			arm.showModel = true;
			arm.render(0.0625F);
		}
		arm.rotationPointX = rX;
		arm.rotationPointY = rY;
		arm.rotationPointZ = rZ;
	}
    
	public static void copyAnglesTo(EntityLivingBase dest, EntityLivingBase source) {
		dest.ridingEntity = source.ridingEntity;
		dest.isSwingInProgress = source.isSwingInProgress;
		dest.swingProgress = source.swingProgress;
		dest.prevSwingProgress = source.prevSwingProgress;
		dest.swingProgressInt = source.swingProgressInt;
		dest.limbSwing = source.limbSwing;
		dest.limbSwingAmount = source.limbSwingAmount;
		dest.prevLimbSwingAmount = source.prevLimbSwingAmount;
		dest.distanceWalkedModified = source.distanceWalkedModified;
		dest.distanceWalkedOnStepModified = source.distanceWalkedOnStepModified;
		dest.prevDistanceWalkedModified = source.prevDistanceWalkedModified;
		dest.rotationPitch = source.rotationPitch;
		dest.rotationYaw = source.rotationYaw;
		dest.prevRotationPitch = source.prevRotationPitch;
		dest.prevRotationYaw = source.prevRotationYaw;
		dest.rotationYawHead = source.rotationYawHead;
		dest.prevRotationYawHead = source.prevRotationYawHead;
		dest.renderYawOffset = source.renderYawOffset;
		dest.prevRenderYawOffset = source.prevRenderYawOffset;
		dest.hurtTime = source.hurtTime;
		dest.setPosition(source.posX, source.posY, source.posZ);
	}
}
