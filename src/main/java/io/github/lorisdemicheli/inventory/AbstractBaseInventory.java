package io.github.lorisdemicheli.inventory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import io.github.lorisdemicheli.inventory.custom.AskInventory;
import io.github.lorisdemicheli.inventory.custom.SearchInventory;
import io.github.lorisdemicheli.inventory.listener.InventoryListener;
import io.github.lorisdemicheli.inventory.util.Ask;
import io.github.lorisdemicheli.inventory.util.BaseInventory;
import io.github.lorisdemicheli.inventory.util.StringSearch;

public abstract class AbstractBaseInventory implements BaseInventory {
	
	private Integer updateInvId;
	private Plugin plugin;
	private BaseInventory previous;
	private BaseInventory sub;
	private Inventory inventory;
	
	private Map<Integer, ItemStack> items = Collections.synchronizedMap(new HashMap<>());
	
	public AbstractBaseInventory(Plugin plugin) {
		this.plugin = plugin;
		InventoryListener.of(plugin);
	}

	public AbstractBaseInventory(BaseInventory previous) {
		this(previous.getPlugin());
		this.previous = previous;
		this.previous.setSub(this);
	}
	
	protected abstract Inventory createInventory(HumanEntity human);
	
	protected abstract long periodTickUpdate();
	
	protected abstract boolean autoUpdate();

	@Override
	public Inventory getInventory() {
		return inventory;
	}
	
	@Override
	public void setItem(int index, ItemStack item) {
		items.put(index, item);
	}
	
	public void clearItem() {
		items.clear();
	}
	
	public Map<Integer, ItemStack> getItems() {
		return items;
	}

	@Override
	public void close(HumanEntity human) {
		human.closeInventory();
	}
	
	@Override
	public boolean cancelledClick() {
		return true;
	}

	@Override
	public BaseInventory getSub() {
		return sub;
	}

	@Override
	public void setSub(BaseInventory sub) {
		this.sub = sub;
	}

	@Override
	public BaseInventory getPrevious() {
		return previous;
	}

	@Override
	public void setPrevious(BaseInventory previous) {
		this.previous = previous;
	}

	@Override
	public Plugin getPlugin() {
		return plugin;
	}

	@Override
	public void openPrevius(HumanEntity human) {
		previous.open(human);
	}

	@Override
	public void open(HumanEntity human) {
		beforeOpen(human);
		human.openInventory(inventory);
	}
	
	protected void beforeOpen(HumanEntity human) {
		if (inventory == null) {
			inventory = createInventory(human);
		}
		if (previous != null) {
			previous.close(human);
		}
		if (sub != null) {
			sub.close(human);
			sub = null;
		}
		update(human);
	}
	
	protected void beforePlaceItem(HumanEntity human) {}
	
	@Override
	public void placeItem(HumanEntity human) {}

	protected void updateAsync(HumanEntity human) {
		placeItem(human);
		beforePlaceItem(human);
		Bukkit.getServer().getScheduler().runTask(plugin, () -> updateSync(human));
	}

	public final void update(HumanEntity human) {
		if (autoUpdate() && updateInvId == null) {
			updateInvId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> update(human), 0,
					periodTickUpdate());
		} else {
			Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
				try {
					updateAsync(human);
				} catch (Exception e) {
					onClose();
					throw e;
				}
			});
		}
	}

	protected void updateSync(HumanEntity human) {
		if (items.size() > 0) {
			inventory.clear();
			//verifica parallel stream se va con multithread
			items.entrySet()
				.parallelStream()
				.forEach(item->inventory.setItem(item.getKey(), item.getValue()));
			clearItem();
		}
	}
	
	@Override
	public void onClose() {
		if (autoUpdate()) {
			Bukkit.getServer().getScheduler().cancelTask(updateInvId);
		}
	}
	
	protected final void ask(Ask ask, HumanEntity human) {
		AskInventory ai = new AskInventory(this, ask);
		ai.open(human);
	}

	protected final void search(StringSearch search, HumanEntity human) {
		SearchInventory si = new SearchInventory(this, search);
		si.open(human);
	}
}
