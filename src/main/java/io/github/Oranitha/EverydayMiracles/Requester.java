package io.github.Oranitha.EverydayMiracles;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class Requester {
  public enum Request{
	  KIT,
	  TRINKET,
	  HEAL,
	  ORE,
	  CHICKEN,
	  COW,
	  SHEEP,
	  WOLF,
	  OCELOT,
	  TREASURE,
	  WRIT
  }
  
  public static boolean checkRequest(String string){
	  try{
	    Request.valueOf(string.toUpperCase());
	    return true;
	  } catch(IllegalArgumentException e){
		return false;
	  }
  }
  
  public static int requestCost(String string){
	  try{
		  Request request = Request.valueOf(string);
		  switch(request) {
		  case KIT:
			  return 40;
		  case CHICKEN:
			  return 200;
		  case COW:
			  return 500;
		  case SHEEP:
			  return 300;
		  case WOLF:
			  return 400;
		  case OCELOT:
			  return 400;
		  case HEAL:
			  return 30;
		  case ORE:
			  return 30;
		  case TRINKET:
			  return 2;
		  case TREASURE:
			  return 30;
		  case WRIT:
			  return 50;
		  default:
			  return 0;
		  }
	  } catch(IllegalArgumentException e){
		return -1;
	  } 
  }
  
  public static void invoke(EverydayMiracles plugin, CommandSender sender, String string){
	  FileConfiguration playerData = plugin.getPlayerData();
	  Player player = (Player) sender;
	  String name = player.getName();
	  int cost = requestCost(string);
	  int balance = playerData.getInt(name+".points");
	  DataHandler dh = plugin.getDataHandler();
	  
	  if(balance<cost){
		  player.sendMessage("You don't have enough points!");
	  } else {
		  int newPoints = playerData.getInt(name+".points")-cost;
		  playerData.set(name+".points", newPoints);
		  plugin.savePlayerData();
		  World world = player.getWorld();
		  try{
			  Request request = Request.valueOf(string);
			  switch(request) {
			  case KIT:
				  ItemStack shovel = new ItemStack(Material.IRON_SPADE,1);
				  world.dropItem(player.getLocation(), shovel);
				  ItemStack pick = new ItemStack(Material.IRON_PICKAXE,1);
				  world.dropItem(player.getLocation(), pick);
				  ItemStack sword = new ItemStack(Material.IRON_SWORD,1);
				  world.dropItem(player.getLocation(), sword);
				  ItemStack torches = new ItemStack(Material.TORCH,32);
				  world.dropItem(player.getLocation(), torches);
				  ItemStack bread = new ItemStack(Material.BREAD,3);
				  world.dropItem(player.getLocation(), bread);
				  break;
			  case CHICKEN:
				  	world.spawnEntity(player.getLocation(), EntityType.CHICKEN);
				  break;
			  case COW:
				  	world.spawnEntity(player.getLocation(), EntityType.COW);
				  break;
			  case SHEEP:
				  	world.spawnEntity(player.getLocation(), EntityType.SHEEP);
				  break;
			  case WOLF:
				  	world.spawnEntity(player.getLocation(), EntityType.WOLF);
				  break;
			  case OCELOT:
				  	world.spawnEntity(player.getLocation(), EntityType.OCELOT);
				  break;
			  case TRINKET:
				  String trinketString = dh.getItemFromPool();
				  try{
					  ItemStack trinket = new ItemStack(Material.valueOf(trinketString),1);
					  world.dropItem(player.getLocation(), trinket);
				  } catch (IllegalArgumentException e){
					  plugin.log("Faulty name in the Trinkets item list: "+ trinketString);
				  }
				  break;
			  case TREASURE:
				  String treasureString = dh.getItemFromTreasurePool();
				  try{
					  ItemStack trinket = new ItemStack(Material.valueOf(treasureString),1);
					  world.dropItem(player.getLocation(), trinket);
				  } catch (IllegalArgumentException e){
					  plugin.log("Faulty name in the Treasures item list: "+ treasureString);
				  }
				  break;
			  case HEAL:
				  int lastTime = playerData.getInt(name+".lastHeal");
				  int currentTime = player.getStatistic(Statistic.PLAY_ONE_TICK);
				  if(currentTime > lastTime+2400){
					  player.setHealth(20.0);
					  playerData.set(name+".lastHeal", currentTime);
				  } else {
					  player.sendMessage("You must play "+(lastTime+2400-currentTime)/20+" seconds before healing again!");
					  int addedPoints = playerData.getInt(name+".points")+requestCost("HEAL");
					  playerData.set(name+".points", addedPoints);
				  }
				  plugin.savePlayerData();
				  break;
			  case ORE:
				  ItemStack coal = new ItemStack(Material.COAL_BLOCK,1);
				  world.dropItem(player.getLocation(), coal);
				  ItemStack ore = new ItemStack(Material.IRON_ORE,8);
				  world.dropItem(player.getLocation(), ore);
				  ItemStack ore2 = new ItemStack(Material.GOLD_ORE,1);
				  world.dropItem(player.getLocation(), ore2);
				  break;
			  case WRIT:
				  ItemStack item = new ItemStack(Material.PAPER,1);
				  ItemMeta im = item.getItemMeta();
				  im.setDisplayName(ChatColor.GOLD + "Writ of Honorable Service");
				  ArrayList<String> loreList = new ArrayList<String>();
				  loreList.add(ChatColor.DARK_AQUA + "Evidence of loyal service to the gods.");//This is the first line of lore
				  loreList.add(ChatColor.DARK_AQUA + "Worth 50 points.");
				  im.setLore(loreList);
				  item.setItemMeta(im);
				  world.dropItem(player.getLocation(), item);
			  default:
				  plugin.log("The plugin author did a bad. Faulty enum: "+string);
				  break;
			  }
		  } catch(IllegalArgumentException e){
			return;
		  } 
	 }
  }
}
