package com.sollace.unicopia.effect;

import com.sollace.unicopia.entity.EntitySpell;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * 
 * Interface for a magic spell
 *
 */
public interface IMagicEffect {
	
	/**
	 * Maximum level this spell can reach or -1 for unlimited.
	 * <br>
	 * If a gem goes past this level it is more likely to explode.
	 */
	public int getMaxLevel();
	
	/**
	 * Sets this effect as dead.
	 */
	public void setDead();
	
	/**
	 * Returns true if this spell is dead, and must be cleaned up.
	 */
	public boolean getDead();
	
	/**
	 * Called every tick when attached to a player.
	 * 
	 * @param source	The entity we are currently attached to.
	 * @return true to keep alive
	 */
	public boolean update(Entity source);
	
	/**
	 * Called every tick when attached to a player. Used to apply particle effects.
	 * Is only called on the client side.
	 * 
	 * @param source	The entity we are currently attached to.
	 */
	public void render(Entity source);
	
	/**
	 * Called every tick when attached to a gem.
	 * 
	 * @param source	The entity we are attached to.
	 * @param w			The world
	 * @param x			Entity position x
	 * @param y			Entity position y
	 * @param z			Entity position z
	 * @param level		Current spell level
	 */
	public boolean updateAt(EntitySpell source, World w, double x, double y, double z, int level);
	
	/**
	 * Called every tick when attached to an entity to produce particle effects.
	 * Is only called on the client side.
	 * 
	 * @param source	The entity we are attached to.
	 * @param w			The world
	 * @param x			Entity position x
	 * @param y			Entity position y
	 * @param z			Entity position z
	 * @param level		Current spell level
	 */
	public void renderAt(EntitySpell source, World w, double x, double y, double z, int level);
	
	/**
	 * Called to save this spell to nbt to persist state on file or to transmit over the network
	 * 
	 * @param compound	Compound tag to write to.
	 */
	public void writeToNBT(NBTTagCompound compound);
	
	/**
	 * Called to load this spell's state from nbt
	 * 
	 * @param compound	Compound tag to read from.
	 */
	public void readFromNBT(NBTTagCompound compound);
	
	/**
	 * Return true to allow the gem update and move.
	 */
	public boolean allowAI();
}
