package io.flixion.blockbreak.data;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import io.flixion.blockbreak.BlockBreak;
import io.flixion.blockbreak.Utils;

public class BlockData implements Runnable {
	private Block b;
	private float ticksToBreak;
	private Set<UUID> playerInteractions = new HashSet<>();
	private BukkitTask blockManager;
	private float percentageBroken = 0;
	private float currentTickDamage = 0;
	private Sound soundType;
	private Material materialType;
	public BlockData(Block b) {
		super();
		this.b = b;
		materialType = b.getType();
		switch(b.getType()) {
			case WEB: ticksToBreak = 60; soundType = Sound.BLOCK_STONE_BREAK; break;
			case GLASS: ticksToBreak = 100; soundType = Sound.BLOCK_GLASS_BREAK; break;
			case BIRCH_FENCE: ticksToBreak = 200; soundType = Sound.BLOCK_WOOD_BREAK; break;
			case JUNGLE_FENCE: ticksToBreak = 400; soundType = Sound.BLOCK_WOOD_BREAK; break;
			case FENCE: ticksToBreak = 600; soundType = Sound.BLOCK_WOOD_BREAK; break;
		}
		for (UUID u : playerInteractions) {
			Bukkit.getPlayer(u).setExp(0.99F);
		}
		blockManager = Bukkit.getScheduler().runTaskTimer(BlockBreak.getPL(), this, 0, 10);
	}
	
	@Override
	public void run() {
		percentageBroken = currentTickDamage / ticksToBreak;
		doUpdate();
	}
	
	private void doUpdate() {
		if (currentTickDamage < ticksToBreak) {
			if (playerInteractions.size() > 0) {
				Set<UUID> toRemove = null;
				for (UUID u : playerInteractions) {
					Player p = Bukkit.getPlayer(u);
					if (Bukkit.getPlayer(u) == null) {
						continue;
					}
					if (p.getLocation().distance(b.getLocation()) > 3) {
						if (toRemove == null) {
							toRemove = new HashSet<>();
						}
						toRemove.add(p.getUniqueId());
						p.sendMessage(Utils.addColor("&cYou are too far away to break this block"));
						resetPlayer(p);
					} else {
						p.setExp(0.99F - percentageBroken);
					}
				}
				for (Player p : Bukkit.getOnlinePlayers()) {
					Utils.sendBlockAnimationPacket(p, (int) 10, b);
					Utils.sendBlockAnimationPacket(p, (int) (percentageBroken * 9), b);
					p.playSound(b.getLocation(), soundType, 4, 0);
				}
				if (toRemove != null) {
					playerInteractions.removeAll(toRemove);
				}
				currentTickDamage += playerInteractions.size() * 10;
			}
		} else { //block to be broken
			b.setType(Material.AIR);
			for (UUID u : playerInteractions) {
				Player p = Bukkit.getPlayer(u);
				if (Bukkit.getPlayer(u) == null) {
					continue;
				}
				resetPlayer(p);
			}
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.playSound(b.getLocation(), soundType, 4, 0);
				Utils.sendBlockAnimationPacket(p, (int) 10, b);
			}
			BlockBreak.getBlockManager().removeBlock(Utils.serializeLoc(b.getLocation()));
			BlockBreak.getBlockManager().addOriginalBlockdata(this);
			blockManager.cancel();
		}
	}
	
	public void resetBlock() {
		b.setType(materialType);
		if (blockManager != null) {
			blockManager.cancel();
		}
		for (UUID u : playerInteractions) {
			Player p = Bukkit.getPlayer(u);
			if (Bukkit.getPlayer(u) == null) {
				continue;
			}
			resetPlayer(p);
			Utils.sendBlockAnimationPacket(p, (int) 10, b);
		}
	}
	
	private void resetPlayer(Player p) {
		p.setExp(0);
		Utils.sendBlockAnimationPacket(p, 10, b);
		BlockBreak.getBlockManager().removeWatchingPlayer(p.getUniqueId());
	}
	
	public void cancel() {
		if (blockManager != null) {
			blockManager.cancel();
		}
	}
	
	public Block getB() {
		return b;
	}
	public void addPlayer(UUID u) {
		playerInteractions.add(u);
	}
	public void removePlayer(UUID u) {
		playerInteractions.remove(u);
		if (Bukkit.getPlayer(u) != null) {
			resetPlayer(Bukkit.getPlayer(u));
		}
	}
	public void destory() {
		if (currentTickDamage > 0 && currentTickDamage != ticksToBreak) {
			b.setType(Material.AIR);
		}
	}
	
	public boolean isBroken() {
		if (currentTickDamage >= ticksToBreak) {
			return true;
		}
		return false;
	}
	
	public Material originalType() {
		return materialType;
	}
}
