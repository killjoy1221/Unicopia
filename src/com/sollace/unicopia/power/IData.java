package com.sollace.unicopia.power;

import io.netty.buffer.ByteBuf;

/**
 * Represents a piece of data to be sent between client and server for a Power.
 */
public interface IData {
	public abstract void toBytes(ByteBuf buf);
	public abstract void fromBytes(ByteBuf buf);
}

