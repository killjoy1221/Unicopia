package com.sollace.unicopia.block;

import java.util.List;

import com.sollace.unicopia.Race;
import com.sollace.unicopia.entity.EntityCloud;
import com.sollace.unicopia.server.PlayerSpeciesRegister;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCloud extends Block {
	
	public static final PropertyEnum VARIANT = PropertyEnum.create("variant", EnumType.class);
	
	public BlockCloud(Material material, String name) {
		super(material);
		setCreativeTab(CreativeTabs.tabMisc);
		setHardness(0.5f);
		setResistance(1.0F);
		setStepSound(soundTypeCloth);
		setLightOpacity(20);
		setUnlocalizedName(name);
		setDefaultState(blockState.getBaseState().withProperty(VARIANT, EnumType.NORMAL));
		useNeighborBrightness = true;
	}
	
	//Render inside?
    public boolean isVisuallyOpaque() {
        return false;
    }
    
    //Render blocks behind?
    public boolean isOpaqueCube() {
        return false;
    }
    
    //Push player out of block
    public boolean isFullCube() {
        return true;//false;
    }
    
    public boolean isNormalCube() {
    	return false;
    }
    
    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT_MIPPED;
    }
    
    //Can entities walk through?
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return super.isPassable(worldIn, pos);
    }
	
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
        if (entityIn.isSneaking()) {
            super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
        } else {
            entityIn.fall(fallDistance, 0);
        }
    }
    
    public void onLanded(World worldIn, Entity entityIn) {
        if (entityIn.isSneaking()) {
            super.onLanded(worldIn, entityIn);
        } else if (entityIn.motionY < 0.0D) {
            entityIn.motionY = -entityIn.motionY;
        }
    }
    
	public void onEntityCollidedWithBlock(World w, BlockPos pos, Entity entity) {
		if (!entity.isSneaking() && Math.abs(entity.motionY) >= 0.25d) {
			entity.motionY += 0.0155*(entity.fallDistance < 1 ? 1 : entity.fallDistance);
		} else {
			entity.motionY = 0;
		}
		super.onEntityCollidedWithBlock(w, pos, entity);
	}
    
    public void addCollisionBoxesToList(World w, BlockPos pos, IBlockState state, AxisAlignedBB p_149743_5_, List p_149743_6_, Entity entity) {
		if (getCanInteract(state, entity)) {
			AxisAlignedBB axisalignedbb1 = AxisAlignedBB.fromBounds(pos.getX() + minX, pos.getY() + minY, pos.getZ() + minZ, pos.getX() + maxX, pos.getY() + maxY - 0.1, pos.getZ() + maxZ);
	        if (axisalignedbb1 != null && p_149743_5_.intersectsWith(axisalignedbb1)) {
	            p_149743_6_.add(axisalignedbb1);
	        }
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
		return false;
	}
	
    public int damageDropped(IBlockState state) {
        return ((EnumType)state.getValue(VARIANT)).getMetadata();
    }
    
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
        for (EnumType i : EnumType.values()) {
        	list.add(new ItemStack(itemIn, 1, i.getMetadata()));
        }
    }

    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(VARIANT, EnumType.byMetadata(meta));
    }
    
    public int getMetaFromState(IBlockState state) {
        return ((BlockCloud.EnumType)state.getValue(VARIANT)).getMetadata();
    }

    protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[] {VARIANT});
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
