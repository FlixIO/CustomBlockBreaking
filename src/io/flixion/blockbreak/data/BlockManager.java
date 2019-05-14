package io.flixion.blockbreak.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import io.flixion.blockbreak.Utils;

public class BlockManager implements Listener {
	private Map<String, BlockData> locationBlockMapping = new HashMap<>();
	private Map<UUID, String> playerBlockMapping = new HashMap<>();
	private Set<Material> affectedBlocks = new HashSet<>();
	private Set<BlockData> originalBlockData = new HashSet<>();
	
	public BlockManager() {
		affectedBlocks.add(Material.WEB);
		affectedBlocks.add(Material.GLASS);
		affectedBlocks.add(Material.FENCE);
		affectedBlocks.add(Material.JUNGLE_FENCE);
		affectedBlocks.add(Material.BIRCH_FENCE);
	}
	
	@EventHandler
	public void handleBlockInit(PlayerInteractEvent e) {
		if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
			Player p = e.getPlayer();
			if (p.getGameMode() == GameMode.SURVIVAL) {
				if (affectedBlocks.contains(e.getClickedBlock().getType())) {
					e.setCancelled(true);
					if (playerBlockMapping.containsKey(p.getUniqueId())) { //Player is currently modifiying a block
						if (!Utils.serializeLoc(e.getClickedBlock().getLocation()).equals(playerBlockMapping.get(p.getUniqueId()))) {
							locationBlockMapping.get(playerBlockMapping.get(p.getUniqueId())).removePlayer(p.getUniqueId());
							if (locationBlockMapping.containsKey(Utils.serializeLoc(e.getClickedBlock().getLocation()))) {
								locationBlockMapping.get(Utils.serializeLoc(e.getClickedBlock().getLocation())).addPlayer(p.getUniqueId());
								playerBlockMapping.put(p.getUniqueId(), Utils.serializeLoc(e.getClickedBlock().getLocation()));
							} else {
								initPlayerBlockInteraction(p, e.getClickedBlock());
							}
						}
					} else {
						if (locationBlockMapping.containsKey(Utils.serializeLoc(e.getClickedBlock().getLocation()))) {
							locationBlockMapping.get(Utils.serializeLoc(e.getClickedBlock().getLocation())).addPlayer(p.getUniqueId());
							playerBlockMapping.put(p.getUniqueId(), Utils.serializeLoc(e.getClickedBlock().getLocation()));
						} else {
							initPlayerBlockInteraction(p, e.getClickedBlock());
						}
					}
				}
			}
		}
	}
	
	private void initPlayerBlockInteraction(Player p, Block b) {
		BlockData bData = new BlockData(b);
		String loc = Utils.serializeLoc(b.getLocation());
		bData.addPlayer(p.getUniqueId());
		locationBlockMapping.put(loc, bData);
		playerBlockMapping.put(p.getUniqueId(), loc);
	}
	
	public void removeWatchingPlayer(UUID u) {
		playerBlockMapping.remove(u);
	}
	
	public void removeBlock(String s) {
		locationBlockMapping.remove(s);
	}

	public Map<String, BlockData> getLocationBlockMapping() {
		return locationBlockMapping;
	}

	public Map<UUID, String> getPlayerBlockMapping() {
		return playerBlockMapping;
	}
	
	public Set<BlockData> getOriginalData() {
		return originalBlockData;
	}
	
	public void addOriginalBlockdata(BlockData b) {
		originalBlockData.add(b);
	}
	
	public void purgeData() {
		playerBlockMapping.clear();
		locationBlockMapping.clear();
		originalBlockData.clear();
	}
}
