package com.sollace.util.entity;

import net.minecraft.entity.EntityLivingBase;

public interface ITameable {
	
	public EntityLivingBase getOwner();
	
	public boolean isSitting();
}
