package com.sollace.unicopia.item;

import com.sollace.unicopia.Unicopia.UItems;
import com.sollace.unicopia.entity.EntityCloud;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ItemCloud extends Item {
	
	public ItemCloud(String name) {
		super();
		setHasSubtypes(true);
		setMaxDamage(0);
		setUnlocalizedName(name);
        maxStackSize = 16;
        setCreativeTab(CreativeTabs.MATERIALS);
	}
	
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
    	ItemStack stack = player.getHeldItem(hand);
    	
    	if (!world.isRemote) {
    		RayTraceResult mop = rayTrace(world, player, true);
    		
    		int x, y, z;
    		if (mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK) {
    			x = mop.getBlockPos().getX() + mop.sideHit.getFrontOffsetX();
    			y = mop.getBlockPos().getY() + mop.sideHit.getFrontOffsetY();
    			z = mop.getBlockPos().getZ() + mop.sideHit.getFrontOffsetZ();
    		} else {
    			x = MathHelper.floor(player.posX);
    			y = MathHelper.floor(player.posY);
    			z = MathHelper.floor(player.posZ);
    		}
    		
            EntityCloud cloud = new EntityCloud(world);
    		cloud.setLocationAndAngles(x, y, z, 0, 0);
	    	cloud.setStationary(true);
	    	cloud.setOpaque(true);
	    	cloud.setCloudSize(1 + (stack.getItemDamage() % 3));
	    	world.spawnEntity(cloud);
	    	if (!player.capabilities.isCreativeMode) stack.shrink(1);
    	}
        
    	return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }
    
    public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack) + "." + CloudSize.byMetadata(stack.getItemDamage()).getName();
    }
    
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subs) {
        for (CloudSize i : CloudSize.values()) {
        	subs.add(new ItemStack(UItems.cloud, 1, i.getMetadata()));
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
