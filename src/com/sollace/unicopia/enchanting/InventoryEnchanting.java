package com.sollace.unicopia.enchanting;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;

public class InventoryEnchanting extends InventoryCrafting {
	
	private final IInventory craftResult;
	
	public InventoryEnchanting(IInventory resultMatrix, Container eventHandler, int width, int height) {
		super(eventHandler, width, height);
		craftResult = resultMatrix;
	}
	
	public IInventory getCraftResultMatrix() {
		return craftResult;
	}
}
