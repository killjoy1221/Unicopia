package com.sollace.unicopia;

import java.io.File;

import com.blazeloader.util.config.Prop;
import com.blazeloader.util.config.Properties;
import com.sollace.unicopia.client.ClientSide;
import com.sollace.unicopia.server.PlayerSpeciesRegister;

public class Settings {
	private static Properties cfg;
	
	private static Prop<Race> playerSpecies;
	private static Prop<Boolean> world;
	
	public static void init(File file) {
		cfg = new Properties(new File(file, "unicopia.properties")).setWriteDefaults(false);
		
		if (Unicopia.isClient()) {
			playerSpecies = cfg.getProperty("General", "PlayerSpecies", Race.getDefault()).setDescription("EARTH / UNICORN / PEGASUS / CHANGELING");
		} else {
			String[] blackList = cfg.getProperty("ServerAdmin", "SpeciesBlackList", new String[] {Race.ALICORN.toString() }).get();
			PlayerSpeciesRegister.loadBlackList(blackList);
		}
		
		world = cfg.getProperty("General", "LegacyMode", false).setDescription("Enable/Disable additions to the world", "Blocks, Items, Entities, etc.");
	}
	
	public static boolean getLegacyMode() {
		return world.get();
	}
	
	public static Race getSpecies() {
		return playerSpecies.get();
	}
	
	public static void setSpecies(Race s, boolean save) {
		playerSpecies.set(s);
		PlayerSpeciesRegister.setPlayerSpecies(ClientSide.thePlayer(), s);
		if (save) cfg.save();
	}
	
	public static int getEntityId(String key) {
		int result = cfg.getEntityId("Entities", key).get();
		cfg.save();
		return result;
	}
	
	public static int getBlockId(String key) {
		int result = cfg.getBlockId("Blocks", key).get();
		cfg.save();
		return result;
	}
	
	public static int getItemId(String key) {
		int result = cfg.getItemId("Items", key).get();
		cfg.save();
		return result;
	}
	
	public static void freeze() {
		cfg.save();
	}
}
