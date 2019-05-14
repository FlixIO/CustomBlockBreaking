package io.flixion.blockbreak.handlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.flixion.blockbreak.BlockBreak;

public class PlayerHandler implements Listener {
	//handle quit
	//handle other exp sources
	
	@EventHandler
	public void handleQuit(PlayerQuitEvent e) {
		if (BlockBreak.getBlockManager().getPlayerBlockMapping().containsKey(e.getPlayer().getUniqueId())) {
			BlockBreak.getBlockManager().getLocationBlockMapping().get(BlockBreak.getBlockManager().getPlayerBlockMapping().get(e.getPlayer().getUniqueId())).removePlayer(e.getPlayer().getUniqueId());;
			BlockBreak.getBlockManager().removeWatchingPlayer(e.getPlayer().getUniqueId());
		}
	}
	
	@EventHandler
	public void handleNaturalEXP(PlayerExpChangeEvent e) {
		if (BlockBreak.getBlockManager().getPlayerBlockMapping().containsKey(e.getPlayer().getUniqueId())) {
			e.setAmount(0);
		}
	}
}
