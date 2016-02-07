package com.sollace.unicopia.client.render;

import com.sollace.unicopia.client.model.ModelSpellbook;
import com.sollace.unicopia.entity.EntitySpellbook;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderSpellbook extends RendererLivingEntity {
	
	private static final ResourceLocation texture = new ResourceLocation("textures/entity/enchanting_table_book.png");
	
	public RenderSpellbook(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelSpellbook(), 0);
	}
	
	protected ResourceLocation getEntityTexture(Entity entity) {
		return texture;
	}
	
	protected float getDeathMaxRotation(EntityLivingBase entity) {
        return 0;
    }
	
	protected void renderModel(EntityLivingBase entity, float time, float walkSpeed, float stutter, float yaw, float pitch, float increment) {
		
		float breath = MathHelper.sin(((float)entity.getAge() + stutter) / 20.0F) * 0.01F + 0.1F;
		
		float first_page_rot = walkSpeed + (breath * 10);
		float second_page_rot = 1 - first_page_rot;
		float open_angle = 0.9f - walkSpeed;
		
		if (first_page_rot > 1) first_page_rot = 1;
		if (second_page_rot > 1) second_page_rot = 1;
		
		if (!((EntitySpellbook)entity).getIsOpen()) {
			GlStateManager.translate(0, 1.44f, 0);
		} else {
			GlStateManager.translate(0, 1.2f + breath, 0);
		}
		GlStateManager.pushMatrix();
		
		if (!((EntitySpellbook)entity).getIsOpen()) {
			first_page_rot = second_page_rot = open_angle = 0;
			GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.translate(-0.25f, 0, 0);
		} else {
			GlStateManager.rotate(-60.0F, 0.0F, 0.0F, 1.0F);
		}
		
		GlStateManager.enableCull();
		super.renderModel(entity, 0, first_page_rot, second_page_rot, open_angle, 0.0F, 0.0625F);
		
		GlStateManager.popMatrix();
	}
	
	protected void rotateCorpse(EntityLivingBase entity, float p_77043_2_, float p_77043_3_, float partialTicks) {
		GlStateManager.rotate(-interpolateRotation(entity.prevRotationYaw, entity.rotationYaw, partialTicks), 0.0F, 1.0F, 0.0F);
	}
	
	protected boolean canRenderName(EntityLivingBase targetEntity) {
        return super.canRenderName(targetEntity) && (targetEntity.getAlwaysRenderNameTagForRender() || targetEntity.hasCustomName() && targetEntity == renderManager.pointedEntity);
    }
}