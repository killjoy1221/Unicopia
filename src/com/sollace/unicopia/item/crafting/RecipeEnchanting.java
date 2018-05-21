package com.sollace.unicopia.item.crafting;

import java.util.Map;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class RecipeEnchanting implements IRecipe {
	
	private final ItemStack output;
	private final ItemStack input;
	private final Enchantment effect;
	
	public RecipeEnchanting(ItemStack output, ItemStack input, Enchantment effect) {
		this.output = output;
		this.input = input;
		this.effect = effect;
	}
	
	public ItemStack getRecipeOutput() {
        return output;
    }
	
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		NonNullList<ItemStack> stacks = NonNullList.<ItemStack>withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        for (int i = 0; i < stacks.size(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack != null) {
            	if (stack.getItem() == input.getItem() && stack.getMetadata() == input.getMetadata()) {
            		stack.setCount(0);
            	} else if (stack.hasEffect()) {
            		Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
	    			if (enchants.containsKey(effect)) {
	    				if (stack.getItem() == Items.ENCHANTED_BOOK) {
	    					stacks.set(i, new ItemStack(Items.BOOK));
    		            	stacks.get(i).setCount(stack.getCount());
	    	            } else {
		    				enchants.remove(effect);
		    				stacks.set(i, stack.copy());
							EnchantmentHelper.setEnchantments(enchants, stacks.get(i));
	    	            }
	    			}
	            }
            }
        }
        return stacks;
    }
	
	public boolean matches(InventoryCrafting inv, World worldIn) {
		boolean hasEnchant = false;
		boolean hasItem = false;
		for (int x = 0; x < inv.getWidth(); x++) {
			for (int y = 0; y < inv.getHeight(); y++) {
				ItemStack stack = inv.getStackInRowAndColumn(x, y);
				if (stack != null) {
					if (!hasEnchant && stack.hasEffect()) {
						Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
						if (enchants.containsKey(effect)) {
							hasEnchant = true;
							continue;
						}
					}
					if (!hasItem && stack.getItem() == input.getItem() && stack.getMetadata() == input.getMetadata()) {
						hasItem = true;
						continue;
					}
					return false;
				}
			}
		}
		
		return hasEnchant && hasItem;
	}
	
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack result = output.copy();
		for (int x = 0; x < inv.getWidth(); x++) {
			for (int y = 0; y < inv.getHeight(); y++) {
				ItemStack stack = inv.getStackInRowAndColumn(x, y);
				if (stack != null && stack.getItem() == input.getItem() && stack.getMetadata() == input.getMetadata()) {
					result.setCount(stack.getCount());
					return result;
				}
			}
		}
        return result;
    }
	
	@Override
	public boolean canFit(int width, int height) {
		return width * height > 2;
	}
}
