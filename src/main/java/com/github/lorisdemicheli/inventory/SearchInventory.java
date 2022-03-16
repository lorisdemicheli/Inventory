package com.github.lorisdemicheli.inventory;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.lorisdemicheli.inventory.util.BaseInventory;
import com.github.lorisdemicheli.inventory.util.StringSearch;

public class SearchInventory extends AnvilInventory{
	
	private StringSearch search;

	public SearchInventory(BaseInventory previous,StringSearch search) {
		super(previous);
		this.search = search;
	}

	@Override
	public void placeItem(HumanEntity human) {
		ItemStack item = new ItemStack(Material.PAPER);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(search.getTitle());
		item.setItemMeta(meta);
		setItem(0, item);
	}

	@Override
	public String title() {
		return search.getTitle();
	}

	@Override
	public void onItemSelected(InventoryClickEvent event, ItemStack item) {
		if(event.getRawSlot() == 2 && !item.getType().equals(Material.AIR)) {
			search.onResult(item.getItemMeta().getDisplayName());
		}
	}	
}
