package com.sollace.util;

import com.google.common.base.Predicate;
import com.sollace.unicopia.entity.EntityCloud;
import com.sollace.unicopia.entity.EntitySpell;
import com.sollace.unicopia.entity.IMagicals;
import com.sollace.unicopia.server.PlayerSpeciesRegister;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class Util {
	public static Predicate<Entity> NOT_CLOUDS = new Predicate<Entity>() {
		public boolean apply(Entity o) {
			return !(o instanceof EntityCloud);
		}
	};
	public static Predicate<Entity> DISGUISABLE = new Predicate<Entity>() {
		public boolean apply(Entity o) {
			return o == null || o instanceof EntityLivingBase && NOT_CLOUDS.apply(o);
		}
	};
	public static Predicate<Entity> FLYABLE = new Predicate<Entity>() {
		public boolean apply(Entity o) {
			return o instanceof EntityPlayer && PlayerSpeciesRegister.getPlayerSpecies((EntityPlayer)o).canFly();
		}
	};
	public static Predicate<Entity> CASTABLE = new Predicate<Entity>() {
		public boolean apply(Entity o) {
			return o instanceof EntityPlayer && PlayerSpeciesRegister.getPlayerSpecies((EntityPlayer)o).canCast();
		}
	};
	public static Predicate<Entity> CLOUDABLE = new Predicate<Entity>() {
		public boolean apply(Entity o) {
			return o instanceof EntityPlayer && PlayerSpeciesRegister.getPlayerSpecies((EntityPlayer)o).canInteractWithClouds();
		}
	};
	public static Predicate<Entity> NONMAGICALS = new Predicate<Entity>() {
		public boolean apply(Entity o) {
			return !(o instanceof IMagicals);
		}
	};
	public static Predicate<Entity> SPELLS = new Predicate<Entity>() {
		public boolean apply(Entity o) {
			return o instanceof EntitySpell;
		}
	};
}
