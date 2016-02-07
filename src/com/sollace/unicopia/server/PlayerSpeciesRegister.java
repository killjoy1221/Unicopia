package com.sollace.unicopia.server;

import java.util.ArrayList;
import java.util.HashMap;

import com.sollace.unicopia.Race;

import net.minecraft.entity.player.EntityPlayer;

public class PlayerSpeciesRegister {
	private static ArrayList<Race> blackList;
	private static final HashMap<String, Race> playerSpeciesMap = new HashMap<String, Race>();
	
	public static Race setPlayerSpecies(EntityPlayer p, Race s) {
		if (s != null && p != null) {
			if (!getSpeciesPermitted(s)) {
				s = Race.getDefault();
			}
			playerSpeciesMap.put(p.getCommandSenderName(), s);
		}
		return getPlayerSpecies(p);
	}
	
	public static Race getPlayerSpecies(EntityPlayer p) {
		if (p != null) {
			String key = p.getCommandSenderName();
			if (playerSpeciesMap.containsKey(key)) {
				return playerSpeciesMap.get(key);
			}
		}
		
		return Race.getDefault();
	}
	
	public static boolean getSpeciesPermitted(Race s) {
		return blackList == null || !blackList.contains(s);
	}
	
	public static String[] loadBlackList(String[] entries) {
		blackList = new ArrayList<Race>();
		ArrayList<String> result = new ArrayList<String>();
		for (int i = 0; i < entries.length; i++) {
			Race s = Race.value(entries[i]);
			if (s != Race.getDefault()) {
				blackList.add(s);
				result.add(s.toString());
			}
		}
		return result.toArray(new String[result.size()]);
	}
}
