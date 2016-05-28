package io.github.Oranitha.EverydayMiracles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class EverydayMiracles extends JavaPlugin{
	
	DataHandler datahandler;
	private File configf, earthmotherf;
	private FileConfiguration config, earthmother;
	
	@Override
	public void onEnable(){
		getLogger().info("Loading and building...");
		createFiles();
		datahandler = new DataHandler(getDataFolder());
	}
	
	@Override
	public void onDisable(){
		saveConfig();
		getLogger().info("onDisable invoked!");
	}
	
    @Override
    public boolean onCommand(CommandSender sender, Command command,
                             String label, String[] args) {
        getCommand("embrace").setExecutor(new EmbraceCommand());
        //getCommand("edm pray").setExecutor(new PrayCommand());
        //getCommand("edm guidance").setExecutor(new GuidanceCommane());
        //getCommand("edm check").setExecutor(new CheckCommand());
        //getCommand("edm offer").setExecutor(new OfferCommand());
        return false;

    }
    
    private void createFiles() {
    	configf = new File(getDataFolder(), "config.yml");
    	File deityFolder = new File(getDataFolder()+"/deities");
    	earthmotherf = new File(getDataFolder(), "EarthMother.yml");
    	if(!configf.exists()){
    		configf.getParentFile().mkdirs();
    		saveResource("config.yml",false);
    	}
    	if(!deityFolder.exists()){
    		deityFolder.mkdirs();
    		saveResource("EarthMother.yml",false);
    	}
    	config = new YamlConfiguration();
    	earthmother = new YamlConfiguration();
    	try {
			config.load(configf);
			earthmother.load(earthmotherf);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
    	try {
			Files.move(earthmotherf.toPath(), Paths.get(deityFolder.toPath()+"/EarthMother.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
