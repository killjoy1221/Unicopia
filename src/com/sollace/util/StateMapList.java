package com.sollace.util;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

/**
 * A collection of block-state mappings.
 * 
 */
public class StateMapList extends ArrayList<IStateMapping> {
	
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
	
	public static class Entry implements IStateMapping {
		protected int fromId, toId;
		protected int[] fromData, toData;
		protected boolean useData = false, enforceData = false, preserveData = false;
		
		public Entry(Block from, Block to) {
			fromId = Block.getIdFromBlock(from);
			toId = Block.getIdFromBlock(to);
		}
		
		/**
		 * Sets this pair to match the grassed data to the corresponding ungrassed data by index.
		 */
		public Entry setDataRelated() {
			enforceData = true;
			return this;
		}
		
		/**
		 * Sets this pair to only change the block id, metadata is left unchanged.
		 */
		public Entry setPreserveData() {
			preserveData = true;
			return this;
		}
		
		/**
		 * Sets the metadata to be used by thie pair.
		 */
		public Entry setData(int from, int to) {
			return setData(new int[] { from }, new int[] { to });
		}
		
		/**
		 * Sets the metadata to be used by thie pair.
		 */
		public Entry setData(int[] from, int[] to) {
			fromData = from;
			toData = to;
			useData = true;
			return this;
		}
		
		/**
		 * Checks if the given state fits the criteria to be converted into a grassed variant
		 */
		public boolean canConvert(IBlockState state) {
			return canConvert(state.getBlock(), state.getBlock().getMetaFromState(state));
		}
		
		protected boolean canConvert(Block id, int data) {
			boolean result = fromId == Block.getIdFromBlock(id);
			if (useData && result) {
				 for (int i : fromData) {
					 if (i == data) return true;
				 }
				 return false;
			}
			return result;
		}
		
		/**
		 * Gets the grassed variant corresponding to the given state.
		 */
		public IBlockState getConverted(IBlockState state) {
			return getTo().getStateFromMeta(getToData(state.getBlock().getMetaFromState(state)));
		}
		
		/**
		 * Gets the block that this mapping converts to
		 */
		public Block getTo() {
			return Block.getBlockById(toId);
		}
		
		/**
		 * Gets the converted metadata based on the metadata value given. Assumes it will be used with the from gotten from getTo().
		 * 
		 */
		public int getToData(int data) {
			if (preserveData) return data;
			return useData ? (enforceData ? getToDataByIndex(data) : data) : 0;
		}
		
		private int getToDataByIndex(int data) {
			int index = getDataIndex(data);
			if (index < 0 || index > toData.length) {
				index = 0;
			}
			return toData[index];
		}
		
		protected int getDataIndex(int data) {
			int i;
			for (i = 0; i < toData.length; i++) {
				if (toData[i] == data) return i;
			}
			for (i = 0; i < fromData.length; i++) {
				if (fromData[i] == data) return i;
			}
			return 0;
		}
	}
}
