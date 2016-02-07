package com.sollace.unicopia.command;

import java.util.List;

import com.sollace.unicopia.entity.EntityCloud;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChatComponentTranslation;

public class CommandDecloud extends CommandBase {
	
	public String getCommandName() {
		return "decloud";
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "commands.decloud.usage";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		boolean removeAll = false;
		if (args.length >= 1) removeAll = args[0].contentEquals("all");
		
		int totalRemovals = 0;
		for (Entity i : (List<Entity>)sender.getEntityWorld().loadedEntityList) {
			if (EntityCloud.class.isAssignableFrom(i.getClass())) {
				if (removeAll || (!((EntityCloud)i).getStationary() && !((EntityCloud)i).getOpaque())) {
					i.setDead();
					totalRemovals++;
				}
			}
		}
    	sender.addChatMessage(new ChatComponentTranslation("commands.decloud.success", totalRemovals));
    	notifyOperators(sender, this, 1, "commands.decloud.success", totalRemovals);
	}

}