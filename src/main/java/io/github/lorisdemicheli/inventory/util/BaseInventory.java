package io.github.lorisdemicheli.inventory.util;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public interface BaseInventory extends InventoryHolder{

	void placeItem(HumanEntity human);
	String title(HumanEntity human);
	void onItemSelected(InventoryClickEvent event, ItemStack item);
	void setItem(int index, ItemStack item);
	void open(HumanEntity human);
	void close(HumanEntity human);
	void onClose();
	BaseInventory getSub();
	void setSub(BaseInventory sub);
	BaseInventory getPrevious();
	void setPrevious(BaseInventory previous);
	Plugin getPlugin();
	void openPrevius(HumanEntity human);
	boolean cancelledClick();
}
