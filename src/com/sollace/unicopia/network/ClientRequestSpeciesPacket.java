package com.sollace.unicopia.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;

import com.blazeloader.api.network.IMessage;
import com.blazeloader.api.network.IMessageHandler;
import com.sollace.unicopia.PlayerExtension;
import com.sollace.unicopia.Settings;
import com.sollace.unicopia.network.RequestSpeciesPacket.Message;

public class ClientRequestSpeciesPacket implements IMessageHandler<Message, IMessage, INetHandler> {
	public IMessage onMessage(Message message, INetHandler ctx) {
		return new UpdateSpeciesPacket.Message(Settings.getSpecies(), PlayerExtension.get(Minecraft.getMinecraft().thePlayer).isFlying, true);
	}
}