package com.sollace.unicopia.effect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * Interface for right-blick actions.
 *
 */
public interface IUseAction {
	
	/**
	 * Triggered when the player right clicks a block
	 * 
	 * @param stack		The current itemstack
	 * @param player	The player
	 * @param world		The player's world
	 * @param pos		The location clicked
	 * @param side		The side of the block clicked
	 * @param hitX		X offset inside the block
	 * @param hitY		Y offset inside the block
	 * @param hitZ		Z offset inside the block
	 * 
	 * @return	ActionResult for the type of action to perform
	 */
	public ActionResult onUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ);
	
	/**
	 * Triggered when the player right clicks
	 * 
	 * @param stack		The current itemstack 
	 * @param player	The player
	 * @param world		The player's world
	 * @param hitEntity	The entity in focus, if any
	 * 
	 * @return	ActionResult for the type of action to perform
	 */
	public ActionResult onUse(ItemStack stack, EntityPlayer player, World world, Entity hitEntity);
}
