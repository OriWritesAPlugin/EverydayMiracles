package io.github.Oranitha.EverydayMiracles;

import java.io.File;
import java.util.ArrayList;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

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
	
	public ArrayList<String> getDeities(){
		return deities;
	}
	
	public String getPlayerDeity(CommandSender sender){
		String name = sender.getName();
		if(!plugin.getPlayerData().getBoolean(name)){
			FileConfiguration dataFile = plugin.getPlayerData();
			dataFile.set(name, true);
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

}
