package io.github.Oranitha.EverydayMiracles;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class Requester {
  public enum Request{
	  Kit,
	  Trinket,
	  Heal,
	  Ore
  }
  
  public static boolean checkRequest(String string){
	  try{
	    Request.valueOf(string);
	    return true;
	  } catch(IllegalArgumentException e){
		return false;
	  }
  }
  
  public static int requestCost(String string){
	  try{
		  Request request = Request.valueOf(string);
		  switch(request) {
		  case Kit:
			  return 20;
		  case Trinket:
			  return 2;
		  case Heal:
			  return 10;
		  case Ore:
			  return 15;
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
	  
	  if(balance<cost){
		  sender.sendMessage("You don't have enough points!");
	  } else {
		  int newPoints = playerData.getInt(name+".points")-cost;
		  playerData.set(name+".points", newPoints);
		  plugin.savePlayerData();
		  try{
			  Request request = Request.valueOf(string);
			  switch(request) {
			  case Kit:
				  World world = player.getWorld();
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
			  case Trinket:
				  break;
			  case Heal:
				  double health = player.getHealth()+80;
				  if(health>200){
					  player.setHealth(200);
				  } else {
					  player.setHealth(health);
				  }
				  break;
			  case Ore:
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
