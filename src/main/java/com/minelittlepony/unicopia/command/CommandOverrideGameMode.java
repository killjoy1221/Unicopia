package com.minelittlepony.unicopia.command;

import com.minelittlepony.unicopia.player.IPlayer;
import com.minelittlepony.unicopia.player.PlayerSpeciesList;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandGameMode;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameType;

class CommandOverrideGameMode extends CommandGameMode {

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {

        if (params.length <= 0) {
            throw new WrongUsageException("commands.gamemode.usage");
        }

        GameType gametype = getGameModeFromCommand(sender, params[0]);

        EntityPlayer player = params.length >= 2 ? getPlayer(server, sender, params[1]) : getCommandSenderAsPlayer(sender);

        updateGameMode(player, gametype);

        ITextComponent mode = new TextComponentTranslation("gameMode." + gametype.getName(), new Object[0]);

        if (sender.getEntityWorld().getGameRules().getBoolean("sendCommandFeedback")) {
            player.sendMessage(new TextComponentTranslation("gameMode.changed", mode));
        }

        if (player == sender) {
            notifyCommandListener(sender, this, 1, "commands.gamemode.success.self", mode);
        } else {
            notifyCommandListener(sender, this, 1, "commands.gamemode.success.other", player.getName(), mode);
        }
    }

    protected void updateGameMode(EntityPlayer player, GameType m) {
        player.setGameType(m);

        IPlayer iplayer = PlayerSpeciesList.instance().getPlayer(player);

        iplayer.setPlayerSpecies(iplayer.getPlayerSpecies());
    }
}
