package com.sollace.unicopia.block;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.sollace.unicopia.Unicopia;
import com.sollace.unicopia.block.BlockCloud.EnumType;

import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCloudSlab extends BlockSlab {
	
	public static final PropertyEnum<EnumType> VARIANT = PropertyEnum.create("variant", EnumType.class);
	
	private boolean isDouble;
	
	public BlockCloudSlab(boolean isDouble, Material material, String name) {
		super(material);
		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
		setHardness(0.5F);
		setResistance(1.0F);
		setSoundType(SoundType.CLOTH);
		setLightOpacity(20);
		setUnlocalizedName(name);
		this.isDouble = isDouble;
		useNeighborBrightness = true;
	}
	
	@Deprecated
    public boolean isTranslucent(IBlockState state) {
        return Unicopia.UBlocks.cloud.isTranslucent(state);
    }
    
	@Deprecated
    public boolean isOpaqueCube(IBlockState state) {
        return isDouble() ? Unicopia.UBlocks.cloud.isOpaqueCube(state) : false;
    }
    
	@Deprecated
    public boolean isFullCube(IBlockState state) {
        return isDouble() ? Unicopia.UBlocks.cloud.isFullCube(state) : false;
    }
    
	@Deprecated
    public boolean isNormalCube(IBlockState state) {
    	return isDouble() ? Unicopia.UBlocks.cloud.isNormalCube(state) : false;
    }
    
    public BlockRenderLayer getBlockLayer() {
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
    	Unicopia.UBlocks.cloud.onEntityCollidedWithBlock(w, pos, state, entity);
    }
    
    @Deprecated
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean p_185477_7_) {
    	if (BlockCloud.getCanInteract(state, entity)) {
    		super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entity, p_185477_7_);
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

    public IProperty<EnumType> getVariantProperty() {
        return VARIANT;
    }

    public Object getVariant(ItemStack stack) {
        return BlockCloud.EnumType.byMetadata(stack.getMetadata() & 7);
    }
    
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
    	Unicopia.UBlocks.cloud.getSubBlocks(tab, list);
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

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, isDouble() ? new IProperty[] {VARIANT} : new IProperty[] {HALF, VARIANT});
    }
    
    public int damageDropped(IBlockState state) {
        return ((BlockCloud.EnumType)state.getValue(VARIANT)).getMetadata();
    }
    
	public boolean isDouble() {
		return isDouble;
	}

	@Override
	public Comparable<EnumType> getTypeForItem(ItemStack stack) {
		return EnumType.byMetadata(stack.getMetadata() & 7);
	}
}
