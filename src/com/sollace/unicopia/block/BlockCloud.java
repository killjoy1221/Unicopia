package com.sollace.unicopia.block;

import java.util.List;

import javax.annotation.Nullable;

import com.sollace.unicopia.Race;
import com.sollace.unicopia.entity.EntityCloud;
import com.sollace.unicopia.server.PlayerSpeciesRegister;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCloud extends Block {
	
	public static final PropertyEnum<EnumType> VARIANT = PropertyEnum.create("variant", EnumType.class);
	
	public BlockCloud(Material material, String name) {
		super(material);
		setCreativeTab(CreativeTabs.MISC);
		setHardness(0.5f);
		setResistance(1.0F);
		setSoundType(SoundType.CLOTH);
		setLightOpacity(20);
		setUnlocalizedName(name);
		setDefaultState(blockState.getBaseState().withProperty(VARIANT, EnumType.NORMAL));
		useNeighborBrightness = true;
	}
	
	//Render inside?
    public boolean isTranslucent(IBlockState state) {
        return true;
    }
    
    //Render blocks behind?
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    //Push player out of block
    public boolean isFullCube(IBlockState state) {
        return true;//false;
    }
    
    public boolean isNormalCube(IBlockState state) {
    	return false;
    }
    
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }
    
    //Can entities walk through?
    @Override
    public boolean isPassable(IBlockAccess w, BlockPos pos) {
        return super.isPassable(w, pos);
    }
	
    @Override
    public void onFallenUpon(World world, BlockPos pos, Entity entityIn, float fallDistance) {
        if (entityIn.isSneaking()) {
            super.onFallenUpon(world, pos, entityIn, fallDistance);
        } else {
            entityIn.fall(fallDistance, 0);
        }
    }
    
    @Override
    public void onLanded(World worldIn, Entity entityIn) {
        if (entityIn.isSneaking()) {
            super.onLanded(worldIn, entityIn);
        } else if (entityIn.motionY < 0.0D) {
            entityIn.motionY = -entityIn.motionY;
        }
    }
    
    @Override
	public void onEntityCollidedWithBlock(World w, BlockPos pos, IBlockState state, Entity entity) {
		if (!entity.isSneaking() && Math.abs(entity.motionY) >= 0.25d) {
			entity.motionY += 0.0155*(entity.fallDistance < 1 ? 1 : entity.fallDistance);
		} else {
			entity.motionY = 0;
		}
		super.onEntityCollidedWithBlock(w, pos, state, entity);
	}
    
    @Deprecated
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean p_185477_7_) {
    	if (getCanInteract(state, entity)) {
    		super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entity, p_185477_7_);
    	}
    }
    
	public static boolean getCanInteract(IBlockState state, Entity e) {
		EnumType type = (EnumType)state.getValue(VARIANT);
		if (type == EnumType.ENCHANTED) {
			return true;
		}
		if (e instanceof EntityPlayer) {
			if (type == EnumType.PACKED) return true;
			Race species = PlayerSpeciesRegister.getPlayerSpecies((EntityPlayer)e);
			return species.canInteractWithClouds() || EntityCloud.getFeatherEnchantStrength((EntityPlayer)e) > 0;
		}
		if (e instanceof EntityCloud && e.isRiding()) {
			return getCanInteract(state, e.getRidingEntity());
		}
		return false;
	}
	
    public int damageDropped(IBlockState state) {
        return ((EnumType)state.getValue(VARIANT)).getMetadata();
    }
    
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (EnumType i : EnumType.values()) {
        	list.add(new ItemStack(this, 1, i.getMetadata()));
        }
    }

    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(VARIANT, EnumType.byMetadata(meta));
    }
    
    public int getMetaFromState(IBlockState state) {
        return ((BlockCloud.EnumType)state.getValue(VARIANT)).getMetadata();
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {VARIANT});
    }
	
	public static enum EnumType implements IStringSerializable {
		NORMAL("NORMAL", 0, "normal"),
		PACKED("PACKED", 1, "packed"),
		ENCHANTED("ENCHANTED", 2, "enchanted");
		
		private String name;
		private int meta;
		private String unlocalizedName;
		
		private static final EnumType[] META_LOOKUP = new EnumType[values().length];
		
		EnumType(String name, int meta, String unlocalised) {
			this.name = name;
			this.meta = meta;
			this.unlocalizedName = unlocalised;
		}

        public static EnumType byMetadata(int meta) {
            if (meta < 0 || meta >= META_LOOKUP.length) meta = 0;
            return META_LOOKUP[meta];
        }
        
        public String toString() {
            return name;
        }
		
		public String getName() {
			return unlocalizedName;
		}
		
		public String getUnlocalizedName() {
            return unlocalizedName;
        }
		
		public int getMetadata() {
            return meta;
        }
		
		static {
            for (EnumType i : values()) {
                META_LOOKUP[i.getMetadata()] = i;
            }
        }
	}
}
