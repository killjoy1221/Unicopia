package com.sollace.unicopia.command;

import java.util.List;

import com.sollace.unicopia.entity.EntityCloud;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandDecloud extends CommandBase {
	@Override
	public String getName() {
		return "decloud";
	}
	
	@Override
	public String getUsage(ICommandSender sender) {
		return "commands.decloud.usage";
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
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
    	sender.sendMessage(new TextComponentTranslation("commands.decloud.success", totalRemovals));
    	notifyCommandListener(sender, this, 1, "commands.decloud.success", totalRemovals);
	}
}