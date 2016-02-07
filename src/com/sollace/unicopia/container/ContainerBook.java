package com.sollace.unicopia.container;

import com.sollace.unicopia.PlayerExtension;
import com.sollace.unicopia.Unicopia;
import com.sollace.unicopia.Unicopia.UItems;
import com.sollace.unicopia.enchanting.IPageUnlockListener;
import com.sollace.unicopia.enchanting.InventoryEnchanting;
import com.sollace.unicopia.enchanting.SlotEnchantingResult;
import com.sollace.unicopia.server.PlayerSpeciesRegister;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class ContainerBook extends Container {
	
	private final World worldObj;
	
	private IInventory craftResult = new InventoryBasic("Spell Result", false, 1);
	
	private InventoryEnchanting craftMatrix = new InventoryEnchanting(craftResult, this, 2, 2);
	
	private boolean hasInit = false;
	
	private IPageUnlockListener listener;
	
	private SlotEnchantingResult resultSlot = null;
	
	public ContainerBook(InventoryPlayer inventory, World world, BlockPos pos) {
		super();
		worldObj = world;
		
		for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new Slot(inventory, i, 121 + i * 18, 195));
        }
		
		onCraftMatrixChanged(craftMatrix);
		if (PlayerExtension.get(inventory.player).getMaxUnlocked() > 0) {
			initCraftingSlots(inventory.player);
		}
	}
	
	public void setListener(IPageUnlockListener listener) {
		this.listener = listener;
		if (resultSlot != null) resultSlot.setListener(listener);
	}
	
	public void initCraftingSlots(EntityPlayer player) {
		if (!hasInit) {
			hasInit = true;
			addSlotToContainer(new Slot(craftMatrix, 0, 171, 51));
			addSlotToContainer(new Slot(craftMatrix, 1, 171, 133));
			addSlotToContainer(new Slot(craftMatrix, 2, 221, 133));
			addSlotToContainer(new Slot(craftMatrix, 3, 221, 51));
			addSlotToContainer(resultSlot = new SlotEnchantingResult(listener, player, craftMatrix, craftResult, 0, 196, 92));
		}
	}
	
	public void onCraftMatrixChanged(IInventory inventoryIn) {
		ItemStack current = craftResult.getStackInSlot(0);
		if (current != null) {
			ItemStack crafted = Unicopia.getCraftingManager().findMatchingRecipe(craftMatrix, worldObj);
			if (crafted != null) {
				current.setItemDamage(crafted.getItemDamage());
			} else {
				current.setItemDamage(0);
			}
		}
	}
	
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack stack = null;
        Slot slot = (Slot)inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();

            if (index < 9) {
                if (!mergeItemStack(slotStack, 9, 13, true)) {
                    return null;
                }
            } else if (!mergeItemStack(slotStack, 0, 9, false)) {
                return null;
            }

            if (slotStack.stackSize == 0) {
                slot.putStack((ItemStack)null);
            } else {
                slot.onSlotChanged();
            }
        }

        return stack;
    }
	
	protected void retrySlotClick(int slot, int clickedButton, boolean mode, EntityPlayer player) {
		super.retrySlotClick(slot, clickedButton, mode, player);
	}
	
	protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean useEndIndex) {
        boolean result = false;
        int i = useEndIndex ? endIndex - 1 : startIndex;
        
        Slot slot;
        ItemStack inSlot;
        
        if (stack.getItem() == UItems.spell && startIndex >= 9) {
        	slot = (Slot)inventorySlots.get(13);
        	inSlot = slot.getStack();
        	if (inSlot == null) {
        		slot.putStack(stack.copy());
                slot.onSlotChanged();
                stack.stackSize = 0;
                return true;
        	} else if (inSlot.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getMetadata() == inSlot.getMetadata()) && ItemStack.areItemStackTagsEqual(stack, inSlot)) {
                int var9 = inSlot.stackSize + stack.stackSize;
                if (var9 <= stack.getMaxStackSize()) {
                    stack.stackSize = 0;
                    inSlot.stackSize = var9;
                    slot.onSlotChanged();
                    return true;
                } else if (inSlot.stackSize < stack.getMaxStackSize()) {
                    stack.stackSize -= stack.getMaxStackSize() - inSlot.stackSize;
                    inSlot.stackSize = stack.getMaxStackSize();
                    slot.onSlotChanged();
                    result = true;
                }
            }
        }
        
        if (stack.isStackable()) {
            while (stack.stackSize > 0 && (!useEndIndex && i < endIndex || useEndIndex && i >= startIndex)) {
                slot = (Slot)inventorySlots.get(i);
                inSlot = slot.getStack();
                if (slot.isItemValid(stack)) {
	                if (inSlot != null && inSlot.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getMetadata() == inSlot.getMetadata()) && ItemStack.areItemStackTagsEqual(stack, inSlot)) {
	                    int var9 = inSlot.stackSize + stack.stackSize;
	                    if (var9 <= stack.getMaxStackSize()) {
	                        stack.stackSize = 0;
	                        inSlot.stackSize = var9;
	                        slot.onSlotChanged();
	                        result = true;
	                    } else if (inSlot.stackSize < stack.getMaxStackSize()) {
	                        stack.stackSize -= stack.getMaxStackSize() - inSlot.stackSize;
	                        inSlot.stackSize = stack.getMaxStackSize();
	                        slot.onSlotChanged();
	                        result = true;
	                    }
	                }
                }

                if (useEndIndex) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        if (stack.stackSize > 0) {
            if (useEndIndex) {
                i = endIndex - 1;
            } else {
                i = startIndex;
            }
            
            while (!useEndIndex && i < endIndex || useEndIndex && i >= startIndex) {
                slot = (Slot)this.inventorySlots.get(i);
                if (slot.isItemValid(stack)) {
	                inSlot = slot.getStack();
	                if (inSlot == null) {
	                    slot.putStack(stack.copy());
	                    slot.onSlotChanged();
	                    stack.stackSize = 0;
	                    return true;
	                }
                }
                
                if (useEndIndex) {
                    --i;
                } else {
                    ++i;
                }
            }
        }
        return result;
    }
	
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
			if (craftMatrix.getStackInSlot(i) != null) {
				player.dropPlayerItemWithRandomChoice(craftMatrix.getStackInSlotOnClosing(i), false);
				craftMatrix.setInventorySlotContents(i, null);
			}
		}
		if (craftResult.getStackInSlot(0) != null) {
			player.dropPlayerItemWithRandomChoice(craftResult.getStackInSlotOnClosing(0), false);
			craftResult.setInventorySlotContents(0, null);
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return PlayerSpeciesRegister.getPlayerSpecies(player).canCast();
	}
}
