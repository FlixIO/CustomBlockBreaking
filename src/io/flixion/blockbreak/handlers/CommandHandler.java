package io.flixion.blockbreak.handlers;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import io.flixion.blockbreak.BlockBreak;
import io.flixion.blockbreak.data.BlockData;

public class CommandHandler implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equals("rebuild")) {
			if (sender.hasPermission("customblockbreak.rebuild")) {
				for(Map.Entry<String, BlockData> entry : BlockBreak.getBlockManager().getLocationBlockMapping().entrySet()) {
					if (entry.getValue().originalType() == Material.WEB) {
						entry.getValue().destory();
					} else {
						entry.getValue().resetBlock();
					}
				}
				for (BlockData d : BlockBreak.getBlockManager().getOriginalData()) {
					if (d.originalType() == Material.WEB) {
						d.destory();
					} else {
						d.resetBlock();
					}
				}
				BlockBreak.getBlockManager().purgeData();
			}
		}
		return true;
	}
	
}
