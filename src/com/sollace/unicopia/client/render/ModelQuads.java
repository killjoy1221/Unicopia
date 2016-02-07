package com.sollace.unicopia.client.render;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.Vec3;

public class ModelQuads {
	
	protected List<TexturedQuad> quadList = new ArrayList<TexturedQuad>();
	
	public void addFace(PositionTextureVertex[] vertices, int texcoordU1, int texcoordV1, int texcoordU2, int texcoordV2, float textureWidth, float textureHeight) {
		quadList.add(new TexturedQuad(vertices));
	}
	
	public void render(WorldRenderer renderer, float scale) {
		for (TexturedQuad i : quadList) {
			drawQuad(renderer, i, scale);
        }
	}
	
	private void drawQuad(WorldRenderer renderer, TexturedQuad quad, float scale) {
		Vec3 var3 = quad.vertexPositions[1].vector3D.subtractReverse(quad.vertexPositions[0].vector3D);
        Vec3 var4 = quad.vertexPositions[1].vector3D.subtractReverse(quad.vertexPositions[2].vector3D);
        Vec3 var5 = var4.crossProduct(var3).normalize();
        renderer.startDrawingQuads();

        renderer.setNormal((float)var5.xCoord, (float)var5.yCoord, (float)var5.zCoord);

        for (int var6 = 0; var6 < quad.vertexPositions.length; ++var6)
        {
            PositionTextureVertex var7 = quad.vertexPositions[var6];
            renderer.addVertexWithUV(var7.vector3D.xCoord * (double)scale, var7.vector3D.yCoord * (double)scale, var7.vector3D.zCoord * (double)scale, (double)var7.texturePositionX, (double)var7.texturePositionY);
        }

        Tessellator.getInstance().draw();
	}
}
