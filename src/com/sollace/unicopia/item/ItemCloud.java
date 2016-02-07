package com.sollace.unicopia.item;

import java.util.List;

import com.sollace.unicopia.entity.EntityCloud;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;

public class ItemCloud extends Item {
	
	public ItemCloud(String name) {
		super();
		setHasSubtypes(true);
		setMaxDamage(0);
		setUnlocalizedName(name);
        maxStackSize = 16;
        setCreativeTab(CreativeTabs.tabMaterials);
	}
	
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
    	if (!world.isRemote) {
    		MovingObjectPosition mop = getMovingObjectPositionFromPlayer(world, player, true);
    		
    		int x, y, z;
    		if (mop != null && mop.typeOfHit == MovingObjectType.BLOCK) {
    			x = mop.getBlockPos().getX() + mop.sideHit.getFrontOffsetX();
    			y = mop.getBlockPos().getY() + mop.sideHit.getFrontOffsetY();
    			z = mop.getBlockPos().getZ() + mop.sideHit.getFrontOffsetZ();
    		} else {
    			x = MathHelper.floor_double(player.posX);
    			y = MathHelper.floor_double(player.posY);
    			z = MathHelper.floor_double(player.posZ);
    		}
    		
            EntityCloud cloud = new EntityCloud(world);
    		cloud.setLocationAndAngles(x, y, z, 0, 0);
	    	cloud.setStationary(true);
	    	cloud.setOpaque(true);
	    	cloud.setCloudSize(1 + (stack.getItemDamage() % 3));
	    	world.spawnEntityInWorld(cloud);
	    	if (!player.capabilities.isCreativeMode) stack.stackSize--;
    	}
        return stack;
    }
    
    public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack) + "." + CloudSize.byMetadata(stack.getItemDamage()).getName();
    }
    
    public void getSubItems(Item item, CreativeTabs tab, List subs) {
        for (CloudSize i : CloudSize.values()) {
        	subs.add(new ItemStack(item, 1, i.getMetadata()));
        }
    }
    
    public static enum CloudSize {
    	SMALL("small", 0), MEDIUM("medium", 1), LARGE("large", 2);
    	
    	private final String name;
    	private final int metadata;
    	
    	CloudSize(String name, int meta) {
    		this.name = name;
    		metadata = meta;
    	}
    	
    	public String getName() {
    		return name;
    	}
    	
    	public int getMetadata() {
    		return metadata;
    	}
    	
    	public static CloudSize byMetadata(int meta) {
    		if (meta < 0 || meta >= META_LOOKUP.length) meta = 0;
    		return META_LOOKUP[meta];
    	}
    	
    	private static final CloudSize[] META_LOOKUP = new CloudSize[values().length];
    	static {
    		CloudSize[] values = values();
    		for (CloudSize i : values) {
    			META_LOOKUP[i.getMetadata()] = i;
    		}
    	}
    }
}
