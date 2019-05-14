package io.flixion.blockbreak;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import io.flixion.blockbreak.data.BlockData;
import io.flixion.blockbreak.data.BlockManager;
import io.flixion.blockbreak.handlers.CommandHandler;
import io.flixion.blockbreak.handlers.PlayerHandler;

public class BlockBreak extends JavaPlugin {
	private static BlockBreak instance;
	private static BlockManager blockManager;
	
	public static BlockBreak getPL() { return instance; }
	
	public static BlockManager getBlockManager() { return blockManager; } 
	
	public void onEnable() {
		instance = this;
		blockManager = new BlockManager();
		
		Bukkit.getPluginManager().registerEvents(blockManager, this);
		Bukkit.getPluginManager().registerEvents(new PlayerHandler(), this);
		getCommand("rebuild").setExecutor(new CommandHandler());
	}
	
	public void onDisable() {
		for (Map.Entry<String, BlockData> entry : blockManager.getLocationBlockMapping().entrySet()) {
			entry.getValue().cancel();
		}
	}
}
