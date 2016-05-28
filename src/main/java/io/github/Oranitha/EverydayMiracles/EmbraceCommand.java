package io.github.Oranitha.EverydayMiracles;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EmbraceCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String s, String[] args){
		if (sender instanceof Player) {
	           Player player = (Player) sender;
	        	   DataHandler dh = new DataHandler();
	        	   StringBuilder out = new StringBuilder("The following deities are listening: ");
		           ArrayList<String> responses = dh.getDeities();
		           for (String deity:responses){
		        	   out.append(deity+" ");
		           }
		           player.sendMessage(ChatColor.BLUE + out.toString());
	        } else {
	           sender.sendMessage("You must be a player!");
	           return false;
	        }
		return false;
	}
}
