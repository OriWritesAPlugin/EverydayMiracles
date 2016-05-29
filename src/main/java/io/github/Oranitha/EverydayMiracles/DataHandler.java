package io.github.Oranitha.EverydayMiracles;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public final class DataHandler {
	
	private static ArrayList<String> deities;
	private static File dataFolder;
	private static EverydayMiracles plugin;
	
	public DataHandler(){}
	
	public DataHandler(EverydayMiracles instance){
		plugin=instance;
		dataFolder = instance.getDataFolder();
		deities = generateDeityList();
	}
	
	public ArrayList<String> getDeities(){
		return deities;
	}
	
	public FileConfiguration getDeity(String deity){
			YamlConfiguration deityYAML = new YamlConfiguration();
		    File deityFile = new File(plugin.getDataFolder()+"/deities/"+deity+".yml");
	    	if (!deityFile.exists()){
	    		plugin.log("Deity "+deity+" not found (from FileConfiguration.getDeity())");
	    	} else {
	    		try {
					deityYAML.load(deityFile);
				} catch (IOException | InvalidConfigurationException e) {
					plugin.log("Bad deity file...somehow");
					e.printStackTrace();
				}
	    	}
	    	return deityYAML;
	}
	
	public String getPlayerDeity(CommandSender sender){
		String name = sender.getName();
		if(plugin.getPlayerData().getString(name+".deity")==null){
			plugin.log("Creating player info for "+name);
			FileConfiguration dataFile = plugin.getPlayerData();
			dataFile.set(name, true);
			//Null values exist for reference, will not show up in playerdata.yml
			dataFile.set(name+".deity", null);
			dataFile.set(name+".rank", null);
			dataFile.set(name+".points", 0);
			dataFile.set(name+".questText", null);
			dataFile.set(name+".questStat", null);
			dataFile.set(name+".questTarget", 0);
			dataFile.set(name+".lastQuest", 0);
			plugin.savePlayerData();
		}
		return plugin.getPlayerData().getString(name+".deity");
	}
	
	private ArrayList<String> generateDeityList() {
		ArrayList<String> deityList = new ArrayList<String>();
		File[] files = new File(dataFolder+"/deities").listFiles();
		//If this pathname does not denote a directory, then listFiles() returns null. 
		if(files != null){
			for (File file : files) {
			    if (file.isFile()) {
			        deityList.add(file.getName().replace(".yml", ""));
			    }
			}
		}
		return deityList;
		
	}

}
