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
		//try{
			Map<String,Object> questList = deityConfig.getConfigurationSection(deity+".quests").getValues(false);
			List<String> keys = new ArrayList<String>(questList.keySet());
			Random generator = new Random();
			String randomKey = keys.get( generator.nextInt(keys.size()) );
			ConfigurationSection quest = (ConfigurationSection) questList.get(randomKey);
			//try{
				//transfer data from quest to player. Chunky. Better way?
				ConfigurationSection playerData = plugin.getPlayerData();
				ConfigurationSection itemList = quest.getConfigurationSection("items");
				ConfigurationSection statList = quest.getConfigurationSection("stats");
				//ConfigurationSection entityList = quest.getConfigurationSection("entities");
				if(!(itemList==null)){
					playerData.set(name+".questItems", itemList);
				}
				if(!(statList==null)){
					Map<String,Object> statSection = statList.getValues(false);
					List<String> statKeys = new ArrayList<String>(statSection.keySet());
					char i = 'A';
					for(String statEntry : statKeys){
						ConfigurationSection statAccess = statList.getConfigurationSection(statEntry);
						playerData.set(name+".questStats."+i+".stat", statAccess.get(".stat"));
					    plugin.log("The probable problem: "+statAccess.get(".stat"));
					    plugin.log("The stat amount possessed is: "+player.getStatistic(Statistic.valueOf(statAccess.get(".stat").toString())));
						int statAmountNew = Integer.parseInt(statAccess.get(".statAmount").toString())+player.getStatistic(Statistic.valueOf(statAccess.get(".stat").toString()));
						playerData.set(name+".questStats."+i+".statAmount", statAmountNew);
						i ++;
					}
				}
				String questDesc = quest.getString("text");
				playerData.set(name+".questDesc", questDesc);
				playerData.set(name+".questStatistic", quest.getString("statistic"));
				playerData.set(name+".questWorth", quest.getInt("points"));
				plugin.savePlayerData();
				player.sendMessage(ChatColor.ITALIC+questDesc);
			//} catch (NullPointerException e){
				//player.sendMessage(ChatColor.RED+"One of your leader's quests is malformed...contact your dev! Quest "+quest.toString());
				//return false;
			//}
		//} catch(NullPointerException e) {
			//player.sendMessage(ChatColor.RED+"Your leader has no quests set up...contact your dev!");
			//return false;
		//}
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
		boolean statComplete = true;
		boolean itemComplete = true;
		//Check if not player
 	    if (!(sender instanceof Player)) {sender.sendMessage("The console cannot accept quests, it is too powerful!"); return true;}
 	    
 	    Player player = (Player) sender;
 	    String deity = dh.getPlayerDeity(player);
 	    String name = player.getName();
 	    
 	    //Check if no deity assigned
 	    if(deity == null){sender.sendMessage("You have not yet chosen who to follow!"); return true;}
 	    
 	    FileConfiguration playerData = plugin.getPlayerData();
 	    ConfigurationSection statList = playerData.getConfigurationSection(name+".questStats");
 	    ConfigurationSection itemList = playerData.getConfigurationSection(name+".questItems");
 	    ArrayList<ItemStack> items = new ArrayList<ItemStack>();
    	PlayerInventory inventory = player.getInventory();
 	   
 	    //Check if stats part of quest is done
 	    if(statList.getValues(false)==null){
 	    	plugin.log("Autoclear stats");
 	    }else{
 	    	Map<String,Object> statSection = statList.getValues(false);
			List<String> statKeys = new ArrayList<String>(statSection.keySet());
			for(String statEntry : statKeys){
				ConfigurationSection statAccess = statList.getConfigurationSection(statEntry);
				String stat = statAccess.get("stat").toString();
				int playerStat = player.getStatistic(Statistic.valueOf(stat));
				int neededStat = (int) statAccess.get("statAmount");
				if(neededStat>playerStat){
					statComplete=false;
					break;
				}
			}
 	    }
 	    
 	    //Check if items part of quest is done
 	   if(itemList.getValues(false)==null){
	    	plugin.log("Autoclear items");
	    }else{
	    	Map<String,Object> itemSection = itemList.getValues(false);
			List<String> itemKeys = new ArrayList<String>(itemSection.keySet());
	    	ItemStack[] contents = inventory.getContents();
			for(String itemEntry : itemKeys){
				ConfigurationSection itemAccess = itemList.getConfigurationSection(itemEntry);
				String item = itemAccess.getString("item");
				String damageVal = itemAccess.getString("damageVal");
				int amount = itemAccess.getInt("itemAmount");
				ItemStack itemStack;
				int playerAmount = 0;
				if(damageVal!=null){
					itemStack = new ItemStack(Material.valueOf(item),amount,Short.parseShort(damageVal));
					for(ItemStack invStack : contents){
			    		if (invStack!=null && invStack.getType() == Material.valueOf(item) && invStack.getDurability() == Short.parseShort(damageVal)){
			    			playerAmount += invStack.getAmount();	
			    		}
			    	}
				} else {
					itemStack = new ItemStack(Material.valueOf(item),amount);
					for(ItemStack invStack : contents){
			    		if (invStack!=null && invStack.getType() == Material.valueOf(item)){
			    		  playerAmount += invStack.getAmount();	
			    		}
			    	}
				}
		    	if (playerAmount>=amount) {
		    		plugin.log("Player amounts: "+playerAmount);
		            items.add(itemStack);
		    	} else {
		    		itemComplete = false;
		    	}
			}
	    }
 	   
			
		if(!statComplete){
			player.sendMessage("You have not yet completed a portion of your task!");
		}
		if(!itemComplete){
			player.sendMessage("You do not have all necessary items!");
		}
    	if(statComplete && itemComplete){
    		for(ItemStack itemStack : items){
    			inventory.removeItem(itemStack);
    		}
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
    		return true;
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

