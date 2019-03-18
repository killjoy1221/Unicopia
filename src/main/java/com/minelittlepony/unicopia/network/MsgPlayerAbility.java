package com.minelittlepony.unicopia.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.minelittlepony.unicopia.player.IPlayer;
import com.minelittlepony.unicopia.player.PlayerSpeciesList;
import com.minelittlepony.unicopia.power.IData;
import com.minelittlepony.unicopia.power.IPower;
import com.minelittlepony.unicopia.power.PowersRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MsgPlayerAbility implements IMessage {

    static final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    private String powerIdentifier;
    private String abilityJson;

    public MsgPlayerAbility() {
    }

    public MsgPlayerAbility(EntityPlayer player, IPower<?> power, IData data) {
        powerIdentifier = power.getKeyName();
        abilityJson = gson.toJson(data, power.getPackageType());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer packet = new PacketBuffer(buf);
        powerIdentifier = packet.readString(30);
        abilityJson = packet.readString(Short.MAX_VALUE);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer packet = new PacketBuffer(buf);
        packet.writeString(powerIdentifier);
        packet.writeString(abilityJson);
    }

    public String getAbilityJson() {
        return abilityJson;
    }

    public String getPowerIdentifier() {
        return powerIdentifier;
    }

    public static class Handler implements IMessageHandlerSync<MsgPlayerAbility> {

        @Override
        public void onMessageSync(MsgPlayerAbility message, MessageContext ctx) {
            PowersRegistry.instance().getPowerFromName(message.getPowerIdentifier())
                    .ifPresent(power -> handleAbility(power, message, ctx));

        }

        private static <T extends IData> void handleAbility(IPower<T> power, MsgPlayerAbility message, MessageContext ctx) {
            IPlayer player = PlayerSpeciesList.instance().getPlayer(ctx.getServerHandler().player);
            if (player == null) {
                return;
            }

            T data = MsgPlayerAbility.gson.fromJson(message.getAbilityJson(), power.getPackageType());

            power.apply(player, data);
        }
    }
}
