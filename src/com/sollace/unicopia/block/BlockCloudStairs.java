package com.sollace.unicopia.block;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
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
	
	public boolean isTranslucent(IBlockState state) {
        return true;
    }
    
    @SuppressWarnings("deprecation")
	public boolean isNormalCube(IBlockState state) {
    	return theBlock.isNormalCube(state);
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
	
	public void onEntityWalk(World w, BlockPos pos, Entity entity) {
		theBlock.onEntityWalk(w, pos, entity);
	}
	
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean p_185477_7_) {
        if (BlockCloud.getCanInteract(theState, entity)) {
	        super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entity, p_185477_7_);
        }
    }
}