package com.minelittlepony.unicopia.input;

import com.minelittlepony.unicopia.Unicopia;
import net.minecraft.util.MouseHelper;

public class MouseControl extends MouseHelper {
    public void mouseXYChange() {
        super.mouseXYChange();

        if (Unicopia.proxy.getIPlayer().getGravity().getGravitationConstant() < 0) {
            deltaX = -deltaX;
            deltaY = -deltaY;
        }
    }
}
