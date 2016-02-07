package com.sollace.unicopia.client.render;

import com.sollace.unicopia.client.model.ModelGem;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class RenderGem extends RendererLivingEntity {
	
	private static final ResourceLocation gem = new ResourceLocation("unicopia", "textures/entity/gem.png");
	
	public RenderGem(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelGem(), 0);
	}
	
	protected ResourceLocation getEntityTexture(Entity entity) {
		return gem;
	}
	
	protected float getDeathMaxRotation(EntityLivingBase entity) {
		return 0;
    }
	
	protected boolean canRenderName(EntityLivingBase targetEntity) {
        return super.canRenderName(targetEntity) && (targetEntity.getAlwaysRenderNameTagForRender() || targetEntity.hasCustomName() && targetEntity == this.renderManager.pointedEntity);
    }
}
