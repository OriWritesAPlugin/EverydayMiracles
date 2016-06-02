package io.github.Oranitha.EverydayMiracles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class Executor{
	
	
	public static boolean follow(EverydayMiracles plugin, CommandSender sender, String[] args){
    	   //TODO: datahandler as arg
		   //Check if not player
    	   if (!(sender instanceof Player)) {sender.sendMessage("The console obeys no one!"); return true;}
           
    	   Player player = (Player) sender;
    	   DataHandler dh = new DataHandler();
    	   String status = dh.getPlayerDeity(sender);
    	   
    	   //Check if deity assigned
    	   if(status != null){player.sendMessage("You are already a follower of "+status+"!"); return true;}
    	   
    	   ArrayList<String> responses = dh.getDeities();
    	   if(args.length>1){
    		   String maybeDeity = args[1];
    		   boolean realDeity = false;
    		   for(String deity:responses){
				   if(args[1].equals(deity)){realDeity=true;}
			   }
    		   if(realDeity){
    			   plugin.getPlayerData().set(sender.getName()+".deity", maybeDeity);
    			   plugin.savePlayerData();
				   player.sendMessage("You have been recognized as a follower of "+maybeDeity+"!");
				   plugin.setNickname(player);
				   return true;
    		   }
    	    }
    	   StringBuilder out = new StringBuilder("Following is currently a permanent action"
        	   		+ "...choose wisely! Your choices are:  ");
    	   for (String deity:responses){out.append(deity+", ");};
    	   out.setLength(out.length() - 1);
           player.sendMessage(out.toString());
		return true;
	}
	
	public static boolean chat(EverydayMiracles plugin, CommandSender sender, String[] args, DataHandler dh){
		//Check if not player
 	    if (!(sender instanceof Player)) {sender.sendMessage("The console has no higher power to speak to!"); return true;}
 	    
 	    String deity = dh.getPlayerDeity(sender);
 	    
 	    //Check if no deity assigned
 	    if(deity == null){sender.sendMessage("You have not yet chosen who to follow!"); return true;}
		
 	    FileConfiguration deityConfig = dh.getDeity(deity);
		try{
			Map<String,Object> greetingsList = deityConfig.getConfigurationSection(deity+".chatter").getValues(false);
			Object[] values = greetingsList.values().toArray();
			Random generator = new Random();
			sender.sendMessage(ChatColor.ITALIC+((String) values[generator.nextInt(values.length)]));
		} catch(NullPointerException e) {
			sender.sendMessage(ChatColor.RED+"Your deity has no chatter set up...contact your dev!"); return false;
		}
		return true;
	}
	
	public static boolean quest(EverydayMiracles plugin, CommandSender sender, String[] args, DataHandler dh){
		//Check if not player
 	    if (!(sender instanceof Player)) {sender.sendMessage("The console has no higher power to speak to!"); return false;}
 	    String deity = dh.getPlayerDeity(sender);
 	    Player player = (Player)sender;
 	    String name = player.getName();
 	    
 	    //Check if no deity assigned
 	    if(deity == null){player.sendMessage("You have not yet chosen who to follow!"); return true;}
		
 	    FileConfiguration deityConfig = dh.getDeity(deity);
		try{
			Map<String,Object> questList = deityConfig.getConfigurationSection(deity+".quests").getValues(false);
			List<String> keys = new ArrayList<String>(questList.keySet());
			Random generator = new Random();
			String randomKey = keys.get( generator.nextInt(keys.size()) );
			ConfigurationSection quest = (ConfigurationSection) questList.get(randomKey);
			try{
				//transfer data from quest to player. Chunky. Better way?
				ConfigurationSection playerData = plugin.getPlayerData();
				String questDesc = quest.getString("text");
				playerData.set(name+".questDesc", questDesc);
				playerData.set(name+".questStatistic", quest.getString("statistic"));
				playerData.set(name+".questItem", quest.getString("item"));
				playerData.set(name+".questItemAmount", quest.getInt("itemAmount"));
				playerData.set(name+".questWorth", quest.getInt("points"));
				if (playerData.getString(name+".questStatistic")!=null){
					plugin.log(quest.getString("statistic"));
					Statistic statistic = Statistic.valueOf(quest.getString("statistic"));
					int oldStat = player.getStatistic(statistic);
					playerData.set(name+".questStatStart", oldStat);
					playerData.set(name+".questStatNeed", oldStat+quest.getInt("statAmount"));
				}
				plugin.savePlayerData();
				player.sendMessage(ChatColor.ITALIC+questDesc);
			} catch (NullPointerException e){
				player.sendMessage(ChatColor.RED+"One of your leader's quests is malformed...contact your dev! Quest "+quest.toString());
				return false;
			}
		} catch(NullPointerException e) {
			player.sendMessage(ChatColor.RED+"Your leader has no quests set up...contact your dev!");
			return false;
		}
		return true;
	}
	
	public static boolean enquire(EverydayMiracles plugin, CommandSender sender, String[] args, DataHandler dh){
		//Check if not player
 	    if (!(sender instanceof Player)) {sender.sendMessage("The console cannot accept quests, it is too powerful!"); return true;}
 	    
 	    String deity = dh.getPlayerDeity(sender);
 	    
 	    //Check if no deity assigned
 	    if(deity == null){sender.sendMessage("You have not yet chosen who to follow!"); return true;}
 	    String questDesc = plugin.getPlayerData().getString(sender.getName()+".questDesc");
		if(questDesc!=null){
			sender.sendMessage("You recall what "+deity+" said: "+ChatColor.ITALIC+questDesc);
		} else {
			sender.sendMessage("You have no outstanding quests");
		}
		return true;
	}
	
	public static boolean submit(EverydayMiracles plugin, CommandSender sender, String[] args, DataHandler dh){
		boolean statComplete = false;
		boolean itemComplete = false;
		//Check if not player
 	    if (!(sender instanceof Player)) {sender.sendMessage("The console cannot accept quests, it is too powerful!"); return true;}
 	    
 	    Player player = (Player) sender;
 	    String deity = dh.getPlayerDeity(player);
 	    String name = player.getName();
 	    
 	    //Check if no deity assigned
 	    if(deity == null){sender.sendMessage("You have not yet chosen who to follow!"); return true;}
 	    
 	    FileConfiguration playerData = plugin.getPlayerData();
 	    String statisticString = playerData.getString(name+".questStatistic");
 	    String itemString = playerData.getString(name+".questItem");
 	    
 	    //Check if stats part of quest is done
 	    plugin.log("statisticString is: "+statisticString);
 	    plugin.log("itemString is: "+statisticString);
 	    if(statisticString==null){
 	    	statComplete = true;
 	    	plugin.log("Autoclear stats");
 	    }else{
 	    	Statistic statistic = Statistic.valueOf(statisticString);
 	    	int oldStat = player.getStatistic(statistic);
 	    	if(oldStat>=playerData.getInt(name+".questStatNeed")){
 	    		statComplete = true;
 	    	} else {
 	    		player.sendMessage("You have not yet completed the requested action!");
 	    	}
 	    }
 	    
 	    //Check if items part of quest is done
 	   if(itemString==null){
	    	itemComplete = true;
	    	plugin.log("Autoclear items");
	    }else{
	    	int itemNumber = playerData.getInt(name+".questItemAmount");
	    	int playerAmount = 0;
	    	PlayerInventory inventory = player.getInventory();
	    	ItemStack[] contents = inventory.getContents();
	    	
	    	for(ItemStack itemStack : contents){
	    		//TODO Fix this you lazy asshole
	    		//So this is almost definitely redundant as shit but I am super tired of rebuilding this project so BANDAIDS.
	    		if (itemStack!=null && itemStack.getType() !=null && itemStack.getType() == Material.valueOf(itemString)){
	    		  playerAmount += itemStack.getAmount();	
	    		}
	    	}
	    	ItemStack itemstack = new ItemStack(Material.valueOf(itemString), itemNumber);
	    	if (playerAmount>=itemNumber) {
	            inventory.removeItem(itemstack);
	            itemComplete = true;
	    	} else {
	    		player.sendMessage("You need to collect more items before handing them in!");
	    	}
	    }
    	if(statComplete && itemComplete){
    		FileConfiguration deityConfig = dh.getDeity(deity);
    		try{
    			Map<String,Object> completionList = deityConfig.getConfigurationSection(deity+".quest complete").getValues(false);
    			Object[] values = completionList.values().toArray();
    			Random generator = new Random();
    			sender.sendMessage(ChatColor.ITALIC+((String) values[generator.nextInt(values.length)]));
    		} catch(NullPointerException e) {
    			sender.sendMessage(ChatColor.RED+"Your leader has no dialog set up for complete quests...contact your dev!"); return false;
    		}
    		int pointsEarned = playerData.getInt(name+".questWorth");
    		player.sendMessage("You have earned "+pointsEarned+" points!");
    		int newPoints = playerData.getInt(name+".points")+pointsEarned;
    		playerData.set(name+".points", newPoints);
    		plugin.savePlayerData();
    		resetQuestData(plugin, player.getName());
    	}
 	    return true;
	}
	
	public static boolean points(EverydayMiracles plugin, CommandSender sender){
		if (!(sender instanceof Player)) {sender.sendMessage("The console is pointless. Wait..."); return true;}
		sender.sendMessage("You have "+plugin.getPlayerData().getInt(sender.getName()+".points")+" points.");
		return true;
	}
	
	public static void resetQuestData(EverydayMiracles plugin, String name){
		FileConfiguration playerData = plugin.getPlayerData();
		playerData.set(name+".questDesc",null);
		playerData.set(name+".questStatistic", null);
		playerData.set(name+".questStatStart", null);
		playerData.set(name+".questStatNeed", null);
		playerData.set(name+".questItem", null);
		playerData.set(name+".questItemAmount", null);
		playerData.set(name+".questWorth", null);
		plugin.savePlayerData();
	}
	
	public static boolean request(EverydayMiracles plugin, CommandSender sender, String[] args, DataHandler dh){
		//Check if not player
 	    if (!(sender instanceof Player)) {sender.sendMessage("The console already owns everything!"); return true;}
 	    
 	    String deity = dh.getPlayerDeity(sender);
 	    
 	    //Check if no deity assigned
 	    if(deity == null){sender.sendMessage("You have not yet chosen who to follow!"); return true;}
		FileConfiguration deityConfig = dh.getDeity(deity);
		Map<String,Object> requestList = deityConfig.getConfigurationSection(deity+".requests").getValues(false);
		if(args.length>1 && Requester.checkRequest(args[1].toUpperCase()) && requestList.containsKey(args[1].toLowerCase())){
			Requester.invoke(plugin, sender, args[1].toUpperCase());
			return true;
		} else {
			Set<String> spells = requestList.keySet();
			StringBuilder out = new StringBuilder("Available requests (and costs) are: ");
			for(String spell : spells){
				spell = spell.toUpperCase();
				if(Requester.checkRequest(spell)){
					out.append(spell.toLowerCase()+ "("+Requester.requestCost(spell)+"), ");
				} else {
					plugin.log("Illegal spell! Only use the programmed ones, caps matter, please!");
				}
			}
			sender.sendMessage(out.toString());
			return true;
		}
	}
}

