package com.sollace.util;

import com.blazeloader.api.client.ApiClient;
import com.blazeloader.util.reflect.Reflect;
import com.blazeloader.util.reflect.Var;
import com.blazeloader.util.version.Versions;

import net.minecraft.entity.Entity;

public final class FillyCamInterface {
	private static boolean fillyCam = false;
	private static Var<?, Float> mCameraHeight;
	
	static {
		if (Versions.isClient()) {
			mCameraHeight = Reflect.lookupStaticField("com.hepolite.fillycam.LiteModFillyCam.mCameraHeight");
			fillyCam = mCameraHeight.valid();
		}
	}
	
	public static final boolean playerIsFillyCamPlayer(Entity player) {
		if (player != null && fillyCam) {
			return player.getCommandSenderName().contentEquals(ApiClient.getPlayer().getCommandSenderName());
		}
		return false;
	}
	
	public static final float getCameraHeight() {
		if (fillyCam && mCameraHeight.valid()) {
			return mCameraHeight.get(null, 1.62f);
		}
		return 1.62f;
	}
	
	public static final float getCameraHeightDiff() {
		return getCameraHeight() - 1.62f;
	}
}
