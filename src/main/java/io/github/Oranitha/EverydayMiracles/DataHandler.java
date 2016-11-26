package io.github.Oranitha.EverydayMiracles;

import java.io.File; 
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

//It's like some kinda budget DAO, except I haven't moved over all the methods yet! Shame.

public final class DataHandler {
	
	private static ArrayList<String> deities;
	private static File dataFolder;
	private static EverydayMiracles plugin;
	private static TreeMap<String,Integer> trinkets;
	private static TreeMap<String,Integer> treasures;
	
	public DataHandler(){}
	
	public DataHandler(EverydayMiracles instance){
		plugin =instance;
		dataFolder = instance.getDataFolder();
		deities = generateDeityList();
		trinkets = generateRewardsList("Trinkets");
		treasures = generateRewardsList("Treasures");
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
	
	public void deityPointsSet(String deity, int points){
		FileConfiguration deityFile = getDeity(deity);
		int oldPoints = deityFile.getInt(deity+".points");
		plugin.log("Oldpoints: "+oldPoints);
		deityFile.set(deity+".points", points);
    	try {
    		plugin.log("Something happened, we're trying to save...");
    		File deityf = new File(plugin.getDataFolder()+"/deities/",deity+".yml");
			deityFile.save(deityf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int deityPointsGet(String deity){
		FileConfiguration deityFile = getDeity(deity);
		plugin.log("Deity: "+deity+" Points: "+deityFile.getInt(deity+".points"));
		return deityFile.getInt(deity+".points");
	}
	
	public NavigableMap<Integer,String> deitiesByPoints(){
		TreeMap<Integer, String> out = new TreeMap<Integer, String>();
		for(String deity : deities){
		  int points = deityPointsGet(deity);
		  while(out.get(points)!=null){
			  points+=1;
		  }
		  out.put(points, deity);
		}
		plugin.log(out.toString());
		return out.descendingMap();
	}
	
	public String getPlayerDeity(CommandSender sender){
		String name = sender.getName();
		if(plugin.getPlayerData().getString(name+".deity")==null){
			plugin.log("Creating player info for "+name);
			FileConfiguration dataFile = plugin.getPlayerData();
			dataFile.set(name, true);
			//Null values exist for resetting player
			dataFile.set(name+".deity", null);
			dataFile.set(name+".rank", null);
			dataFile.set(name+".points", 0);
			dataFile.set(name+".questText", null);
			dataFile.set(name+".questStat", null);
			dataFile.set(name+".lastQuest", 0);
			plugin.savePlayerData();
		}
		plugin.log("Deity not equal to null.");
		plugin.log(plugin.getPlayerData().getString(name+".deity"));
		return plugin.getPlayerData().getString(name+".deity");
	}
	
	//TODO: Make this work for all possible pools
	public String getItemFromPool(){
		Random generator = new Random();
		int pos = generator.nextInt(trinkets.lastEntry().getValue());
		List<String> treasurePool = new ArrayList<String>(trinkets.keySet());
		for(String key: treasurePool){
			int val = trinkets.get(key);
			if(val>=pos){
				return key;
			}
		}
		return null;
	}
	
	//TODO: Remove this, replace with above
	public String getItemFromTreasurePool(){
		Random generator = new Random();
		int pos = generator.nextInt(treasures.lastEntry().getValue());
		List<String> treasurePool = new ArrayList<String>(treasures.keySet());
		for(String key: treasurePool){
			int val = treasures.get(key);
			if(val>=pos){
				return key;
			}
		}
		return null;
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
	
	private TreeMap<String,Integer> generateRewardsList(String rewardPool){
		TreeMap<String,Integer> rewardsList = new TreeMap<String,Integer>(); 
		FileConfiguration config = plugin.getConfig();
		Map<String,Object> unformattedRewards = config.getConfigurationSection("Reward Pools."+rewardPool).getValues(false);
		List<String> keys = new ArrayList<String>(unformattedRewards.keySet());
		int probTotal = 0;
		int holder = 0;
		for(String key : keys){
			//TODO less hideous workaround
			holder = Integer.parseInt(unformattedRewards.get(key).toString());
			probTotal += holder;
			rewardsList.put(key, probTotal);
		}
		return rewardsList;
	}

}
