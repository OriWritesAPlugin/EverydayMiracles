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
	
	DataHandler datahandler;
	private File configf, earthmotherf, playerdataf;
	private FileConfiguration config, earthmother, playerdata;
	//RenameListener's a private inner class, FYI.
	private RenameListener renameListener;
	
//_______________________________________MAIN LOGIC_____________________________________\\
	@Override
	public void onEnable(){
		log("Loading and building...");
		createFiles();
		datahandler = new DataHandler(this);
		log("Starting listener...");
		renameListener = new RenameListener();
		Bukkit.getServer().getPluginManager().registerEvents(renameListener, this);
	}
	
	@Override
	public void onDisable(){
		saveConfig();
		log("EverydayMiracles has been disabled!");
	}
	
    @Override
    public boolean onCommand(CommandSender sender, Command command,
                             String label, String[] args) {
    	//TODO: Fix shitfit if console calls command
    	if(label.equals("edm")){
    	if(args.length > 0){
    		if(args[0].equalsIgnoreCase("follow")) {Executor.follow(this, sender, args);}
    		else if(args[0].equalsIgnoreCase("task")) {sender.sendMessage(this.getPlayerData().getString(sender.getName()+".deity"+" has no tasks for you right now..."));
    		} else {displayCommands(sender);}}
    	else { displayCommands(sender);}
        //getCommand("edm pray").setExecutor(new PrayCommand());
        //getCommand("edm guidance").setExecutor(new GuidanceCommane());
        //getCommand("edm check").setExecutor(new CheckCommand());
        //getCommand("edm offer").setExecutor(new OfferCommand());
    	}
    	return true;
    }
   
  //_______________________________________GET/SET_____________________________________\\ 
    public FileConfiguration getPlayerData(){
    	if (playerdata == null){
    		createFiles();
    		log("playerdata was lost...");
    	}
    	return playerdata;
    }
    
    public void savePlayerData(){
    	try {
			getPlayerData().save(playerdataf);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void setNickname(Player p){
	       String deity = datahandler.getPlayerDeity(p);
	       if (!(deity==null)){
	    	   FileConfiguration deityFile = datahandler.getDeity(deity);
	    	   String chatColor=deityFile.getString(deity+".chatcolor");
	    	   String followerNick = deityFile.getString(deity+".followers");
	           p.setDisplayName(ChatColor.WHITE+"["+ChatColor.valueOf(chatColor)+followerNick+ChatColor.WHITE+"] "+p.getName());
	       }
    }
    
//_____________________________________UTILITY METHODS__________________________________\\
    
    public void log(String message){
    	getLogger().info(message);
    }
    
   
    public void displayCommands(CommandSender sender){
    	//TODO: Replace with version that reads properly from config.
    	sender.sendMessage("Supported commands are: follow. Use as /edm <command>, e.g. /edm follow.");
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
    
    private class RenameListener implements Listener {
        @EventHandler
       public void PlayerJoin(PlayerJoinEvent event) {
	       Player p =  event.getPlayer();
	       setNickname(p);
       }
    }
}
