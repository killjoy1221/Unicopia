package com.sollace.unicopia.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;

import com.blazeloader.api.network.IMessage;
import com.blazeloader.api.network.IMessageHandler;
import com.sollace.unicopia.PlayerExtension;
import com.sollace.unicopia.Settings;
import com.sollace.unicopia.network.UpdateSpeciesPacket.Message;

public class ClientUpdateSpeciesPacket implements IMessageHandler<UpdateSpeciesPacket.Message, IMessage, INetHandler> {
		public IMessage onMessage(Message message, INetHandler ctx) {
			EntityPlayer player = (EntityPlayer)(Minecraft.getMinecraft().player);
			Settings.setSpecies(message.species, message.persist);
			PlayerExtension.get(player).updateIsFlying(message.species, message.isFlying);
			player.sendPlayerAbilities();
			return null;
		}
}