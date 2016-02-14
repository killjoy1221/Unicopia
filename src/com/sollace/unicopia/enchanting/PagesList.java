package com.sollace.unicopia.enchanting;

import java.util.ArrayList;
import java.util.List;

import com.sollace.unicopia.PlayerExtension;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class PagesList {
	
	private static final List<Integer> unreadPages = new ArrayList<Integer>();
	
	private static final List<PageEvent> pageEvents = new ArrayList<PageEvent>();
	
	private static int totalPages = 0;
	
	/**
	 * Sets the maximum number of pages displayed in the spellbook.
	 * Only allows widening. Total pages cannot be reduced.
	 */
	public static void setTotalPages(int pages) {
		if (pages > totalPages) {
			totalPages = pages;
		}
	}
	
	public static int getTotalPages() {
		return totalPages;
	}
	
	/**
	 * Registers an event for unlocking a page.
	 */
	public static void registerPageEvent(PageEvent event) {
		pageEvents.add(event);
	}
	
	/**
	 * Marks a page as read
	 */
	public static void readPage(int pageIndex) {
		unreadPages.remove(Integer.valueOf(pageIndex));
	}
	
	/**
	 * Checks if there are any pages after the given index that are unread
	 * Only useful on the client
	 */
	public static boolean hasUnreadPagesAfter(int pageIndex) {
		for (Integer i : unreadPages) {
			if (i > pageIndex) return true;
		}
		return false;
	}
	
	/**
	 * Checks if there are any pages before the given index that are unread
	 * Only useful on the client
	 */
	public static boolean hasUnreadPagesBefore(int pageIndex) {
		for (Integer i : unreadPages) {
			if (i < pageIndex) return true;
		}
		return false;
	}
	
	/**
	 * Checks if the given page has been read yet.
	 * Only of use on the client
	 */
	public static boolean isPageUnread(int pageIndex) {
		return unreadPages.contains(pageIndex);
	}
	
	private static boolean unlockPages(PlayerExtension prop, ItemStack stack) {
		boolean result = false;
		if (stack != null && stack.stackSize > 0) {
			for (PageEvent i : pageEvents) {
				if (i.matches(prop, stack)) {
					int page = i.getPage(stack.stackSize);
					if (page >= 0 && prop.unlockPage(page)) {
						result |= unreadPages.add(page);
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * Checks for, and unlocks any pages that can be unlocked by the given item for the given player
	 * @return	True if a page was unlocked, false otherwise
	 */
	public static boolean unlockPage(EntityPlayer player, ItemStack stack) {
		PlayerExtension prop = PlayerExtension.get(player);
		return unlockPages(prop, stack);
	}
	
	/**
	 * A PageEvent for determining when certain pages must be unlocked.
	 * 
	 */
	public static interface PageEvent {
		/**
		 * Checks if this event's conditions are met.
		 * @param prop		PlayerExtension for the player doing the crafting
		 * @param stack		ItemStack crafted
		 */
		public boolean matches(PlayerExtension prop, ItemStack stack);
		
		/**
		 * Gets the page number corresponding to the given stack for this event
		 */
		public int getPage(int stackSize);
	}
}
