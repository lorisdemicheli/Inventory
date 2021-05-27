package org.github.lorisdemicheli.inventory.examples;

import java.util.ArrayList;
import java.util.List;
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

public class PagedImplementationV2 extends PagedInventory<PlayerType> {

	public PagedImplementationV2(Plugin plugin, InventoryBase<?> prevoius) {
		super(plugin, prevoius);
	}

	@Override
	protected List<PlayerType> updateElements(HumanEntity human) {
		List<PlayerType> online = new ArrayList<>();
		for (Player player : Bukkit.getOnlinePlayers()) {
			online.add(new PlayerType(player));
		}
		return online;
	}

	@Override
	protected ItemStack itemList(PlayerType element) {
		//return ItemUtil.loreItem(Skull.getPlayerSkull(element.getPlayer()), element.getPlayer().getName(),1);
		//return ItemUtil.loreItem(SkullV2.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjBhMGFjNjgwNjcwNGQ5OWJmZWMxNTBhZGZjNzlkMmY1NTI4NmY1ODgzNjA0MzkzM2Q4MWVkOWY4MGMwYmExOSJ9fX0="), element.getPlayer().getName(),1);
		//return SkullV2.create(null,"ewogICJ0aW1lc3RhbXAiIDogMTYyMjEzNDgyODk5OCwKICAicHJvZmlsZUlkIiA6ICI3ZjUxODllYjI2MTk0MzBjYmM4OGU4NGY4ZGYwNTU3OCIsCiAgInByb2ZpbGVOYW1lIiA6ICJ4TG9yaXM5OSIsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS85NDQ3ZGFjZjc5NzZhODQwYWZmY2E1NTFkODE1OTgwMzViNzg2YjIwNjdhYTVkMjIzMTc1OTdmNmEwMDk4MzM1IgogICAgfQogIH0KfQ==");
		return Skull.getHead(element.getPlayer());
	}

	@Override
	protected void onItemSelectedList(PlayerType element, InventoryClickEvent event) {
		Player owner = (Player) event.getWhoClicked();
		ask(new Ask("Say hello to " + element.getPlayer().getName()) {
			@Override
			public void onResult(boolean result) {
				if (result) {
					element.getPlayer().sendMessage(owner.getDisplayName() + " say hello!");
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
		super.placeItem(human);
	}

}
