package com.minelittlepony.unicopia.extern;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.pony.data.IPony;
import com.minelittlepony.pony.data.PonyRace;
import com.minelittlepony.unicopia.Race;

import net.minecraft.client.Minecraft;

public final class MineLP {
    private static boolean checkComplete;
    private static boolean modIsActive;

    /**
     * Returns true if mine little pony is present. That's all we need.
     */
    static boolean modIsActive() {
        if (!checkComplete) {
            try {
                MineLittlePony.getInstance();

                // always true, but this will throw if we don't have what we need.
                modIsActive = PonyRace.HUMAN.isHuman();
            } catch (Exception e) {
                modIsActive = false;
            }
        }
        return modIsActive;
    }

    public static Race getPlayerPonyRace() {

        if (!modIsActive()) {
            return Race.HUMAN;
        }

        switch (IPony.forPlayer(Minecraft.getMinecraft().player).getRace(false)) {
            case ALICORN:
                return Race.ALICORN;
            case CHANGELING:
            case REFORMED_CHANGELING:
                return Race.CHANGELING;
            case ZEBRA:
            case EARTH:
                return Race.EARTH;
            case GRIFFIN:
            case HIPPOGRIFF:
            case PEGASUS:
            case BATPONY:
                return Race.PEGASUS;
            case SEAPONY:
            case UNICORN:
                return Race.UNICORN;
            default:
                return Race.EARTH;

        }
    }
}