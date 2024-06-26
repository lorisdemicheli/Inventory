package io.github.lorisdemicheli.inventory.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import io.github.lorisdemicheli.inventory.util.BaseInventory;


public class InventoryListener implements Listener {

	private static InventoryListener listener;
		
	public synchronized static InventoryListener of(Plugin plugin) {
		if(listener == null) {
			listener = new InventoryListener(plugin);
		}
		return listener;
	}
	
	public InventoryListener(Plugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Inventory inv = event.getClickedInventory();
		if (inv == null) {
			return;
		}

		InventoryHolder holder = event.getClickedInventory().getHolder();
		
		if (holder instanceof BaseInventory) {
			BaseInventory base = (BaseInventory) holder;
			ItemStack item = event.getCurrentItem();
			
			if (item != null && !item.getType().equals(Material.AIR)) {			
				base.onItemSelected(event, item);
				event.setCancelled(base.cancelledClick());
			}
			
			return;
		}
	}

	@EventHandler
	public void onInventoryCloseEvent(InventoryCloseEvent event) {
		InventoryHolder holder = event.getInventory().getHolder();
		if (holder instanceof BaseInventory) {
			BaseInventory base = (BaseInventory) holder;
			base.onClose();
		}
	}

	@EventHandler
	public void onPlayerAnimationEvent(PlayerAnimationEvent event) {
		if (event.getPlayer().getOpenInventory().getTopInventory().getHolder() instanceof BaseInventory) {
			event.setCancelled(true);
		}
	}
}
