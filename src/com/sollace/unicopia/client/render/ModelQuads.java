package com.sollace.unicopia.client.render;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.BufferBuilder;

public class ModelQuads extends ModelBox {
	
	public ModelQuads(ModelRenderer renderer) {
		super(renderer, 0, 0, 0, 0, 0, 0, 0, 0, 0);
	}

	protected List<TexturedQuad> quadList = new ArrayList<TexturedQuad>();
	
	public void addFace(PositionTextureVertex[] vertices, int texcoordU1, int texcoordV1, int texcoordU2, int texcoordV2, float textureWidth, float textureHeight) {
		quadList.add(new TexturedShape2d(vertices));
	}
	
	public void render(BufferBuilder renderer, float scale) {
		for (TexturedQuad i : quadList) {
			i.draw(renderer, scale);
        }
	}
}
