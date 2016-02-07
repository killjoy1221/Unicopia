package com.sollace.util;

import net.minecraft.block.state.IBlockState;

public interface IStateMapping {
	
	/**
	 * Checks if this state can be converted by this mapping
	 * 
	 * @param state	State to check
	 * 
	 * @return	True if the state can be converted
	 */
	public boolean canConvert(IBlockState state);
	
	/**
	 * Converts the given state based on this mapping
	 *  
	 * @param state	State to convert
	 * 
	 * @return	The converted state
	 */
	public IBlockState getConverted(IBlockState state);
}
