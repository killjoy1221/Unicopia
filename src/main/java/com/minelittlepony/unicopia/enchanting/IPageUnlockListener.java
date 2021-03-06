package com.minelittlepony.unicopia.enchanting;

@FunctionalInterface
public interface IPageUnlockListener {
    /**
     * Called when a page is unlocked.
     *
     * @param page The page that has been unlocked
     * @return True to allow, false to block.
     */
	boolean onPageUnlocked(IPage page);
}
