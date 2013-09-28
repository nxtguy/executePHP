package com.volcanicplaza.executePHP;

import java.util.ArrayList;

public class Config {
	
	//Return update checking boolean.
	public static boolean updateCheck(){
		return executePHP.plugin.getConfig().getBoolean("checkUpdate");
	}

	public static String getURL() {
		return executePHP.plugin.getConfig().getString("URL");
	}
	
	public static String verifyingMessage() {
		return executePHP.plugin.getConfig().getString("verifyingMessage");
	}
	
	public static void populateResultMap() {
		final ArrayList<String> resultsList = new ArrayList<String>(executePHP.plugin.getConfig().getConfigurationSection("results").getKeys(false));
		//Populate public HashMap.
		for (String string : resultsList){
			executePHP.resultMessages.put(string, executePHP.plugin.getConfig().getString("results." + string));
		}
	}
}
