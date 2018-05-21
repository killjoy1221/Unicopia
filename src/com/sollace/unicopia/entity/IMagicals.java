package com.sollace.unicopia.entity;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.IAnimals;

public interface IMagicals extends IAnimals {
	public static final Predicate<Entity> magicalSelector = entity-> entity instanceof IMagicals;
    public static final Predicate<Entity> VISIBLE_MAGICAL_SELECTOR = entity -> entity instanceof IMagicals && !((Entity)entity).isInvisible();
    public static final Predicate<Entity> nonMagicalSelector = entity -> !(entity instanceof IMagicals);
}
