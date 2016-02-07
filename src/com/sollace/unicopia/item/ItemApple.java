package com.sollace.unicopia.item;

import java.util.List;
import java.util.Random;

import com.blazeloader.api.recipe.IFuel;
import com.sollace.util.MagicalDamageSource;
import com.sollace.util.VecHelper;

import net.minecraft.block.BlockPlanks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;

public class ItemApple extends ItemFood implements IFuel {
	
	private int[] typeRarities = new int[0];
	private String[] subTypes = new String[0];
	private String[] variants = subTypes;
	
	public int getRandomAppleMetadata(Random rand, Object variant) {
		int[] rarity = typeRarities;
		int result = 0;
		for (int i = 0; i < rarity.length && i < subTypes.length; i++) {
			if (rand.nextInt(rarity[i]) == 0) {
				result++;
			}
		}
		if (variant == BlockPlanks.EnumType.JUNGLE) {
			result = result == 0 ? 1 : result == 1 ? 0 : result;
		}
		if (variant == BlockPlanks.EnumType.SPRUCE) {
			result = result == 0 ? 3 : result == 3 ? 0 : result;
		}
		if (variant == BlockPlanks.EnumType.BIRCH) {
			result = result == 0 ? 2 : result == 2 ? 0 : result;
		}
		if (variant == BlockPlanks.EnumType.DARK_OAK) {
			result = result == 1 ? getZapAppleMetadata() : result == getZapAppleMetadata() ? 1 : result;
		}
		return result;
	}
	
	public ItemApple() {
		super(4, 3, false);
		setHasSubtypes(true);
		setMaxDamage(0);
	}
	
	
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		MovingObjectPosition mop = VecHelper.getObjectMouseOver(player, 5, 0);
		if (mop != null && mop.typeOfHit == MovingObjectType.ENTITY) {
			if (canFeedTo(stack, mop.entityHit)) {
				return onFedTo(stack, player, mop.entityHit);
			}
		}
		return super.onItemRightClick(stack, world, player);
	}
	
	protected void onFoodEaten(ItemStack stack, World w, EntityPlayer player) {
		super.onFoodEaten(stack, w, player);
		if (isZapApple(stack)) {
			player.attackEntityFrom(MagicalDamageSource.create("zap"), 120);
			w.addWeatherEffect(new EntityLightningBolt(w, player.posX, player.posY, player.posZ));
		}
	}
	
	public boolean canFeedTo(ItemStack stack, Entity e) {
		return isZapApple(stack) && (e instanceof EntityVillager || e instanceof EntityCreeper || e instanceof EntityPig);
	}
	
	public boolean isZapApple(ItemStack stack) {
		int meta = stack.getMetadata();
		return meta == getZapAppleMetadata() || meta >= subTypes.length;
	}
	
	public ItemStack onFedTo(ItemStack stack, EntityPlayer player, Entity e) {
		e.onStruckByLightning(new EntityLightningBolt(e.worldObj, e.posX, e.posY, e.posZ));
		if (!player.capabilities.isCreativeMode) stack.stackSize--;
		return stack;
	}
	
	public int getZapAppleMetadata() {
		return 4;
	}
	
	public ItemApple setSubTypes(String... types) {
		subTypes = types;
		variants = new String[subTypes.length * 2];
		setUnlocalizedName(variants[0] = types[0]);
		for (int i = 1; i < variants.length; i++) {
			variants[i] = variants[0] + (i % subTypes.length != 0 ? "_" + subTypes[i % subTypes.length] : "");
		}
		return this;
	}
	
	public ItemApple setTypeRarities(int ... rarity) {
		typeRarities = rarity;
		return this;
	}
	
	public String[] getVariants() {
		return variants;
	}

	public void getSubItems(Item item, CreativeTabs tab, List subItems) {
		for (int i = 0; i < subTypes.length; i++) {
			subItems.add(new ItemStack(item, 1, i));
		}
	}
	
	public EnumRarity getRarity(ItemStack stack) {
		int meta = stack.getMetadata();
		if (meta == getZapAppleMetadata()) return EnumRarity.EPIC;
        if (meta >= subTypes.length) return EnumRarity.RARE;
        return EnumRarity.COMMON;
    }
	
	/*public String getItemStackDisplayName(ItemStack stack) {
		String result = super.getItemStackDisplayName(stack);
        if (stack.getMetadata() >= subTypes.length) {
        	return ChatColor.ITALIC + result;
        }
        return result;
    }*/
	
	public String getUnlocalizedName(ItemStack stack) {
		int meta = stack.getMetadata() % subTypes.length;
		if (meta < 0) meta = 0;
		return super.getUnlocalizedName(stack) + (meta > 0 ? "." + subTypes[meta] : "");
	}
	
	public int getBurnTime(ItemStack stack) {
		return stack.getMetadata() == 2 ? 150 : 0;
	}
}
