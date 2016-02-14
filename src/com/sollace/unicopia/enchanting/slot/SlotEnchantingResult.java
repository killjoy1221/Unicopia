package com.sollace.unicopia.enchanting.slot;

import com.sollace.unicopia.Unicopia;
import com.sollace.unicopia.Unicopia.UItems;
import com.sollace.unicopia.enchanting.IPageUnlockListener;
import com.sollace.unicopia.enchanting.InventoryEnchanting;
import com.sollace.unicopia.enchanting.PagesList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotEnchantingResult extends SlotEnchanting {
	
	private final EntityPlayer thePlayer;
	private final InventoryEnchanting craftMatrix;
	
	private int amountCrafted;
	
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
	
	public ItemStack decrStackSize(int amount) {
        if (getHasStack()) {
            amountCrafted += Math.min(amount, getStack().stackSize);
        }

        return super.decrStackSize(amount);
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
            } else {
            	ItemStack inSlot = craftMatrix.getStackInSlot(i);
            	if (inSlot != null) {
            		if (inSlot.stackSize <= amountCrafted) {
            			craftMatrix.setInventorySlotContents(i, null);
            		}
            	} else {
            		craftMatrix.decrStackSize(i, amountCrafted);
            	}
            }
        }
    }
	
	protected void onCrafting(ItemStack stack, int amount) {
		amountCrafted += amount;
		onCrafting(stack);
	}
	
	protected void onCrafting(ItemStack stack) {
		amountCrafted = 0;
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