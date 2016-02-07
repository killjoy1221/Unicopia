package com.sollace.unicopia.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.NetHandlerPlayServer;

import com.blazeloader.api.network.IMessage;
import com.blazeloader.api.network.IMessageHandler;
import com.google.common.base.Charsets;
import com.sollace.unicopia.power.IData;
import com.sollace.unicopia.power.Power;

public class PPacket implements IMessageHandler<PPacket.Message, IMessage, NetHandlerPlayServer> {
	
	public IMessage onMessage(PPacket.Message message, NetHandlerPlayServer handler) {
		if (message.payload != null) {
			message.power.apply(handler.playerEntity, message.payload);
		}
		return null;
	}
	
	public static class Message implements IMessage {
		private IData payload;
		
		private Power power;
		
		public Message() {
			
		}
		
		public Message(Power power, IData data) {
			this.power = power;
			this.payload = data;
		}
		
		@Override
		public void fromBytes(ByteBuf buf) {
			try {
				int len = buf.readInt();
				byte[] bytes = new byte[len];
				buf.readBytes(bytes);
				String name = new String(bytes, Charsets.UTF_8);
				power = Power.powerFromName(name);
				if (power != null) {
					payload = power.fromBytes(buf);
				}
			} catch (Exception e) {
				e.printStackTrace();
				power = null;
				payload = null;
			}
		}
		
		@Override
		public void toBytes(ByteBuf buf) {
			byte[] bytes = power.getKeyName().getBytes(Charsets.UTF_8);
			buf.writeInt(bytes.length);
			buf.writeBytes(bytes);
			payload.toBytes(buf);
		}
	}
}
