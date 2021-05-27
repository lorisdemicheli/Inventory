package org.github.lorisdemicheli.inventory.examples;

import java.lang.instrument.IllegalClassFormatException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.github.lorisdemicheli.inventory.InventoryBase;
import org.github.lorisdemicheli.inventory.listener.InventoryListener;
import org.github.lorisdemicheli.inventory.util.DataType;

public class Main extends JavaPlugin {

	@Override
	public void onEnable() {
		try {
			DataType.registerType(PlayerType.class);
		} catch (IllegalClassFormatException e) {
			e.printStackTrace();
		}
		InventoryListener.of(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equals("test")) {
			InventoryBase<?> base = new BaseImplementation(this);
			base.open((Player)sender);
		}
		return true;
	}
}
