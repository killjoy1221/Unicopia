package com.sollace.unicopia.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

public class ModelCloud extends ModelBase {
	ModelRenderer body;
	
	public ModelCloud() {
		init();
	}
	
	private void init() {
		body = new ModelRenderer(this, 0, 0);
		body.addBox(3, -3, -27, 28, 10, 54);
		body.addBox(-29, -5, -26, 32, 13, 32);
		body.addBox(-28, -5, 6, 31, 13, 22);
		body.addBox(2, -5, -21, 19, 13, 32);
		body.rotationPointY += 4.2;
	}
	
    public void render(Entity entity, float par2, float par3, float par4, float par5, float par6, float par7) {
    	GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 0.6F, 0.0F);
        body.render(par7);
        GL11.glPopMatrix();
    }
}
