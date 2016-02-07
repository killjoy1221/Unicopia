package com.sollace.unicopia.client.render;

import com.blazeloader.api.tick.ApiTick;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class ChangelingItemRenderer {
	
	private final RenderChangeling renderer;
	
	private float equippedProgress;
	private float prevEquippedProgress;
	private ItemStack itemToRender;
	
	public ChangelingItemRenderer(RenderChangeling renderer) {
		this.renderer = renderer;
	}
	
	public void renderLeftArm(AbstractClientPlayer clientPlayer) {
		updateEquippedItem(clientPlayer);
		float partialTicks = ApiTick.getPartialRenderTicks();
		float var2 = 1 - (prevEquippedProgress + (equippedProgress - prevEquippedProgress) * partialTicks);
		func_178095_a(clientPlayer, var2, clientPlayer.getSwingProgress(partialTicks));
	}
	
	private void func_178095_a(AbstractClientPlayer clientPlayer, float p_178095_2_, float p_178095_3_) {
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.0F, p_178095_2_ * -0.6F, 0.0F);
        GlStateManager.translate(0, 0, -2);
        GlStateManager.rotate(-55F, 1, 0, 0);
        GlStateManager.rotate(-200F, 0, 1, 0);
        GlStateManager.rotate(-10F, 0, 0, 1);
        GlStateManager.scale(1, 1, 1);
        GlStateManager.translate(0.2f, -1.7f, -0.4f);
        renderer._renderLeftArm(clientPlayer);
    }
	
	public void updateEquippedItem(AbstractClientPlayer clientPlayer) {
        this.prevEquippedProgress = this.equippedProgress;
        ItemStack var2 = clientPlayer.inventory.getCurrentItem();
        boolean var3 = false;

        if (this.itemToRender != null && var2 != null) {
            if (!this.itemToRender.getIsItemStackEqual(var2)) {
                var3 = true;
            }
        } else if (this.itemToRender == null && var2 == null) {
            var3 = false;
        } else {
            var3 = true;
        }

        float var4 = 0.4F;
        float var5 = var3 ? 0.0F : 1.0F;
        float var6 = MathHelper.clamp_float(var5 - equippedProgress, -var4, var4);
        equippedProgress += var6;

        if (equippedProgress < 0.1F) itemToRender = var2;
    }
}
