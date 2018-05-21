package com.sollace.unicopia.client.model;

import com.blazeloader.api.client.MCColor;
import com.sollace.unicopia.client.render.FaceRenderer;
import com.sollace.unicopia.client.render.ModelQuads;
import com.sollace.unicopia.effect.SpellList;
import com.sollace.unicopia.entity.EntitySpell;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelGem extends ModelBase {
	
	private FaceRenderer body;
	
	public ModelGem() {
		init();
	}
	
	private void init() {
		body = new FaceRenderer(this);
		
		body.offsetY = 1.2f;
		
		ModelQuads quad = new ModelQuads(body);
		int s = 1;
		
		textureWidth = textureHeight = 256;
		
		quad.addFace(new PositionTextureVertex[] {
				new PositionTextureVertex(s, 0, s, 0, 0.5f),
				new PositionTextureVertex(-s, 0, s, 0.25f, 0.25f),
				new PositionTextureVertex(0, s * 2, 0, 0, 0.25f),
				new PositionTextureVertex(0, s * 2, 0, 0, 0.25f)
		}, 0, 0, 0, 0, 0, 0);
		quad.addFace(new PositionTextureVertex[] {
				new PositionTextureVertex(s, 0, s, 0, 0.25f),
				new PositionTextureVertex(-s, 0, s, 0.25f, 0),
				new PositionTextureVertex(0, -s * 2, 0, 0.25f, 0.25f),
				new PositionTextureVertex(0, -s * 2, 0, 0.25f, 0.25f)
		}, 0, 0, 0, 0, 0, 0);
		
		quad.addFace(new PositionTextureVertex[] {
				new PositionTextureVertex(s, 0, -s, 0.25f, 0.5f),
				new PositionTextureVertex(s, 0, s, 0.5f, 0.25f),
				new PositionTextureVertex(0, s * 2, 0, 0.25f, 0.25f),
				new PositionTextureVertex(0, s * 2, 0, 0.25f, 0.25f)
		}, 4, 0, 8, 4, textureWidth, textureHeight);
		quad.addFace(new PositionTextureVertex[] {
				new PositionTextureVertex(s, 0, -s, 0.25f, 0.25f),
				new PositionTextureVertex(s, 0, s, 0.5f, 0),
				new PositionTextureVertex(0, -s * 2, 0, 0.5f, 0.25f),
				new PositionTextureVertex(0, -s * 2, 0, 0.5f, 0.25f)
		}, 4, 4, 8, 8, textureWidth, textureHeight);
		
		quad.addFace(new PositionTextureVertex[] {
				new PositionTextureVertex(-s, 0, -s, 0.5f, 0.5f),
				new PositionTextureVertex(s, 0, -s, 0.75f, 0.25f),
				new PositionTextureVertex(0, s * 2, 0, 0.5f, 0.25f),
				new PositionTextureVertex(0, s * 2, 0, 0.5f, 0.25f)
		}, 12, 0, 16, 4, textureWidth, textureHeight);
		quad.addFace(new PositionTextureVertex[] {
				new PositionTextureVertex(-s, 0, -s, 0.5f, 0.25f),
				new PositionTextureVertex(s, 0, -s, 0.75f, 0),
				new PositionTextureVertex(0, -s * 2, 0, 0.75f, 0.25f),
				new PositionTextureVertex(0, -s * 2, 0, 0.75f, 0.25f)
		}, 12, 4, 16, 8, textureWidth, textureHeight);
		
		quad.addFace(new PositionTextureVertex[] {
				new PositionTextureVertex(-s, 0, s, 0.75f, 0.5f),
				new PositionTextureVertex(-s, 0, -s, 1, 0.25f),
				new PositionTextureVertex(0, s * 2, 0, 0.75f, 0.25f),
				new PositionTextureVertex(0, s * 2, 0, 0.75f, 0.25f)
		}, 8, 0, 12, 4, textureWidth, textureHeight);
		quad.addFace(new PositionTextureVertex[] {
				new PositionTextureVertex(-s, 0, s, 0.75f, 0.25f),
				new PositionTextureVertex(-s, 0, -s, 1, 0),
				new PositionTextureVertex(0, -s * 2, 0, 1, 0.25f),
				new PositionTextureVertex(0, -s * 2, 0, 1, 0.25f)
		}, 8, 4, 8, 8, textureWidth, textureHeight);
		
		body.addQuad(quad);
	}
	
	public void render(Entity entity, float time, float walkSpeed, float stutter, float yaw, float pitch, float scale) {
		GlStateManager.pushMatrix();
		EntitySpell spell = (EntitySpell)entity;
		
		float var15 = MathHelper.sin(((float)spell.ticksExisted + stutter) / 10.0F + spell.hoverStart) * 0.1F + 0.1F;
        GlStateManager.translate(0, var15, 0);
		
        var15 = ((float)spell.ticksExisted + stutter) / 20;
        if (spell.getLevel() > 0) {
        	var15 *= spell.getLevel() + 1;
        }
        var15 += spell.hoverStart;
        var15 *= 180F / (float)Math.PI;
        GlStateManager.rotate(var15, 0.0F, 1.0F, 0.0F);
        
        body.render(scale);
        
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.blendFunc(1, 1);
        
        char var9 = 61680;
        int var10 = var9 % 65536;
        int var11 = var9 / 65536;
        
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)var10 / 1.0F, (float)var11 / 1.0F);
        int colour = SpellList.getGemColour(spell.getEffect());
        GlStateManager.color(MCColor.r(colour), MCColor.g(colour), MCColor.b(colour), 1.0F);
        float glowFactor = 1.2f;
        GlStateManager.scale(glowFactor, glowFactor, glowFactor);
        GlStateManager.translate(0, 1 - glowFactor, 0);
        
		body.render(scale);
		
		int var12 = entity.getBrightnessForRender();
		var10 = var12 % 65536;
        var11 = var12 / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)var10 / 1.0F, (float)var11 / 1.0F);
		
		GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
		
		GlStateManager.popMatrix();
	}
	
	public void setRotationAngles(float time, float walkSpeed, float stutter, float yaw, float pitch, float increment, Entity entity) {
    	
    }
}
