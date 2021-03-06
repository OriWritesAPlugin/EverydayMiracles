package io.github.Oranitha.EverydayMiracles;

import java.io.File;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class EverydayMiracles extends JavaPlugin{
	
	DataHandler dataHandler;
	private File configf, earthmotherf, playerdataf;
	private FileConfiguration config, earthmother, playerdata;
	//RenameListener's a private inner class, FMI.
	private RenameListener renameListener;
	
//_______________________________________MAIN LOGIC_____________________________________\\
	@Override
	public void onEnable(){
		//Log statements should be prefaced with plugin name automagically
		log("Plugin is loading up");
		createFiles();
		dataHandler = new DataHandler(this);	
		renameListener = new RenameListener();
		Bukkit.getServer().getPluginManager().registerEvents(renameListener, this);
	}
	
	@Override
	public void onDisable(){
		saveConfig();
		log("Plugin has been disabled");
	}
	
    @Override
    //Theoretically a much prettier way of doing this, but the doc was so-so. Refactor?
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	if(label.equals("edm") && sender.hasPermission("everydaymiracles.worship")){
    	if(args.length > 0){
    		if(args[0].equalsIgnoreCase("follow")) {Executor.follow(this, sender, args);}
    		else if(args[0].equalsIgnoreCase("chat")) {Executor.chat(this,sender,args, dataHandler);}
    		else if(args[0].equalsIgnoreCase("quest")) {Executor.quest(this,sender,args, dataHandler);}
    		else if(args[0].equalsIgnoreCase("enquire")) {Executor.enquire(this,sender,args, dataHandler);}
    		else if(args[0].equalsIgnoreCase("submit")) {Executor.submit(this,sender,args, dataHandler);}
    		else if(args[0].equalsIgnoreCase("forfeit")) {Executor.forfeit(this,sender, dataHandler);}
    		else if(args[0].equalsIgnoreCase("request")) {Executor.request(this,sender,args, dataHandler);}
    		else if(args[0].equalsIgnoreCase("rankings")) {Executor.rankings(this,sender,args, dataHandler);}
    		else if(args[0].equalsIgnoreCase("points")) {Executor.points(this,sender);
    		} else {displayCommands(sender);}}
    	else { displayCommands(sender);}
    	} else if(label.equals("edma")&&sender.hasPermission("everydaymiracles.admin")){
    		if(args.length > 0){
    			if(args[0].equalsIgnoreCase("givePoints")) {Executor.givePoints(this,sender,args);}
    			else if(args[0].equalsIgnoreCase("setDeity")) {Executor.setDeity(this,sender,args, dataHandler);}
    			else if(args[0].equalsIgnoreCase("points")) {Executor.getPoints(this,sender,args);}
    			else if(args[0].equalsIgnoreCase("islands")) {Executor.setIslands(this,sender,args, dataHandler);}
    			else if(args[0].equalsIgnoreCase("conquest")) {Executor.conquest(this,sender,args, dataHandler);}
    		} else {displayCommandsA(sender);}
    	} else { displayCommandsA(sender);}
    	return true;
    }
   
  //_______________________________________GET/SET/SAVE_____________________________________\\ 
    public FileConfiguration getPlayerData(){
    	if (playerdata == null){
    		createFiles();
    		log("Playerdata file lost! Has something changed?");
    	}
    	return playerdata;
    }
    
    public FileConfiguration getConfig(){
    	if (config == null){
    		createFiles();
    		log("Config file lost! Has something changed?");
    	}
    	return config;
    }
    
    public void savePlayerData(){
    	try {
			getPlayerData().save(playerdataf);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void saveDeity(String deity){
    	try {
    		File deityf = new File(getDataFolder()+"/deities/"+"test"+".yml");
    		log(deityf.getAbsolutePath());
			dataHandler.getDeity(deity).save(deityf);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void setNickname(Player p){
    	   log(dataHandler.getPlayerDeity(p));
    	   log(p.getName());
	       String deity = dataHandler.getPlayerDeity(p);
	       if (!(deity==null)){
	    	   FileConfiguration deityFile = dataHandler.getDeity(deity);
	    	   FileConfiguration playerData = getPlayerData();
	    	   int followerLevel = playerData.getInt(p.getName()+".followerLevel");
	    	   if(!(followerLevel>=0)) followerLevel = 0;
	    	   String chatColor=deityFile.getString(deity+".chatcolor");
	    	   String followerNick = deityFile.getString(deity+".followerLevel"+followerLevel);
	           p.setDisplayName(ChatColor.WHITE+"["+ChatColor.valueOf(chatColor)+followerNick+ChatColor.WHITE+"] "+p.getDisplayName());
	       }
    }
    
    public DataHandler getDataHandler(){
    	return dataHandler;
    }
    
//_____________________________________UTILITY METHODS__________________________________\\
    
    public void log(String message){
    	getLogger().info(message);
    }
    
   
    public void displayCommands(CommandSender sender){
    	//TODO: Make this not terrible
    	sender.sendMessage("Use /help EveryDayMiracles for a list of commands");
    }
    
    public void displayCommandsA(CommandSender sender){
    	//TODO: Make this not terrible...but for admins.
    	sender.sendMessage("Use /help EveryDayMiracles for a list of commands");
    }
    
  //___________________________________PRIVATE HELPERS__________________________________\\
    
    private void createFiles() {
    	configf = new File(getDataFolder(), "config.yml");
    	playerdataf = new File(getDataFolder(), "playerdata.yml");
    	File deityFolder = new File(getDataFolder()+"/deities");
    	if(!configf.exists()){
    		log("Creating config.yml");
    		configf.getParentFile().mkdirs();
    		saveResource("config.yml",false);
    	}
    	if(!playerdataf.exists()){
    		log("Creating playerdata.yml");
    		configf.getParentFile().mkdirs();
    		saveResource("playerdata.yml",false);
    	}
    	if(!deityFolder.exists()){
    		log("Creating folder to store deities and giving you a starter deity");
    		deityFolder.mkdirs();
        	earthmotherf = new File(getDataFolder(), "EarthMother.yml");
    		saveResource("EarthMother.yml",false);
    		earthmother = new YamlConfiguration();
    		try {
				earthmother.load(earthmotherf);
				//Probably a better way, but...welp. 
				Files.move(earthmotherf.toPath(), Paths.get(deityFolder.getPath()+"/EarthMother.yml"));
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
    	}
    	config = new YamlConfiguration();
    	playerdata = new YamlConfiguration();
    	try {
			config.load(configf);
			playerdata.load(playerdataf);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
    }
    
  //_____________________________________INNER CLASSES__________________________________\\
    
    //Vault integration questionable, more testing needed, might just need to override the way the
    //other guys do.
    private class RenameListener implements Listener {
        @EventHandler
       public void PlayerJoin(PlayerJoinEvent event) {
	       Player p =  event.getPlayer();
	       setNickname(p);
       }
    }
}
