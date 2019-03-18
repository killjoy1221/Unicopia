package com.minelittlepony.unicopia.network;

import net.minecraft.network.INetHandler;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public interface IMessageHandlerSync<REQ extends IMessage> extends IMessageHandler<REQ, IMessage> {

    void onMessageSync(REQ msg, MessageContext ctx);

    static IThreadListener getThread(INetHandler netHandler) {
        return FMLCommonHandler.instance().getWorldThread(netHandler);
    }

    @Override
    default IMessage onMessage(REQ msg, MessageContext ctx) {
        getThread(ctx.netHandler).addScheduledTask(() -> onMessageSync(msg, ctx));
        return null;
    }
}
