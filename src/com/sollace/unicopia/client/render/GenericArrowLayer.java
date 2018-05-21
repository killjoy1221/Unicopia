package com.sollace.unicopia.client.render;

import java.util.Random;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.util.math.MathHelper;

public class GenericArrowLayer<T extends EntityLivingBase> implements LayerRenderer<T> {
    private RenderLivingBase<T> renderer;
    
    public GenericArrowLayer() {
        
    }
    
    public void setRenderer(RenderLivingBase<T> r) {
    	renderer = r;
    }

    public void doRenderLayer(T entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
        int arrows = entitylivingbaseIn.getArrowCountInEntity();

        if (arrows > 0) {
            EntityArrow arrow = new EntityTippedArrow(entitylivingbaseIn.world, entitylivingbaseIn.posX, entitylivingbaseIn.posY, entitylivingbaseIn.posZ);
            Random var11 = new Random((long)entitylivingbaseIn.getEntityId());
            RenderHelper.disableStandardItemLighting();

            for (int i = 0; i < arrows; ++i) {
                GlStateManager.pushMatrix();
                ModelRenderer var13 = renderer.getMainModel().getRandomModelBox(var11);
                ModelBox var14 = (ModelBox)var13.cubeList.get(var11.nextInt(var13.cubeList.size()));
                var13.postRender(0.0625F);
                float var15 = var11.nextFloat();
                float var16 = var11.nextFloat();
                float var17 = var11.nextFloat();
                float var18 = (var14.posX1 + (var14.posX2 - var14.posX1) * var15) / 16.0F;
                float var19 = (var14.posY1 + (var14.posY2 - var14.posY1) * var16) / 16.0F;
                float var20 = (var14.posZ1 + (var14.posZ2 - var14.posZ1) * var17) / 16.0F;
                GlStateManager.translate(var18, var19, var20);
                var15 = var15 * 2.0F - 1.0F;
                var16 = var16 * 2.0F - 1.0F;
                var17 = var17 * 2.0F - 1.0F;
                var15 *= -1.0F;
                var16 *= -1.0F;
                var17 *= -1.0F;
                float var21 = MathHelper.sqrt(var15 * var15 + var17 * var17);
                arrow.prevRotationYaw = arrow.rotationYaw = (float)(Math.atan2((double)var15, (double)var17) * 180.0D / Math.PI);
                arrow.prevRotationPitch = arrow.rotationPitch = (float)(Math.atan2((double)var16, (double)var21) * 180.0D / Math.PI);
                
                renderer.getRenderManager().doRenderEntity(arrow, 0, 0, 0, 0, partialTicks, false);
                GlStateManager.popMatrix();
            }

            RenderHelper.enableStandardItemLighting();
        }
    }

    public boolean shouldCombineTextures() {
        return false;
    }
}
