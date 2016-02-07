package com.sollace.unicopia.enchanting;

import com.sollace.unicopia.PlayerExtension;
import com.sollace.unicopia.Unicopia.UItems;
import com.sollace.unicopia.effect.SpellList;
import com.sollace.unicopia.enchanting.PagesList.PageEvent;

import net.minecraft.item.ItemStack;

/**
 * A basic event for unlocking a page when a gem is crafted for the given spell
 * 
 */
public class BasicCraftingEvent implements PageEvent {
	
	private final int matched;
	private final int pageIndex;
	
	public BasicCraftingEvent(int page, String effectName) {
		matched = SpellList.getId(effectName);
		pageIndex = page;
	}
	
	public boolean matches(PlayerExtension prop, ItemStack stack) {
		return stack.getItem() == UItems.spell && stack.getMetadata() == matched;
	}
	
	public int getPage(int stackSize) {
		return pageIndex;
	}

}
