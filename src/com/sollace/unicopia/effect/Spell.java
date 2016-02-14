package com.sollace.unicopia.effect;

import com.sollace.unicopia.entity.EntitySpell;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public abstract class Spell implements IMagicEffect {
	
	protected boolean isDead = false;
	
	public int getMaxLevel() {
		return 0;
	}
	
	public void setDead() {
		isDead = true;
	}
	
	public boolean getDead() {
		return isDead;
	}
	
	public boolean allowAI() {
		return false;
	}
	
	public boolean update(Entity source) {
		return false;
	}
	
	public void render(Entity source) {
		
	}
	
	public boolean updateAt(EntitySpell source, World w, double x, double y, double z, int level) {
		return false;
	}
	
	public void renderAt(EntitySpell source, World w, double x, double y, double z, int level) {
		
	}
	
	public void writeToNBT(NBTTagCompound compound) {
		
	}
	
	public void readFromNBT(NBTTagCompound compound) {
		
	}
}
