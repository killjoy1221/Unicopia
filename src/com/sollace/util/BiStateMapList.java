package com.sollace.util;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

/**
 * Collection of two-way BlockState mappings.
 * 
 */
public class BiStateMapList extends ArrayList<IBiStateMapping> {
	
	/**
	 * Adds a new mapping from one block to the other. Returns the mapping object to allow specifying metadata mapping.
	 * @param from	Block to map from
	 * @param to	Block to map to
	 * @return	The resulting mapping object for further modification.
	 */
	public Entry add(Block from, Block to) {
		Entry mapping = new Entry(from, to);
		super.add(mapping);
		return mapping;
	}
	
	/**
	 * Checks if this collection contains a mapping capable of converting the given state.
	 * 
	 * @param state		State to check
	 * 
	 * @return	True if the state can be converted
	 */
	public boolean canConvert(IBlockState state) {
		for (IStateMapping i : this) {
			if (i.canConvert(state)) return true;
		}
		return false;
	}
	
	/**
	 * Attempts to convert the given state based on the known mappings in this collection.
	 * 
	 * @param state		State to convert
	 * 
	 * @return	The converted state if there is one, otherwise null
	 */
	public IBlockState getConverted(IBlockState state) {
		for (IStateMapping i : this) {
			if (i.canConvert(state)) return i.getConverted(state);
		}
		return null;
	}
	
	/**
	 * Checks if this collection contains a mapping for which the given state is a known output
	 * 
	 * @param state		State to check
	 * 
	 * @return	True if the state can be converted
	 */
	public boolean canRevert(IBlockState state) {
		for (IBiStateMapping i : this) {
			if (i.canRevert(state)) return true;
		}
		return false;
	}
	
	/**
	 * Attempts to convert the given state based on the known mappings in this collection.
	 * 
	 * @param state		State to convert
	 * 
	 * @return	The converted state if there is one, otherwise null
	 */
	public IBlockState getReverted(IBlockState state) {
		for (IBiStateMapping i : this) {
			if (i.canRevert(state)) return i.getReverted(state);
		}
		return null;
	}
	
	public static class Entry extends StateMapList.Entry implements IBiStateMapping {
		
		public Entry(Block from, Block to) {
			super(from, to);
		}
		
		/**
		 * Checks if the given state fits the criteria to be converted into a ungrassed variant
		 */
		public boolean canRevert(IBlockState state) {
			return canRevert(state.getBlock(), state.getBlock().getMetaFromState(state));
		}
		
		protected boolean canRevert(Block id, int data) {
			boolean result = toId == Block.getIdFromBlock(id);
			if (useData && result) {
				for (int i : toData) {
					if (i == data) return true;
				}
				return false;
			}
			return result;
		}
		
		public IBlockState getReverted(IBlockState state) {
			return getFrom().getStateFromMeta(getFromData(state.getBlock().getMetaFromState(state)));
		}
		
		/**
		 * Gets the block that this mapping will convert from.
		 */
		public Block getFrom() {
			return Block.getBlockById(fromId);
		}
		
		/**
		 * Gets the unconverted metadata based on the metadata value given. Assumes it will be used with the block returned from getFrom()
		 */
		public int getFromData(int data) {
			if (preserveData) return data;
			return useData ? (enforceData ? getFromDataByIndex(data) : data) : 0;
		}
		
		private int getFromDataByIndex(int data) {
			int index = getDataIndex(data);
			if (index < 0 || index > fromData.length) {
				index = 0;
			}
			return fromData[index];
		}
	}
}
