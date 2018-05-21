package com.sollace.unicopia.client.render;

import com.blazeloader.api.client.ApiClient;
import com.blazeloader.api.client.render.ILivingRenderer;
import com.sollace.unicopia.PlayerExtension;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.EnumHand;

public class RenderChangeling extends RenderPlayer {
	
	private static final GenericArrowLayer<EntityLivingBase> arrows = new GenericArrowLayer<EntityLivingBase>();
	
	private final ChangelingItemRenderer itemRenderer = new ChangelingItemRenderer(this);
	
	public RenderChangeling(RenderManager renderManager) {
		super(renderManager);
	}
	
	public void doRender(AbstractClientPlayer clientPlayer, double x, double y, double z, float entityYaw, float partialTicks) {
		EntityLivingBase renderedEntity = PlayerExtension.get(clientPlayer).getDisguise().getEntity();
		if (renderedEntity != null) {
			copyAnglesTo(renderedEntity, clientPlayer);
			ILivingRenderer<EntityLivingBase> r = getEntityRender(renderedEntity);
			r.addLayer(arrows);
			arrows.setRenderer(r.unwrap());
			r.unwrap().doRender(renderedEntity, x, y, z, entityYaw, partialTicks);
			r.removeLayer(arrows);
		}
	}
	
	@SuppressWarnings("unchecked")
	protected ILivingRenderer<EntityLivingBase> getEntityRender(Entity e) {
		return (ILivingRenderer<EntityLivingBase>)ApiClient.getRenderManager().getEntityRenderObject(e);
	}
	
	public void renderRightArm(AbstractClientPlayer clientPlayer) {
		EntityLivingBase renderedEntity = PlayerExtension.get(clientPlayer).getDisguise().getEntity();
		if (renderedEntity != null) {
			ILivingRenderer<EntityLivingBase> r = getEntityRender(renderedEntity);
			ModelBase m = r.getModel();
			if (m instanceof ModelBiped) {
				preRenderArm(r, (ModelBiped)m, renderedEntity);
				renderArm((ModelBiped)m, getMainModel().bipedRightArm, ((ModelBiped) m).bipedRightArm);
			}
		}
    }
	
	public void renderLeftArm(AbstractClientPlayer clientPlayer) {
		EntityLivingBase renderedEntity = PlayerExtension.get(clientPlayer).getDisguise().getEntity();
		if (renderedEntity != null) {
			ILivingRenderer<EntityLivingBase> r = getEntityRender(renderedEntity);
			ModelBase m = r.getModel();
			if (m instanceof ModelBiped) {
				preRenderArm(r, (ModelBiped)m, renderedEntity);
				renderArm((ModelBiped)m, getMainModel().bipedLeftArm, ((ModelBiped) m).bipedLeftArm);
			}
		}
		if (renderedEntity instanceof EntityZombie || renderedEntity instanceof EntitySkeleton) {
			itemRenderer.renderLeftArm(clientPlayer);
		}
	}
	
    private void preRenderArm(ILivingRenderer<EntityLivingBase> r, ModelBiped m, EntityLivingBase renderedEntity) {
    	GlStateManager.color(1, 1, 1);
		this.bindTexture(r.getTexture(renderedEntity));
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
		arm.showModel = true;
		arm.render(0.0625F);
		arm.rotationPointX = rX;
		arm.rotationPointY = rY;
		arm.rotationPointZ = rZ;
	}
    
	public static void copyAnglesTo(EntityLivingBase dest, EntityLivingBase source) {
		dest.startRiding(source.getRidingEntity());
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
		for (EnumHand i : EnumHand.values()) {
			dest.setHeldItem(i, source.getHeldItem(i));
		}
	}
}
