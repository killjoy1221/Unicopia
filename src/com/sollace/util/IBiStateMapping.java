package com.sollace.util;

import net.minecraft.block.state.IBlockState;

public interface IBiStateMapping extends IStateMapping {
	
	/**
	 * Checks if this state corresponds to a possible output for this mapping.
	 * 
	 * @param state	State to check
	 * 
	 * @return	True if the state can be converted
	 */
	public boolean canRevert(IBlockState state);
	
	/**
	 * Converts the given state back into a source state (s) such that calling getConverted(s) returns the original state
	 *  
	 * @param state	State to convert
	 * 
	 * @return	The converted state
	 */
	public IBlockState getReverted(IBlockState state);
}
