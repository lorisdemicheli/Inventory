package com.github.lorisdemicheli.inventory.custom;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.github.lorisdemicheli.inventory.AnvilInventory;
import com.github.lorisdemicheli.inventory.util.BaseInventory;
import com.github.lorisdemicheli.inventory.util.ItemUtil;
import com.github.lorisdemicheli.inventory.util.StringSearch;

public class SearchInventory extends AnvilInventory {
	
	private StringSearch search;

	public SearchInventory(BaseInventory previous,StringSearch search) {
		super(previous);
		this.search = search;
	}

	@Override
	public void placeItem(HumanEntity human) {
		setItem(0, ItemUtil.basicItem(Material.PAPER, search.getTitle(), 1));
	}

	@Override
	public String title(HumanEntity human) {
		return search.getTitle();
	}

	@Override
	public void onItemSelected(InventoryClickEvent event, ItemStack item) {
		if(event.getRawSlot() == 2) {
			search.onResult(item.getItemMeta().getDisplayName());
		}
	}	
	
	@Override
	public BaseInventory getSub() {
		throw new UnsupportedOperationException("Sub inventory not supported");
	}

	@Override
	public void setSub(BaseInventory sub) {
		throw new UnsupportedOperationException("Sub inventory not supported");
	}
}
