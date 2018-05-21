package com.sollace.unicopia.effect;

import net.minecraft.dispenser.IBlockSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;

/**
 * Represents an object with an action to perform when dispensed from a dispenser.
 * 
 */
public interface IDispenceable {
	
	/**
	 * Called when dispensed.
	 * 
	 * @param pos		Block position in front of the dispenser
	 * @param facing	Direction of the dispenser
	 * @param source	The dispenser currently dispensing
	 * @return	an ActionResult for the type of action to perform.
	 */
	public ActionResult onDispenced(BlockPos pos, EnumFacing facing, IBlockSource source);
}
