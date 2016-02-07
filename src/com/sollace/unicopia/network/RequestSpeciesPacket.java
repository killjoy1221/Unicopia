package com.sollace.unicopia.network;

import net.minecraft.network.NetHandlerPlayServer;

import com.blazeloader.api.network.IMessage;
import com.blazeloader.api.network.IMessageHandler;

import io.netty.buffer.ByteBuf;

public class RequestSpeciesPacket implements IMessageHandler<RequestSpeciesPacket.Message, IMessage, NetHandlerPlayServer> {
	public IMessage onMessage(Message message, NetHandlerPlayServer handler) {
		return null;
	}
	
	public static class Message implements IMessage {
		public Message() {}
		public void fromBytes(ByteBuf buf) {}
		public void toBytes(ByteBuf buf) {}
	}
}
