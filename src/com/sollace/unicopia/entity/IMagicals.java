package com.sollace.unicopia.entity;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.IAnimals;

public interface IMagicals extends IAnimals {
	public static final Predicate magicalSelector = new Predicate() {
        public boolean apply(Object entity) {
        	return entity instanceof IMagicals;
        }
    };
    public static final Predicate VISIBLE_MAGICAL_SELECTOR = new Predicate() {
        public boolean apply(Object entity) {
        	return entity instanceof IMagicals && !((Entity)entity).isInvisible();
        }
    };
    public static final Predicate nonMagicalSelector = new Predicate() {
    	public boolean apply(Object entity) {
        	return !(entity instanceof IMagicals);
        }
    };
}
