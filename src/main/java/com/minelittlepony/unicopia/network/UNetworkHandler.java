package com.minelittlepony.unicopia.network;

import com.minelittlepony.unicopia.Unicopia;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class UNetworkHandler {

    private static int packetId = 0;
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Unicopia.MODID);

    static {
        INSTANCE.registerMessage(MsgPlayerCapabilities.Handler.class, MsgPlayerCapabilities.class, packetId++, Side.CLIENT);
        INSTANCE.registerMessage(MsgRequestCapabilities.Handler.class, MsgRequestCapabilities.class, packetId++, Side.SERVER);
        INSTANCE.registerMessage(MsgPlayerAbility.Handler.class, MsgPlayerAbility.class, packetId++, Side.SERVER);
    }

    public static void init() {
        // left empty on purpose
    }
}
