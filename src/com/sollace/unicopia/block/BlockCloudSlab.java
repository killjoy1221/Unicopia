package com.sollace.unicopia.block;

import java.util.List;
import java.util.Random;

import com.sollace.unicopia.Unicopia;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCloudSlab extends BlockSlab {
	
	public static final PropertyEnum VARIANT = PropertyEnum.create("variant", BlockCloud.EnumType.class);
	
	private boolean isDouble;
	
	public BlockCloudSlab(boolean isDouble, Material material, String name) {
		super(material);
		setCreativeTab(CreativeTabs.tabBlock);
		setHardness(0.5F);
		setResistance(1.0F);
		setStepSound(Block.soundTypeCloth);
		setLightOpacity(20);
		setUnlocalizedName(name);
		this.isDouble = isDouble;
		useNeighborBrightness = true;
	}
	
    public boolean isVisuallyOpaque() {
        return Unicopia.UBlocks.cloud.isVisuallyOpaque();
    }
    
    public boolean isOpaqueCube() {
        return isDouble() ? Unicopia.UBlocks.cloud.isOpaqueCube() : false;
    }
    
    public boolean isFullCube() {
        return isDouble() ? Unicopia.UBlocks.cloud.isFullCube() : false;
    }
    
    public boolean isNormalCube() {
    	return isDouble() ? Unicopia.UBlocks.cloud.isNormalCube() : false;
    }
    
    public EnumWorldBlockLayer getBlockLayer() {
        return Unicopia.UBlocks.cloud.getBlockLayer();
    }
    
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return super.isPassable(worldIn, pos);
    }

    public void onFallenUpon(World w, BlockPos pos, Entity entity, float fallDistance) {
    	Unicopia.UBlocks.cloud.onFallenUpon(w, pos, entity, fallDistance);
    }
    
    public void onLanded(World w, Entity entity) {
    	Unicopia.UBlocks.cloud.onLanded(w, entity);
    }
    
	public void onEntityCollidedWithBlock(World w, BlockPos pos, IBlockState state, Entity entity) {
    	Unicopia.UBlocks.cloud.onEntityCollidedWithBlock(w, pos, entity);
    }
	
    public void onEntityCollidedWithBlock(World w, BlockPos pos, Entity entity) {
    	Unicopia.UBlocks.cloud.onEntityCollidedWithBlock(w, pos, entity);
    }
    
    public void addCollisionBoxesToList(World w, BlockPos pos, IBlockState state, AxisAlignedBB axis, List list, Entity entity) {
        super.setBlockBoundsBasedOnState(w, pos);
        if (BlockCloud.getCanInteract(state, entity)) {
	        double maxy = pos.getY() + maxY;
	        maxy -= 0.1;
	        
	        AxisAlignedBB axisalignedbb1 = AxisAlignedBB.fromBounds(pos.getX() + minX, pos.getY() + minY, pos.getZ() + minZ, pos.getX() + maxX, maxy, pos.getZ() + maxZ);
	    	
	        if (axisalignedbb1 != null && axis.intersectsWith(axisalignedbb1)) {
	            list.add(axisalignedbb1);
	        }
        }
    }
    
    public Item getItemDropped(int side, Random rand, int data) {
        return Item.getItemFromBlock(Unicopia.UBlocks.cloud_slab);
    }
	
    protected ItemStack createStackedBlock(int data) {
        return new ItemStack(Item.getItemFromBlock(Unicopia.UBlocks.cloud_slab), 2, data & 7);
    }
    
    public Item getItem(World w, int x, int y, int z) {
        return Item.getItemFromBlock(Unicopia.UBlocks.cloud_slab);
    }
    
    public String getUnlocalizedName(int meta) {
        return super.getUnlocalizedName() + "." + BlockCloud.EnumType.byMetadata(meta).getUnlocalizedName();
    }

    public IProperty getVariantProperty() {
        return VARIANT;
    }

    public Object getVariant(ItemStack stack) {
        return BlockCloud.EnumType.byMetadata(stack.getMetadata() & 7);
    }
    
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
    	Unicopia.UBlocks.cloud.getSubBlocks(itemIn, tab, list);
    }
    
    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = getDefaultState().withProperty(VARIANT, BlockCloud.EnumType.byMetadata(meta & 7));
        if (!isDouble()) {
            state = state.withProperty(HALF, (meta & 8) == 0 ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP);
        }
        return state;
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state) {
        byte mask = 0;
        int result = mask | ((BlockCloud.EnumType)state.getValue(VARIANT)).getMetadata();
        if (!isDouble() && state.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP) {
            result |= 8;
        }
        return result;
    }

    protected BlockState createBlockState() {
        return isDouble() ? new BlockState(this, new IProperty[] {VARIANT}): new BlockState(this, new IProperty[] {HALF, VARIANT});
    }
    
    public int damageDropped(IBlockState state) {
        return ((BlockCloud.EnumType)state.getValue(VARIANT)).getMetadata();
    }
    
	public boolean isDouble() {
		return isDouble;
	}
}
