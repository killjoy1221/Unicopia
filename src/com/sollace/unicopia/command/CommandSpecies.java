package com.sollace.unicopia.command;

import java.util.ArrayList;
import java.util.List;

import com.sollace.unicopia.UnicopiaPacketChannel;
import com.sollace.unicopia.PlayerExtension;
import com.sollace.unicopia.Race;
import com.sollace.unicopia.network.UpdateSpeciesPacket;
import com.sollace.unicopia.server.PlayerSpeciesRegister;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

public class CommandSpecies extends CommandBase {

	public String getCommandName() {
		return "race";
	}
	
    public int getRequiredPermissionLevel() {
        return 0;
    }
    
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return sender.canCommandSenderUseCommand(getRequiredPermissionLevel(), "help");
    }
    
    private String getRacesString() {
		String values = "";
		for (Race i : Race.values()) {
			if (PlayerSpeciesRegister.getSpeciesPermitted(i)) {
				if (values != "") values += ", ";
				values += i.toString();
			}
		}
		return values;
    }
    
	public String getCommandUsage(ICommandSender sender) {
		return "commands.race.usage";
	}

	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 1) {
			throw new WrongUsageException(getCommandUsage(sender));
		}
		
		EntityPlayerMP player;
		int playerIndex = 1;
		
		if (args[0].contentEquals("set")) playerIndex++;
		
		if (args.length > playerIndex) {
			player = getPlayer(sender, args[playerIndex]);
		} else {
			player = getCommandSenderAsPlayer(sender);
		}
		
		if (args[0].contentEquals("set")) {
			if (args.length >= 2) {
				Race species = null;
				try {
					species = Race.getSpeciesFromName(args[1].toUpperCase());
					if (species == null) {
						species = Race.getSpeciesFromId(args[1]);
					}
				} catch (Throwable e) { }
				if (species == null) {
					player.addChatMessage(new ChatComponentTranslation("commands.race.fail", args[1].toUpperCase()));
				} else {
					if (PlayerSpeciesRegister.getSpeciesPermitted(species)) {
						PlayerSpeciesRegister.setPlayerSpecies(player, species);
						PlayerExtension.get(player).updateIsFlying(species, player.capabilities.isFlying);
						UnicopiaPacketChannel.instance().sendToClient(new UpdateSpeciesPacket.Message(species, player.capabilities.isFlying, true), player);
						
						ChatComponentTranslation formattedName = new ChatComponentTranslation(species.name().toLowerCase());
						
						if (player != sender) {
							notifyOperators(sender, this, 1, "commands.race.success.other", player.getCommandSenderName(), formattedName);
			            } else {
			            	player.addChatMessage(new ChatComponentTranslation("commands.race.success.self"));
			            	notifyOperators(sender, this, 1, "commands.race.success.otherself", player.getCommandSenderName(), formattedName);
			            }
					} else {
						player.addChatMessage(new ChatComponentTranslation("commands.race.permission"));
					}
				}
			}
		} else if (args[0].contentEquals("get")) {
			Race spec = PlayerSpeciesRegister.getPlayerSpecies(player);
			String name = "commands.race.tell.";
			name += player == sender ? "self" : "other";
			ChatComponentTranslation message;
			if (spec.startsWithVowel()) name += ".alt";
			message = new ChatComponentTranslation(name);
			ChatComponentText race = new ChatComponentText(spec.displayName());
			race.getChatStyle().setColor(EnumChatFormatting.GOLD);
			message.appendSibling(race);
			player.addChatMessage(message);
		} else if (args[0].contentEquals("list")) {
			player.addChatMessage(new ChatComponentTranslation("commands.race.list"));
			ChatComponentText message = new ChatComponentText(" " + getRacesString());
			message.getChatStyle().setColor(EnumChatFormatting.GOLD);
			player.addChatMessage(message);
		}
	}
	
    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] args, BlockPos pos)
    {
    	if (args.length == 1) {
    		return getListOfStringsMatchingLastWord(args, new String[] { "get", "set", "list" });
    	} else if (args.length == 2 && args[0].contentEquals("set")) {
    		ArrayList<String> names = new ArrayList<String>();
    		for (Race i : Race.values()) {
    			if (PlayerSpeciesRegister.getSpeciesPermitted(i)) {
    				names.add(i.toString());
    			}
    		}
			return getListOfStringsMatchingLastWord(args, names.toArray(new String[names.size()]));
    	} else if ((args.length == 3 && args[0].contentEquals("set")) || (args[0].contentEquals("get") && args.length == 2)) {
    		return getListOfStringsMatchingLastWord(args, getListOfPlayerUsernames());
    	}
    	
        return null;
    }

    protected String[] getListOfPlayerUsernames() {
        return MinecraftServer.getServer().getAllUsernames();
    }

    public boolean isUsernameIndex(String[] args, int index) {
    	if (args[0].contentEquals("get")) {
			return index == 1;
    	} else if (args[0].contentEquals("set")) {
    		return index == 2;
    	}
        return false;
    }
}
