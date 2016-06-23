package com.sollace.util;

import com.google.common.base.Predicate;
import com.sollace.unicopia.entity.EntityCloud;
import com.sollace.unicopia.entity.EntitySpell;
import com.sollace.unicopia.entity.IMagicals;
import com.sollace.unicopia.server.PlayerSpeciesRegister;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class Util {
	public static Predicate NOT_CLOUDS = new Predicate() {
		public boolean apply(Object o) {
			return !(o instanceof EntityCloud);
		}
	};
	public static Predicate DISGUISABLE = new Predicate() {
		public boolean apply(Object o) {
			return o == null || o instanceof EntityLivingBase && NOT_CLOUDS.apply(o);
		}
	};
	public static Predicate FLYABLE = new Predicate() {
		public boolean apply(Object o) {
			return o instanceof EntityPlayer && PlayerSpeciesRegister.getPlayerSpecies((EntityPlayer)o).canFly();
		}
	};
	public static Predicate CASTABLE = new Predicate() {
		public boolean apply(Object o) {
			return o instanceof EntityPlayer && PlayerSpeciesRegister.getPlayerSpecies((EntityPlayer)o).canCast();
		}
	};
	public static Predicate CLOUDABLE = new Predicate() {
		public boolean apply(Object o) {
			return o instanceof EntityPlayer && PlayerSpeciesRegister.getPlayerSpecies((EntityPlayer)o).canInteractWithClouds();
		}
	};
	public static Predicate NONMAGICALS = new Predicate() {
		public boolean apply(Object o) {
			return !(o instanceof IMagicals);
		}
	};
	public static Predicate SPELLS = new Predicate() {
		public boolean apply(Object o) {
			return o instanceof EntitySpell;
		}
	};
}
