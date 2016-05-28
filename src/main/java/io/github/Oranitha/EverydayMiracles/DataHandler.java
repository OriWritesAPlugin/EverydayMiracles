package io.github.Oranitha.EverydayMiracles;

import java.io.File;
import java.util.ArrayList;

public final class DataHandler {
	
	private static ArrayList<String> deities;
	private static File dataFolder;
	
	public DataHandler(){}
	
	public DataHandler(File data){
		dataFolder = data;
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

}
