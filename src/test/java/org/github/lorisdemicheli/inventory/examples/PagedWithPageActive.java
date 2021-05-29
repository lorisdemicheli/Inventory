package org.github.lorisdemicheli.inventory.examples;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.github.lorisdemicheli.inventory.InventoryBase;
import org.github.lorisdemicheli.inventory.PagedInventory;
import org.github.lorisdemicheli.inventory.util.ItemUtil;

public class PagedWithPageActive extends PagedInventory<Integer>{

	public PagedWithPageActive(Plugin plugin, InventoryBase<?> prevoius) {
		super(plugin, prevoius);
	}

	@Override
	protected List<Integer> updateElements(HumanEntity human) {
		List<Integer> list = new ArrayList<>();
		for(int i=0;i<100;i++) {
			list.add(i);
		}
		return list;
	}

	@Override
	protected ItemStack itemList(Integer element) {
		return ItemUtil.basicItem(Material.APPLE, "N."+element, 1);
	}

	@Override
	protected void onItemSelectedList(Integer element, InventoryClickEvent event) {
		event.getWhoClicked().sendMessage("Number selected: " + element);
	}

	@Override
	protected boolean autoUpdate() {
		return false;
	}

	@Override
	protected String title() {
		return "Number";
	}

	@Override
	protected void placeItem(HumanEntity human) {
		setItem(49, setStringKey(ItemUtil.basicItem(Material.LOOM, "Back", 1), "back"));
	}

	@Override
	public void onItemSelected(InventoryClickEvent event, ItemStack item) {
		super.onItemSelected(event, item);
		if (getItemStringValue(item) != null) {
			switch (getItemStringValue(item)) {
			case "back":
				previusInv(event.getWhoClicked());
				break;
			}
		}
	}
}
