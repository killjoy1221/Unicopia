package com.sollace.unicopia.network;

import java.io.IOException;

import com.blazeloader.api.network.IMessage;
import com.blazeloader.api.network.IMessageHandler;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.BlockPos;

public class ClientUpdateTileEntity implements IMessageHandler<ClientUpdateTileEntity.Message, IMessage, INetHandler> {

	@Override
	public IMessage onMessage(Message message, INetHandler net) {
		if (message.nbt != null) {
			Minecraft.getMinecraft().theWorld.getTileEntity(message.pos).readFromNBT(message.nbt);
		}
		return null;
	}
	
	public static class Message implements IMessage {
		
		private BlockPos pos;
		private NBTTagCompound nbt; 
		
		public Message() {
			
		}
		
		public Message(BlockPos pos, NBTTagCompound tag) {
			this.pos = pos;
			nbt = tag;
		}
		
		@Override
		public void fromBytes(ByteBuf buf) {
			PacketBuffer pbuf = new PacketBuffer(buf);
			pos = pbuf.readBlockPos();
			try {
				nbt = pbuf.readNBTTagCompoundFromBuffer();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void toBytes(ByteBuf buf) {
			PacketBuffer pbuf = new PacketBuffer(buf);
			pbuf.writeBlockPos(pos);
			pbuf.writeNBTTagCompoundToBuffer(nbt);
		}
		
	}
}
