package com.sollace.unicopia.enchanting;

import com.sollace.unicopia.Unicopia;
import com.sollace.unicopia.Unicopia.UItems;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotEnchantingResult extends Slot {
	
	private final EntityPlayer thePlayer;
	private final InventoryEnchanting craftMatrix;
	
	private IPageUnlockListener listener;
	
	public SlotEnchantingResult(IPageUnlockListener listener, EntityPlayer player, InventoryEnchanting craftMatric, IInventory inventory, int index, int xPosition, int yPosition) {
		super(inventory, index, xPosition, yPosition);
		thePlayer = player;
		this.listener = listener;
		craftMatrix = craftMatric;
	}
	
	public void setListener(IPageUnlockListener listener) {
		this.listener = listener;
	}
	
	public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {
        this.onCrafting(stack);
        ItemStack current = craftMatrix.getCraftResultMatrix().getStackInSlot(0);
        craftMatrix.getCraftResultMatrix().setInventorySlotContents(0, stack);
        ItemStack[] remaining = Unicopia.getCraftingManager().getUnmatchedInventory(craftMatrix, player.worldObj);
        craftMatrix.getCraftResultMatrix().setInventorySlotContents(0, current);
        for (int i = 0; i < remaining.length; ++i) {
            current = craftMatrix.getStackInSlot(i);
            ItemStack remainder = remaining[i];
            
            if (current != null) {
            	if (stack.getMetadata() > 0) {
            		craftMatrix.decrStackSize(i, stack.stackSize);
            	}
            }
            
            if (remainder != null) {
                if (craftMatrix.getStackInSlot(i) == null) {
                    craftMatrix.setInventorySlotContents(i, remainder);
                }
            }
        }
        super.onPickupFromSlot(player, stack);
    }
	
	protected void onCrafting(ItemStack stack) {
		if (PagesList.unlockPage(thePlayer, stack) && listener != null) {
			listener.onPageUnlocked();
		}
		if (listener != null) {
			listener.onPageUnlocked();
		}
	}
	
	public boolean isItemValid(ItemStack stack) {
        return stack.getItem() == UItems.spell;
    }
}