package com.sollace.unicopia.command;

import com.sollace.unicopia.server.PlayerSpeciesRegister;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandGameMode;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.WorldSettings.GameType;

public class CommandOverrideGameMode extends CommandGameMode {
	public void processCommand(ICommandSender sender, String[] params) throws CommandException {
        if (params.length <= 0) {
        	throw new WrongUsageException("commands.gamemode.usage");
        }
        GameType gametype = getGameModeFromCommand(sender, params[0]);
        EntityPlayerMP entityplayermp = params.length >= 2 ? getPlayer(sender, params[1]) : getCommandSenderAsPlayer(sender);
        updateGameMode(entityplayermp, gametype);
        entityplayermp.fallDistance = 0.0F;
        ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("gameMode." + gametype.getName(), new Object[0]);

        if (entityplayermp != sender) {
        	notifyOperators(sender, this, 1, "commands.gamemode.success.other", entityplayermp.getCommandSenderName(), chatcomponenttranslation);
        } else {
            notifyOperators(sender, this, 1, "commands.gamemode.success.self", chatcomponenttranslation);
        }
    }
	
	private void updateGameMode(EntityPlayerMP player, GameType m) {
		boolean flying = player.capabilities.isFlying;
		player.setGameType(m);
		if (flying != player.capabilities.isFlying && PlayerSpeciesRegister.getPlayerSpecies(player).canFly()) {
			player.capabilities.isFlying = true;
			player.sendPlayerAbilities();
		}
	}
}
