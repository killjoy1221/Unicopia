package com.minelittlepony.unicopia.item;

import com.minelittlepony.unicopia.spell.IAligned;
import com.minelittlepony.unicopia.spell.SpellAffinity;

import net.minecraft.item.ItemStack;

public interface IMagicalItem extends IAligned {
    /**
     * If true this item serves as host to its own inner dimensional space.
     * Bag of Holding will explode if you try to store items of this kind inside of it.
     */
    default boolean hasInnerSpace() {
        return false;
    }

    /**
     * Gets the affinity of this magical artifact. Either good, bad, or unaligned.
     * What this returns may have effects on the behaviour of certain spells and effects.
     */
    default SpellAffinity getAffinity(ItemStack stack) {
        return getAffinity();
    }
}
