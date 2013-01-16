package com.gmail.dejayyy.ZombieCount;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class zkMain extends JavaPlugin implements Listener{

	public List<String> myTop5 = new ArrayList<String>();
	public static File pluginFolder;
	public static File configFile;
	public FileConfiguration playersFile;
	
	
	public void onEnable(){
		
		this.getServer().getPluginManager().registerEvents(this,  this);
		
		this.loadPlayerYML();
		
	}
	
	public void onDisable(){
		
		this.savePlayerYML();
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String cmdl, String[] args)  {
		
		
		if(!(sender instanceof Player)){
			
			sender.sendMessage("You goofball, you cant execute that command from console!");
			
			return true;
		}
		
		Player player = (Player) sender;
		
		if(cmdl.equalsIgnoreCase("zombiecount") || cmdl.equalsIgnoreCase("zc")){
			
			if(args.length == 0){
				
				this.msgPlayer(player, "Created by: ImDeJay");
				this.msgPlayer(player, "Commands: ");
				this.msgPlayer(player, "/zc show");
				this.msgPlayer(player, "/zc top");
				
			}else if(args.length == 1){
				
				if(args[0].equalsIgnoreCase("show")){
					
					if(player.hasPermission("zombiecount.show")){
						
						if(this.playersFile.isSet("Players." + player.getName())){
							
							int kills = this.playersFile.getInt("Players." + player.getName());
														
							player.sendMessage(ChatColor.DARK_AQUA + "You have killed " + ChatColor.AQUA + kills + ChatColor.DARK_AQUA + " zombies.");
							
						}else{
							
							player.sendMessage(ChatColor.DARK_AQUA + "You have killed " + ChatColor.AQUA + "0" + ChatColor.DARK_AQUA + " zombies.");
						
						}
						
					} //perm check
					
				}else if(args[0].equalsIgnoreCase("top")){
					
					if(this.playersFile.isConfigurationSection("Players")){
						
						this.getTop(player);
						
					}
					
				} //args[0]
				
			}else{
				
				this.msgPlayer(player, "Invalid Arguments!");
				
			}
			
		}
		
		return true;
		
	}
	
	@EventHandler
	public void zombieDeath(EntityDeathEvent event){
		
		Entity zombie = event.getEntity();
		
		if(zombie instanceof Zombie){
			
			if(event.getEntity().getKiller() instanceof Player){
				
				Player p = (Player) event.getEntity().getKiller();
				
				if(this.playersFile.contains("Players." + p.getName())){
					
					int kills = this.playersFile.getInt("Players." + p.getName());
					
					kills++;
				
					this.playersFile.set("Players." + p.getName(), kills);
					
				}else{
					
					this.playersFile.set("Players." + p.getName(), 1);
					
				}
			}
		}
		
	}
	
	public void getTop(Player player) {
		 
		Map<String, Integer> scoreMap = new HashMap<String, Integer>();
		List<String> finalScore = new ArrayList<String>();
		
		ConfigurationSection score = this.playersFile.getConfigurationSection("Players"); 

		for(String playerName : score.getKeys(false)) {
		 
			int kills = score.getInt(playerName);
			
			scoreMap.put(playerName, kills);
		 
			
		}
		  
		String topName = "";
		
		int topScore = 0;
		 
		for(String playerName : scoreMap.keySet()) {
		 
		int myScore = scoreMap.get(playerName);
		 
			if(myScore > topScore) {
			 
				topName = playerName;
				topScore = myScore;
			 
			}
		 
		}
		 
		if(!topName.equals("")) {
		 
			scoreMap.remove(topName);
				 
			int kills = score.getInt(topName);
				 
			String finalString = ChatColor.DARK_AQUA + "The #1 zombie killer is " + ChatColor.AQUA + topName + ChatColor.DARK_AQUA + " with " + ChatColor.AQUA + kills + ChatColor.DARK_AQUA +  " kills";
				 
			finalScore.add(finalString);
			 
		}

		 
		myTop5 = finalScore;
		 
			for(String blah : myTop5){
				
				player.sendMessage(blah);
				
			}
	
		}
	
	public void loadPlayerYML(){
		pluginFolder = getDataFolder();
	    configFile = new File(getDataFolder(), "players.yml");
	    
	    playersFile = new YamlConfiguration();
	    
		if(getDataFolder().exists() == false){
			
			try{
				getDataFolder().mkdir();
			}catch (Exception ex){
				//something went wrong.
			}
			
		} //plugin folder exists
	
	
		if(configFile.exists() == false){
			
			try{
				configFile.createNewFile();
			}catch (Exception ex){
				//something went wrong.
			}
		} //Configfile exist's
		
		try{ //Load payers.yml
			playersFile.load(configFile);
		}catch (Exception ex){
			//Something went wrong
		} //End load players.yml
	}
	
	public void savePlayerYML(){
		
		try {
			playersFile.save(configFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public void msgPlayer(Player player, String message){
		
		player.sendMessage(ChatColor.DARK_AQUA + "[ZombieKillCount] " + ChatColor.AQUA + message);
		
	}
}
