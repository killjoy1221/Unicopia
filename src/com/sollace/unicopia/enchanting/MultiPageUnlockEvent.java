package com.sollace.unicopia.enchanting;

import com.sollace.unicopia.PlayerExtension;
import com.sollace.unicopia.enchanting.PagesList.PageEvent;

import net.minecraft.item.ItemStack;

/**
 * An unlock event that requires other pages to be unlocked before it too can be unlocked.
 * 
 */
public class MultiPageUnlockEvent implements PageEvent {
	
	private final int pageIndex;
	private final int[][] otherPageIndeces;
	
	public MultiPageUnlockEvent(int page, int[]... otherPages) {
		pageIndex = page;
		otherPageIndeces = otherPages;
	}
	
	public boolean matches(PlayerExtension prop, ItemStack stack) {
		for (int i = 0; i < otherPageIndeces.length; i++) {
			if (!checkPageUnlockSet(prop, otherPageIndeces[i])) return false;
		}
		return true;
	}
	
	private boolean checkPageUnlockSet(PlayerExtension prop, int[] pages) {
		for (int i = 0; i < pages.length; i++) {
			if (prop.hasPageUnlock(pages[i])) return true;
		}
		return false;
	}
	
	public int getPage(int stackSize) {
		return pageIndex;
	}
}
