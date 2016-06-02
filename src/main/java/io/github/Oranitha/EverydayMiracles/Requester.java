package io.github.Oranitha.EverydayMiracles;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class Requester {
  public enum Request{
	  KIT,
	  TRINKET,
	  HEAL,
	  ORE,
	  CHICKEN,
	  COW,
	  TREASURE
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
			  return 20;
		  case CHICKEN:
			  return 200;
		  case COW:
			  return 500;
		  case HEAL:
			  return 10;
		  case ORE:
			  return 15;
		  case TRINKET:
			  return 2;
		  case TREASURE:
			  return 30;
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
				  ItemStack shovel = new ItemStack(Material.STONE_SPADE,1);
				  world.dropItem(player.getLocation(), shovel);
				  ItemStack pick = new ItemStack(Material.STONE_PICKAXE,1);
				  world.dropItem(player.getLocation(), pick);
				  ItemStack sword = new ItemStack(Material.STONE_SWORD,1);
				  world.dropItem(player.getLocation(), sword);
				  ItemStack torches = new ItemStack(Material.TORCH,16);
				  world.dropItem(player.getLocation(), torches);
				  ItemStack bread = new ItemStack(Material.BREAD,2);
				  world.dropItem(player.getLocation(), bread);
				  break;
			  case CHICKEN:
				  	world.spawnEntity(player.getLocation(), EntityType.CHICKEN);
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
					  plugin.log("Faulty name in the Trinkets item list: "+ treasureString);
				  }
				  break;
			  case HEAL:
				  double health = player.getHealth()+8.0;
				  if(health>20.0){
					  player.setHealth(20.0);
				  } else {
					  player.setHealth(health);
				  }
				  break;
			  case ORE:
				  ItemStack coal = new ItemStack(Material.COAL_BLOCK,1);
				  world.dropItem(player.getLocation(), coal);
				  ItemStack ore = new ItemStack(Material.IRON_ORE,8);
				  world.dropItem(player.getLocation(), ore);
				  ItemStack ore2 = new ItemStack(Material.GOLD_ORE,1);
				  world.dropItem(player.getLocation(), ore2);
				  break;
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
