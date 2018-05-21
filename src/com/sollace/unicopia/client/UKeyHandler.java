package com.sollace.unicopia.client;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import com.blazeloader.api.client.ApiClient;
import com.mumfrey.liteloader.core.LiteLoader;
import com.sollace.unicopia.PlayerExtension;
import com.sollace.unicopia.Settings;
import com.sollace.unicopia.power.Power;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;

public class UKeyHandler {
	private static ArrayList<KeyBinding> bindings = new ArrayList<KeyBinding>();
	private static ArrayList<KeyBinding> removed = new ArrayList<KeyBinding>();
		
	private static ArrayList<KeyBinding> pressed = new ArrayList<KeyBinding>();
	
	public static void RegisterKeyBinding(Power<?> p) {
		KeyBinding b = new KeyBinding(p.getKeyName(), p.getDefaultKeyIndex(), p.getKeyCategory());
		LiteLoader.getInput().registerKeyBinding(b);
		bindings.add(b);
	}
	
	public void onKeyInput() {
		if (ApiClient.getClient().currentScreen == null) {
			EntityPlayer player = ApiClient.getPlayer();
			PlayerExtension prop = PlayerExtension.get(player);
			for (KeyBinding i : bindings) {
				if (Keyboard.isKeyDown(i.getKeyCode())) {
					if (!pressed.contains(i)) pressed.add(i);
					if (!Power.keyHasRegisteredPower(i.getKeyCodeDefault())) {
						removed.add(i);
						System.out.println("Error: Keybinding(" + i.getKeyDescription() + ") does not have a registered pony power. Keybinding will be removed from event.");
					} else {
						Power<?> p = Power.getCapablePowerFromKey(i.getKeyCodeDefault(), Settings.getSpecies());
						if (p != null) {
							if (p.canUse(Settings.getSpecies())) {
								prop.tryUseAbility(p);
							}
						}
					}
				} else {
					if (pressed.contains(i)) {
						pressed.remove(i);
						prop.tryUseAbility(null);
					}
				}
			}
			
			for (KeyBinding i : removed) {
				bindings.remove(i);
				if (pressed.contains(i)) pressed.remove(i);
			}
			removed.clear();
		}
	}
}
