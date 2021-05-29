package org.github.lorisdemicheli.inventory.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.github.lorisdemicheli.inventory.InventoryBase;
import org.github.lorisdemicheli.inventory.PagedInventory;
import org.github.lorisdemicheli.inventory.util.Ask;
import org.github.lorisdemicheli.inventory.util.ItemUtil;
import org.github.lorisdemicheli.inventory.util.Skull;

public class PagedImplementation extends PagedInventory<String> {

	public PagedImplementation(Plugin plugin, InventoryBase<?> prevoius) {
		super(plugin, prevoius);
	}

	@Override
	protected List<String> updateElements(HumanEntity human) {
		List<String> online = new ArrayList<>();
		for (Player player : Bukkit.getOnlinePlayers()) {
			online.add(player.getUniqueId().toString());
		}
		return online;
	}

	@Override
	protected ItemStack itemList(String element) {
		Player player = Bukkit.getPlayer(UUID.fromString(element));
		return Skull.getHead(player);
	}

	@Override
	protected void onItemSelectedList(String element, InventoryClickEvent event) {
		Player player = Bukkit.getPlayer(UUID.fromString(element));
		Player owner = (Player) event.getWhoClicked();
		ask(new Ask("Say hello to " + player.getName()) {
			@Override
			public void onResult(boolean result) {
				if (result) {
					player.sendMessage(owner.getDisplayName() + " say hello!");
				}
				open(event.getWhoClicked());
			}
		}, owner);
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

	@Override
	protected boolean autoUpdate() {
		return true;
	}

	@Override
	protected String title() {
		return "Online player";
	}

	@Override
	protected void placeItem(HumanEntity human) {
		setItem(49, setStringKey(ItemUtil.basicItem(Material.LOOM, "Back", 1), "back"));
	}

}
