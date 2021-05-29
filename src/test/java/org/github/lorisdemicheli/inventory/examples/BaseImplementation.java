package org.github.lorisdemicheli.inventory.examples;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.github.lorisdemicheli.inventory.InventoryBase;
import org.github.lorisdemicheli.inventory.util.ItemUtil;

public class BaseImplementation extends InventoryBase<String>{

	public BaseImplementation(Plugin plugin) {
		super(plugin);
		setInventory(Bukkit.createInventory(this, 54,"Test"));
	}

	@Override
	protected void onItemSelected(InventoryClickEvent event, ItemStack item) {
		switch (getItemValue(item)) {
		case "greet":
			event.getWhoClicked().sendMessage("You clicked");
			break;
		case "paged":
			InventoryBase<?> inv = new PagedImplementation(plugin,this);
			inv.update(event.getWhoClicked());
			break;
		case "pagedV2":
			InventoryBase<?> invV2 = new PagedImplementationV2(plugin,this);
			invV2.update(event.getWhoClicked());
			break;
		case "number":
			InventoryBase<?> invNumber = new PagedWithPageActive(plugin,this);
			invNumber.update(event.getWhoClicked());
			break;
		}
	}

	@Override
	protected boolean autoUpdate() {
		return false;
	}

	@Override
	protected String title() {
		return "Test";
	}

	@Override
	protected void placeItem(HumanEntity human) {
		setItem(0, ItemUtil.basicItem(Material.POPPY, "Click for a greeting", 1), "greet");
		setItem(1, ItemUtil.loreItem(Material.CAKE, "Click", 1,Arrays.asList("Open","paged","inventory")), "paged");
		setItem(2, ItemUtil.loreItem(Material.CAKE, "Click", 1,Arrays.asList("Open","paged","inventory","V2")), "pagedV2");
		setItem(3, ItemUtil.loreItem(Material.CAKE, "Click", 1,Arrays.asList("Number")), "number");
	}

}
