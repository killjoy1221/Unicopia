package com.sollace.unicopia.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;

import com.blazeloader.api.network.IMessage;
import com.blazeloader.api.network.IMessageHandler;
import com.sollace.unicopia.PlayerExtension;
import com.sollace.unicopia.Race;
import com.sollace.unicopia.server.PlayerSpeciesRegister;

import io.netty.buffer.ByteBuf;

public class UpdateSpeciesPacket implements IMessageHandler<UpdateSpeciesPacket.Message, IMessage, NetHandlerPlayServer> {

	public IMessage onMessage(Message message, NetHandlerPlayServer ctx) {
		EntityPlayer player = ctx.playerEntity;
		message.species = PlayerSpeciesRegister.setPlayerSpecies(player, message.species);
		PlayerExtension prop = PlayerExtension.get(player);
		prop.updateIsFlying(message.species);
		message.isFlying = prop.isFlying;
		message.persist = false;
		return message;
	}
	
	public static class Message implements IMessage {
		public Race species;
		public boolean isFlying;
		public boolean persist;
		
		public Message() {}
		
		public Message(Race s, boolean b, boolean p) {
			species = s;
			isFlying = b;
			persist = p;
		}
		
		public void fromBytes(ByteBuf buf) {
			int ind = buf.readInt();
			if (ind >= 0 && ind < species.values().length) {
				species = Race.values()[ind];
			} else {
				species = Race.EARTH;
			}
			isFlying = buf.readBoolean();
			persist = buf.readBoolean();
		}

		public void toBytes(ByteBuf buf) {
			buf.writeInt(species.ordinal());
			buf.writeBoolean(isFlying);
			buf.writeBoolean(persist);
		}
	}
}
