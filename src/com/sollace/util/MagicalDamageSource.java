package com.sollace.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;

public class MagicalDamageSource extends EntityDamageSource {
	
	public static DamageSource create(String type) {
		return new MagicalDamageSource(type);
	}
	
	public static DamageSource causePlayerDamage(String type, EntityPlayer player) {
		return new MagicalDamageSource(type, player);
	}
	
	public static DamageSource causeMobDamage(String type, EntityLivingBase source) {
		return new MagicalDamageSource(type, source);
	}
	
	protected MagicalDamageSource(String type) {
		this(type, null);
	}
	
	protected MagicalDamageSource(String type, Entity source) {
		super(type, source);
		setMagicDamage();
    }
	
    public IChatComponent getDeathMessage(EntityLivingBase target) {
        EntityLivingBase attacker = damageSourceEntity instanceof EntityLivingBase ? (EntityLivingBase)damageSourceEntity : target.func_94060_bK();
        String basic = "death.attack." + this.damageType;
        
        if (attacker != null) {
        	String withAttecker = basic + ".player";
	        ItemStack held = attacker instanceof EntityLivingBase ? attacker.getHeldItem() : null;
	        
	        String withItem = withAttecker + ".item";
	        if (held != null && held.hasDisplayName() && StatCollector.canTranslate(withItem)) {
	        	return new ChatComponentTranslation(withItem, target.getDisplayName(), attacker.getDisplayName(), held.getChatComponent());
	        }
	        
	        if (StatCollector.canTranslate(withAttecker)) {
	        	return new ChatComponentTranslation(withAttecker, target.getDisplayName(), attacker.getDisplayName());
	        }
	    }
        
    	return new ChatComponentTranslation(basic, target.getDisplayName());
    }
}
