package com.sollace.unicopia;

import com.blazeloader.api.network.PacketChannel;
import com.blazeloader.api.network.Side;
import com.sollace.unicopia.network.ClientRequestSpeciesPacket;
import com.sollace.unicopia.network.ClientUpdateSpeciesPacket;
import com.sollace.unicopia.network.ClientUpdateTileEntity;
import com.sollace.unicopia.network.PPacket;
import com.sollace.unicopia.network.RequestSpeciesPacket;
import com.sollace.unicopia.network.UpdateSpeciesPacket;

public class UnicopiaPacketChannel extends PacketChannel {
	
	private static UnicopiaPacketChannel instance;
	
	public static UnicopiaPacketChannel instance() {
		return instance;
	}
	
	public UnicopiaPacketChannel(String identifier) {
		super(identifier);
		instance = this;
	}
	
	public void init() {
		registerMessageHandler(Side.SERVER, new PPacket(), PPacket.Message.class, 0);
		registerMessageHandler(Side.BOTH, new UpdateSpeciesPacket(), UpdateSpeciesPacket.Message.class, 1);
		registerMessageHandler(Side.CLIENT, new ClientRequestSpeciesPacket(), RequestSpeciesPacket.Message.class, 2);
		registerMessageHandler(Side.CLIENT, new ClientUpdateSpeciesPacket(), UpdateSpeciesPacket.Message.class, 3);
		registerMessageHandler(Side.SERVER, new RequestSpeciesPacket(), RequestSpeciesPacket.Message.class, 2);
		registerMessageHandler(Side.SERVER, new UpdateSpeciesPacket(), UpdateSpeciesPacket.Message.class, 3);
		registerMessageHandler(Side.CLIENT, new ClientUpdateTileEntity(), ClientUpdateTileEntity.Message.class, 4);
	}
}
