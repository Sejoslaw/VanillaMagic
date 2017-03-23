package seia.vanillamagic.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import seia.vanillamagic.command.commands.VMCommandGetAllAchievements;
import seia.vanillamagic.command.commands.VMCommandTeleport;

public class VMCommand extends CommandBase
{
	public String getName()
	{
		return "vm";
	}
	
	public String getUsage(ICommandSender sender) 
	{
		return "command.vm.usage";
	}
	
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) 
			throws CommandException 
	{
		if(args.length >= 1)
		{
			if(args[0].equals(VMCommandGetAllAchievements.COMMAND_NAME))
			{
				VMCommandGetAllAchievements.execute(server, sender, args);
			}
			else if(args[0].equals(VMCommandTeleport.COMMAND_NAME))
			{
				VMCommandTeleport.execute(server, sender, args);
			}
		}
	}
}