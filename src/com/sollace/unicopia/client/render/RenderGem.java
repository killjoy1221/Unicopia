package com.sollace.unicopia.client.render;

import com.sollace.unicopia.client.model.ModelGem;
import com.sollace.unicopia.entity.EntitySpell;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderGem extends RenderLiving<EntitySpell> {
	
	private static final ResourceLocation gem = new ResourceLocation("unicopia", "textures/entity/gem.png");
	
	public RenderGem(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelGem(), 0);
	}
	
	protected ResourceLocation getEntityTexture(EntitySpell entity) {
		return gem;
	}
	
	protected float getDeathMaxRotation(EntitySpell entity) {
		return 0;
    }
	
	protected boolean canRenderName(EntitySpell targetEntity) {
        return super.canRenderName(targetEntity) && (targetEntity.getAlwaysRenderNameTagForRender() || targetEntity.hasCustomName() && targetEntity == this.renderManager.pointedEntity);
    }
}
