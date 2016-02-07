package com.sollace.util;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

/**
 * Alternative version of Material that allows public access to its methods
 */
public class BlockMaterial extends Material {
	
    /** Indicates if the material is translucent */
    private boolean isTranslucent;

    public BlockMaterial(MapColor color) {
    	super(color);
    }
    
    public boolean isOpaque() {
        return isTranslucent ? false : blocksMovement();
    }
    
    public BlockMaterial setReplaceable() {
        return (BlockMaterial)super.setReplaceable();
    }
    
    /**
     * Marks the material as translucent
     */
    public BlockMaterial setTranslucent() {
        isTranslucent = true;
        return this;
    }
    
    public BlockMaterial setRequiresTool() {
        return (BlockMaterial)super.setRequiresTool();
    }
    
    public BlockMaterial setBurning() {
        return (BlockMaterial)super.setBurning();
    }
    
    public BlockMaterial setNoPushMobility() {
        return (BlockMaterial)super.setNoPushMobility();
    }
    
    public BlockMaterial setImmovableMobility() {
        return (BlockMaterial)super.setImmovableMobility();
    }
    
    public BlockMaterial setAdventureModeExempt() {
        return (BlockMaterial)super.setAdventureModeExempt();
    }
}
