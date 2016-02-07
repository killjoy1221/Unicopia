package com.sollace.unicopia.effect;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.sollace.unicopia.Unicopia;
import com.sollace.unicopia.enchanting.SpellRecipe;

public final class SpellList {
	private static int nextId = 1;
	
	private static final Map<String, Entry> nameLookup = new HashMap<String, Entry>();
	private static final Map<Integer, Entry> idLookup = new HashMap<Integer, Entry>();
	private static final Map<Class<? extends IMagicEffect>, Entry> classLookup = new HashMap<Class<? extends IMagicEffect>, Entry>();
	
	public static Iterator<Integer> getIDIterator() {
		return idLookup.keySet().iterator();
	}
	
	/**
	 * Registers a new spell.
	 * 
	 * @param name			A string identifier for the spell.
	 * @param clazz			The class to use
	 * @param gemColour		Integer colour to be used on gems
	 */
	public static void registerSpell(String name, Class<? extends IMagicEffect> clazz, int gemColour) {
		Entry entry = new Entry(name, clazz, gemColour);
		nameLookup.put(name, entry);
		idLookup.put(entry.id, entry);
		classLookup.put(clazz, entry);
	}
	
	/**
	 * Registers a gem recipe.
	 * 
	 * @param name		Name of the spell to craft
	 * @param inputs	Items/Block/ItemStacks as input (maximum 4)
	 */
	public static void addRecipe(String name, Object... inputs) {
		Unicopia.getCraftingManager().addRecipe(new SpellRecipe(name, inputs));
	}
	
	/**
	 * Returns true if there is a spell registered with the given name
	 */
	public static boolean isEffect(String name) {
		return nameLookup.containsKey(name);
	}
	
	/**
	 * Returns true if there is a spell registered with the given id/metadata value
	 */
	public static boolean isEffect(int metadata) {
		return idLookup.containsKey(metadata);
	}
	
	/**
	 * Returns true if the spell registered with the given id/metadata implements IDispenceable
	 */
	public static boolean hasDispenceBehaviour(int metadata) {
		return idLookup.containsKey(metadata) && idLookup.get(metadata).canDispence;
	}
	
	/**
	 * Gets a spell instance for the given id/metadata or null.
	 */
	public static IMagicEffect forId(int id) {
		if (isEffect(id)) {
			try {
				Class c = idLookup.get(id).clazz;
				if (c != null) {
					return (IMagicEffect)c.newInstance();
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * Gets a spell instance for the given name, or null.
	 */
	public static IMagicEffect forName(String name) {
		if (isEffect(name)) {
			return create(nameLookup.get(name).clazz);
		}
		return null;
	}
	
	private static IMagicEffect create(Class<? extends IMagicEffect> clazz) {
		try {
			return (IMagicEffect)clazz.newInstance();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Gets the name for the given spell.
	 */
	public static String getName(IMagicEffect obj) {
		return obj == null ? "" : classLookup.get(obj.getClass()).name;
	}
	
	/**
	 * Gets a name for the spell associated with the given id/metadata or "none".
	 */
	public static String getName(int metadata) {
		if (idLookup.containsKey(metadata)) {
			return idLookup.get(metadata).name;
		}
		return "none";
	}
	
	/**
	 * Gets the id/metadata for the given spell name, or 0
	 */
	public static int getId(String name) {
		if (name == null) return 0;
		return !nameLookup.containsKey(name) ? 0 : nameLookup.get(name).id;
	}
	
	/**
	 * Gets the id/metadat for the given spell, or 0.
	 */
	public static int getId(IMagicEffect obj) {
		if (obj == null) return 0;
		Class clazz = obj.getClass();
		return !classLookup.containsKey(clazz) ? 0 : classLookup.get(clazz).id;
	}
	
	/**
	 * Gets the colour of gem for the given spell, or white.
	 */
	public static int getGemColour(IMagicEffect obj) {
		if (obj != null && classLookup.containsKey(obj.getClass())) {
			return classLookup.get(obj.getClass()).gemColour;
		}
		return 16777215;
	}
	
	/**
	 * Gets the colour of gem for the given id/metadat, or white.
	 */
	public static int getGemColour(int metadata) {
		if (idLookup.containsKey(metadata)) {
			return idLookup.get(metadata).gemColour;
		}
		return 16777215;
	}
	
	private static final class Entry {
		final Class<? extends IMagicEffect> clazz;
		final String name;
		final int id;
		final int gemColour;
		final boolean canDispence;
		
		public Entry(String name, Class<? extends IMagicEffect> clazz, int gemColour) {
			this.clazz = clazz;
			this.name = name;
			this.gemColour = gemColour; 
			this.id = nextId++;
			this.canDispence = IDispenceable.class.isAssignableFrom(clazz);
		}
	}
}
