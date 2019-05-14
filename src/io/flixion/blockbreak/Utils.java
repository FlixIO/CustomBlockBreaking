package io.flixion.blockbreak;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.comphenix.protocol.wrappers.BlockPosition;

public class Utils {
	private static Random rng = new Random();
	
	public static String addColor(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	
	public static String serializeLoc(Location loc) {
		return loc.getWorld() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
	}
	
	public static void sendBlockAnimationPacket(Player p, int stage, Block b) {
		WrapperPlayServerBlockBreakAnimation packet = new WrapperPlayServerBlockBreakAnimation();
		packet.setDestroyStage(stage);
		packet.setLocation(new BlockPosition(b.getLocation().toVector()));
		packet.setEntityID(rng.nextInt(2000));
		packet.sendPacket(p);
		WrapperPlayServerWorldEvent particles = new WrapperPlayServerWorldEvent();
		particles.setEffectId(2001);
		particles.setLocation(new BlockPosition(b.getLocation().toVector()));
		particles.setDisableRelativeVolume(false);
		particles.setData(b.getTypeId());
		particles.sendPacket(p);
	}
}
