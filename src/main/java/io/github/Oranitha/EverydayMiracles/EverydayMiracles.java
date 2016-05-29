package io.github.Oranitha.EverydayMiracles;

import java.io.File;

import java.io.IOException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class EverydayMiracles extends JavaPlugin{
	
	DataHandler datahandler;
	@SuppressWarnings("unused") //warns about earthmotherf--it is used!
	private File configf, earthmotherf, playerdataf;
	@SuppressWarnings("unused") //warns about earthmother--it is used!
	private FileConfiguration config, earthmother, playerdata;
	
	@Override
	public void onEnable(){
		getLogger().info("Loading and building...");
		createFiles();
		datahandler = new DataHandler(this);
	}
	
	@Override
	public void onDisable(){
		saveConfig();
		getLogger().info("onDisable invoked!");
	}
	
    @Override
    public boolean onCommand(CommandSender sender, Command command,
                             String label, String[] args) {
    	if(label.equals("edm")){
    	log(args.toString());
    	if(args.length > 0){
    		if(args[0].equalsIgnoreCase("follow")) {Executor.follow(this, sender, args);}
    		else if(args[0].equalsIgnoreCase("pray")) {sender.sendMessage("Heya! "+this.getPlayerData().getString(sender.getName()+".deity"));
    		} else {displayCommands(sender);}}
    	else { displayCommands(sender);}
        //getCommand("edm pray").setExecutor(new PrayCommand());
        //getCommand("edm guidance").setExecutor(new GuidanceCommane());
        //getCommand("edm check").setExecutor(new CheckCommand());
        //getCommand("edm offer").setExecutor(new OfferCommand());
    	}
    	return true;
    }
    
    public FileConfiguration getPlayerData(){
    	if (playerdata == null){
    		createFiles();
    		log("playerdata was lost...");
    	}
    	log ("Path: "+playerdata.getCurrentPath());
    	return playerdata;
    }
    
    public void savePlayerData(){
    	try {
			getPlayerData().save(playerdataf);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void log(String message){
    	getLogger().info(message);
    }
    
    private void displayCommands(CommandSender sender){
    	sender.sendMessage("For a list of commands, see /help EverydayMiracles");
    }
    
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
        	earthmotherf = new File(getDataFolder()+"/deities", "EarthMother.yml");
    		saveResource("EarthMother.yml",false);
    	}
    	config = new YamlConfiguration();
    	playerdata = new YamlConfiguration();
    	try {
			config.load(configf);
			playerdata.load(playerdataf);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
    }
}
