package com.volcanicplaza.executePHP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.volcanicplaza.executePHP.Updater.UpdateResult;

public class executePHP extends JavaPlugin {
	
	//Updater Class
  	public static UpdateResult update;
  	public static String name = "";
  	public static String version;
  	public static long size = 0;
  	
  	public static JavaPlugin plugin;
  	
  	public static HashMap<String, String> resultMessages = new HashMap<String, String>();
	
	@Override
	public void onEnable(){
		Bukkit.getLogger().info("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
		PluginDescriptionFile pdfFile = getDescription();
		plugin = this;
		
		getConfig().options().copyDefaults(true);
		saveConfig();
		Config.populateResultMap();
		
		//Check if a new update is available
		long startTime = System.currentTimeMillis();
		Bukkit.getLogger().info("Checking for update from BukkitDev...");
		if (Config.updateCheck() == true){
			Updater updater = new Updater(plugin, "executePHP", this.getFile(), Updater.UpdateType.NO_DOWNLOAD, false);
			update = updater.getResult();
			if (update == Updater.UpdateResult.UPDATE_AVAILABLE) {
				name = updater.getLatestVersionString(); // Get the latest version
				size = updater.getFileSize(); // Get latest size
				version = updater.getLatestVersionString().substring(updater.getLatestVersionString().lastIndexOf('v') + 1);
				Bukkit.getLogger().info("There is a new update available!");
				Bukkit.getLogger().info("File name: " + name);
				Bukkit.getLogger().info("Latest Version: " + version);
				Bukkit.getLogger().info("File size: " + size);
			} else if (updater.getResult() == Updater.UpdateResult.NO_UPDATE){
				Bukkit.getLogger().info("You have the latest version of executePHP. (Yay)");
			}
		} else {
			Bukkit.getLogger().info("WARNING: You have disabled update checking in the configuration file!");
			Bukkit.getLogger().info("This could be preventing you from downloading an important update.");
		}
		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		Bukkit.getLogger().info("-Update Check Time: " + duration + "ms-");
		Bukkit.getLogger().info("");
		
		
		Bukkit.getLogger().info(pdfFile.getName() + " v" + pdfFile.getVersion() + " has been enabled.");
		Bukkit.getLogger().info("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
	}

	public void onDisable(){
		PluginDescriptionFile pdfFile = getDescription();
		Bukkit.getLogger().info(pdfFile.getName() + " v" + pdfFile.getVersion() + " has been disabled.");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		PluginDescriptionFile pdfFile = plugin.getDescription();
		if (cmd.getName().equalsIgnoreCase("executePHP")){
			if (args.length == 0){
				sender.sendMessage(ChatColor.AQUA + "-=-=-=-=-=- executePHP -=-=-=-=-=-");
				sender.sendMessage(ChatColor.AQUA + "/executePHP" + ChatColor.GRAY + " Shows this help page.");
				sender.sendMessage(ChatColor.AQUA + "/executePHP reload" + ChatColor.GRAY + " Reloads the configuration file.");
				sender.sendMessage(ChatColor.AQUA + "/verify <code>" + ChatColor.GRAY + " Verify your code.");
				sender.sendMessage(ChatColor.AQUA + "Developed by: " + ChatColor.GRAY + "NXTGUY from www.volcanicplaza.com");
				sender.sendMessage(ChatColor.AQUA + "-=-=-=-=-=-{ v" + pdfFile.getVersion() + " }-=-=-=-=-=-");	
				return true;
			} else if (args.length == 1){
				if (args[0].equalsIgnoreCase("reload")){
					if (sender.hasPermission("executePHP.reload")){
						reloadConfig();
						Config.populateResultMap();
						sender.sendMessage(ChatColor.AQUA + "Reload Complete!");
					} else {
						sender.sendMessage(ChatColor.RED + "You do not have permission to perform that executePHP command.");
					}
					return true;
				}
			}
		} else if (cmd.getName().equalsIgnoreCase("verify")){
			if (!(sender.hasPermission("executePHP.verify"))){
				sender.sendMessage(ChatColor.RED + "You do not have permission to perform that executePHP command.");
				return true;
			}
			if (args.length == 0){
				sender.sendMessage(ChatColor.RED + "Incorrect Command Usage." + ChatColor.GRAY + " /verify <code>");
				return true;
			} else if (args.length == 1){
				if (sender instanceof Player){
					Player player = (Player) sender;
					
					//Show verifying message.
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Config.verifyingMessage()));
					
					//Get the result from the PHP call.
					String result = executePHP.verifyCode(player, args[0]);
					
					//Check if the PHP script returned a valid message that we know about.
					if (resultMessages.containsKey(result)){
						//Show appropriate message result from configuration.
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', resultMessages.get(result)));
					} else {
						sender.sendMessage(ChatColor.RED + "Error. Check the console for the stack trace.");
						Bukkit.getLogger().info("The result: '" + result + "' isn't a known result.");
						Bukkit.getLogger().info("If it is, please add a result message to the configuration file.");
					}
					
					return true;
					
				} else {
					sender.sendMessage(ChatColor.RED + "Only players can execute this command!");
				}
				return true;
			}
		}
		return false;
	}
	
	public static String verifyCode(Player player, String code){
		URL url;
		try {
			String uuid = player.getUniqueId().toString();
			url = new URL(Config.getURL().replace("%username%", player.getName()).replace("%code%", code).replace("%uuid%", uuid));
	        URLConnection yc = url.openConnection();
	        BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
	       
	        //String inputLine;
	        //while ((inputLine = in.readLine()) != null)
	        String result = in.readLine();
	        in.close();
	        return result;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return "Error occured. Check console for stack trace.";
	}
}
