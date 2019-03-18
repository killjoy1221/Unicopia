package com.minelittlepony.unicopia.network;

import com.minelittlepony.unicopia.Race;
import com.minelittlepony.unicopia.Unicopia;
import com.minelittlepony.unicopia.player.IPlayer;
import com.minelittlepony.unicopia.player.PlayerSpeciesList;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MsgRequestCapabilities implements IMessage {

    private Race race;

    public MsgRequestCapabilities() {
    }

    public MsgRequestCapabilities(Race race) {
        this.race = race;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer packet = new PacketBuffer(buf);
        race = packet.readEnumValue(Race.class);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer packet = new PacketBuffer(buf);
        packet.writeEnumValue(race);
    }

    public Race getRace() {
        return race;
    }

    public static class Handler implements IMessageHandlerSync<MsgRequestCapabilities> {

        @Override
        public void onMessageSync(MsgRequestCapabilities message, MessageContext ctx) {
            Unicopia.log.warn("[Unicopia] [SERVER] [MsgRequestCapabilities] Sending capabilities to player %s\n", ctx.getServerHandler().player.getUniqueID());
            IPlayer player = PlayerSpeciesList.instance().getPlayer(ctx.getServerHandler().player);

            if (player.getPlayerSpecies().isDefault()) {
                player.setPlayerSpecies(message.getRace());
            }
            IMessage msg = new MsgPlayerCapabilities(player.getPlayerSpecies(), player.toNBT());
            UNetworkHandler.INSTANCE.sendTo(msg, ctx.getServerHandler().player);

        }
    }
}
