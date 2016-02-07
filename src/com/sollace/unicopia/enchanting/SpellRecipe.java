package com.sollace.unicopia.enchanting;

import java.util.ArrayList;

import com.google.common.collect.Lists;
import com.sollace.unicopia.Unicopia.UItems;
import com.sollace.unicopia.effect.SpellList;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class SpellRecipe implements IRecipe {
	
	private int spellId;
	private RecipeItem[] recipeStacks = new RecipeItem[4];
	
	public SpellRecipe(String spellName, Object... stacks) {
		spellId = SpellList.getId(spellName);
		int i = 0;
		for (; i < stacks.length && i < recipeStacks.length; i++) {
			if (stacks[i] instanceof Item) {
				recipeStacks[i] = new RecipeItem(new ItemStack((Item)stacks[i], 1, 0), true);
			} else if (stacks[i] instanceof Block) {
				recipeStacks[i] = new RecipeItem(new ItemStack((Block)stacks[i], 1, 0), true);
			} else {
				recipeStacks[i] = new RecipeItem((ItemStack)stacks[i]);
			}
		}
		for (; i < recipeStacks.length; i++) {
			recipeStacks[i] = new RecipeItem(null);
		}
	}
	
	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		ItemStack enchantedStack = ((InventoryEnchanting)inv).getCraftResultMatrix().getStackInSlot(0);
		if (enchantedStack == null) return false;
		int materialMult = enchantedStack.stackSize;
		
		ArrayList<RecipeItem> toMatch = Lists.newArrayList(recipeStacks);
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			boolean match = false;
			if (toMatch.size() == 0 && stack != null) {
				return false;
			}
			for (RecipeItem s : toMatch) {
				if (stack == null) {
					if (s.contained == null) {
						match = true;
						toMatch.remove(s);
						break;
					}
				} else if (s.matches(stack, materialMult)) {
                    match = true;
                    toMatch.remove(s);
                    break;
                }
			}
			if (!match) return false;
		}
		return toMatch.isEmpty();
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		return getRecipeOutput();
	}

	@Override
	public int getRecipeSize() {
		return recipeStacks.length;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return new ItemStack(UItems.spell, 1, spellId);
	}

	@Override
	public ItemStack[] getRemainingItems(InventoryCrafting inv) {
		ItemStack[] remainers = new ItemStack[inv.getSizeInventory()];
        for (int i = 0; i < remainers.length; i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack != null && stack.getItem().hasContainerItem()) {
                remainers[i] = new ItemStack(stack.getItem().getContainerItem());
            }
        }
        return remainers;
	}
	
	private class RecipeItem {
		
		private final ItemStack contained;
		private final boolean ignoreMeta;
		
		public RecipeItem(ItemStack stack) {
			this(stack, false);
		}
		
		public RecipeItem(ItemStack stack, boolean meta) {
			contained = stack;
			ignoreMeta = meta;
		}
		
		public boolean matches(ItemStack other,  int materialMult) {
			if (contained == null) return other == null;
			if (other != null && contained.getItem() == other.getItem() && (ignoreMeta || other.getMetadata() == contained.getMetadata())) {
				return other.stackSize >= materialMult;
			}
			return false;
		}
	}
}
