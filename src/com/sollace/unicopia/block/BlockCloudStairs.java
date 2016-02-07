package com.sollace.unicopia.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCloudStairs extends BlockStairs {
	
	protected Block theBlock;
	protected IBlockState theState;
	
	public BlockCloudStairs(IBlockState inherited, String name) {
		super(inherited);
		setUnlocalizedName(name);
		theBlock = inherited.getBlock();
		theState = inherited;
		useNeighborBrightness = true;
	}
	
    public boolean isVisuallyOpaque() {
        return theBlock.isVisuallyOpaque();
    }
    
    public boolean isNormalCube() {
    	return theBlock.isNormalCube();
    }
    
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return theBlock.isPassable(worldIn, pos);
    }
	
    public void onFallenUpon(World w, BlockPos pos, Entity entity, float fallDistance) {
    	theBlock.onFallenUpon(w, pos, entity, fallDistance);
    }
    
    public void onLanded(World w, Entity entity) {
    	theBlock.onLanded(w, entity);
    }
	
    public void onEntityCollidedWithBlock(World w, BlockPos pos, IBlockState state, Entity entity) {
		theBlock.onEntityCollidedWithBlock(w, pos, theState, entity);
	}
	
	public void onEntityCollidedWithBlock(World w, BlockPos pos, Entity entity) {
		theBlock.onEntityCollidedWithBlock(w, pos, entity);
	}
	
    public void addCollisionBoxesToList(World w, BlockPos pos, IBlockState state, AxisAlignedBB axis, List list, Entity entity) {
        if (BlockCloud.getCanInteract(theState, entity)) {
	        super.addCollisionBoxesToList(w, pos, state, axis, list, entity);
        }
    }
}