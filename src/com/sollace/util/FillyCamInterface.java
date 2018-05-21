package com.sollace.util;

import com.blazeloader.api.client.ApiClient;
import com.blazeloader.util.reflect.Reflect;
import com.blazeloader.util.reflect.StaticVar;
import com.blazeloader.util.version.Versions;

import net.minecraft.entity.Entity;

public final class FillyCamInterface {
	private static boolean fillyCam = false;
	private static StaticVar<?, Float> mCameraHeight;
	
	static {
		if (Versions.isClient()) {
			mCameraHeight = Reflect.lookupStaticField("com.hepolite.fillycam.LiteModFillyCam.mCameraHeight");
			fillyCam = mCameraHeight.valid();
		}
	}
	
	public static final boolean playerIsFillyCamPlayer(Entity player) {
		if (player != null && fillyCam) {
			return player.getName().contentEquals(ApiClient.getPlayer().getName());
		}
		return false;
	}
	
	public static final float getCameraHeight() {
		if (fillyCam && mCameraHeight.valid()) {
			return mCameraHeight.get(1.62f);
		}
		return 1.62f;
	}
	
	public static final float getCameraHeightDiff() {
		return getCameraHeight() - 1.62f;
	}
}
