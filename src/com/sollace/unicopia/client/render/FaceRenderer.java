package com.sollace.unicopia.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;

public class FaceRenderer extends ModelRenderer {

	public FaceRenderer(ModelBase model) {
		super(model);
	}
	
	public void addQuad(ModelBox quad) {
		cubeList.add(quad);
	}
}
