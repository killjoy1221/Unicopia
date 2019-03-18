package com.minelittlepony.unicopia.network;

import com.minelittlepony.unicopia.Race;
import com.minelittlepony.unicopia.player.IPlayer;
import com.minelittlepony.unicopia.player.PlayerSpeciesList;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.IOException;

public class MsgPlayerCapabilities implements IMessage {

    private Race race;
    private NBTTagCompound tag;

    public MsgPlayerCapabilities() {

    }

    public MsgPlayerCapabilities(Race race, NBTTagCompound tag) {
        this.race = race;
        this.tag = tag;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        try {
            PacketBuffer packet = new PacketBuffer(buf);

            race = packet.readEnumValue(Race.class);
            tag = packet.readCompoundTag();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer packet = new PacketBuffer(buf);

        packet.writeEnumValue(race);
        packet.writeCompoundTag(tag);
    }

    public Race getRace() {
        return race;
    }

    public NBTTagCompound getTag() {
        return tag;
    }

    public static class Handler implements IMessageHandlerSync<MsgPlayerCapabilities> {

        @Override
        public void onMessageSync(MsgPlayerCapabilities message, MessageContext ctx) {

            EntityPlayer p = FMLClientHandler.instance().getClient().player;
            IPlayer player = PlayerSpeciesList.instance().getPlayer(p);

            if (message.getTag().isEmpty()) {
                player.setPlayerSpecies(message.getRace());
            } else {
                player.readFromNBT(message.getTag());
            }
        }
    }
}
