package com.github.lorisdemicheli.inventory;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import com.github.lorisdemicheli.inventory.custom.AskInventory;
import com.github.lorisdemicheli.inventory.custom.SearchInventory;
import com.github.lorisdemicheli.inventory.listener.InventoryListener;
import com.github.lorisdemicheli.inventory.util.Ask;
import com.github.lorisdemicheli.inventory.util.BaseInventory;
import com.github.lorisdemicheli.inventory.util.DataType;
import com.github.lorisdemicheli.inventory.util.StringSearch;

public abstract class ChestInventory<T extends Serializable> implements InventoryHolder,BaseInventory {

	private Inventory inventory;
	private Integer updateInvId;
	private NamespacedKey key;
	private NamespacedKey stringKey;
	private BaseInventory previous;
	private BaseInventory sub;
	private Map<Integer, ItemStack> items = Collections.synchronizedMap(new HashMap<>());
	private PersistentDataType<?, T> dataType;
	private int inventorySize;

	protected Plugin plugin;

	public ChestInventory(Plugin plugin) {
		this.plugin = plugin;
		InventoryListener.of(plugin);
		key = new NamespacedKey(plugin, "menu-item-T");
		stringKey = new NamespacedKey(plugin, "menu-item-string");
	}

	public ChestInventory(Plugin plugin, int size) {
		this(plugin);
		if(size % 9 != 0 || size > 54 || size < 9) {
			throw new IllegalArgumentException("Size " + size + " is not allowed");
		}
		this.inventorySize = size;
	}

	public ChestInventory(BaseInventory previous) {
		this(previous.getPlugin());
		this.previous = previous;
		this.previous.setSub(this);
	}

	public ChestInventory(BaseInventory previous, int size) {
		this(previous);
		this.inventorySize = size;
	}

	protected boolean autoUpdate() {
		return false;
	}
	
	@Override
	public boolean cancelledClick() {
		return true;
	}

	protected void updateAsync(HumanEntity human) {
		placeItem(human);
		updateAsyncBeforeSync(human);
		Bukkit.getServer().getScheduler().runTask(plugin, () -> updateSync(human));
	}

	protected void updateAsyncBeforeSync(HumanEntity human) {
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

	protected long periodTickUpdate() {
		return 10;
	}

	protected void updateSync(HumanEntity human) {
		if (items.size() > 0) {
			getInventory().clear();
			Set<Entry<Integer, ItemStack>> i = items.entrySet();
			Iterator<Entry<Integer, ItemStack>> value = i.iterator();
			while (value.hasNext()) {
				Entry<Integer, ItemStack> itempos = value.next();
				getInventory().setItem(itempos.getKey(), itempos.getValue());
			}
			clearItem();
		}
	}

	public void setItem(int index, ItemStack item) {
		items.put(index, item);
	}

	protected void setItem(int index, ItemStack item, T value) {
		setItem(index, setKey(item, value));
	}

	protected void clearItem() {
		items.clear();
	}

	protected Map<Integer, ItemStack> getItems() {
		return items;
	}

	protected ItemStack setKey(ItemStack item, T value) {
		ItemMeta meta = item.getItemMeta();
		if (dataType == null) {
			dataType = DataType.getType(value);
		}
		meta.getPersistentDataContainer().set(key, dataType, value);
		item.setItemMeta(meta);
		return item;
	}

	protected T getItemValue(ItemStack item) {
		if (dataType != null && item.getItemMeta().getPersistentDataContainer().has(key, dataType))
			return item.getItemMeta().getPersistentDataContainer().get(key, dataType);
		else
			return null;
	}

	protected ItemStack setStringKey(ItemStack item, String value) {
		ItemMeta meta = item.getItemMeta();
		meta.getPersistentDataContainer().set(stringKey, DataType.STRING, value);
		item.setItemMeta(meta);
		return item;
	}

	protected String getItemStringValue(ItemStack item) {
		if (item.getItemMeta().getPersistentDataContainer().has(stringKey, DataType.STRING))
			return item.getItemMeta().getPersistentDataContainer().get(stringKey, DataType.STRING);
		else
			return null;
	}

	protected final void ask(Ask ask, HumanEntity human) {
		AskInventory ai = new AskInventory(this, ask);
		close(human);
		ai.open(human);
	}

	protected final void search(StringSearch search, HumanEntity human) {
		SearchInventory si = new SearchInventory(this, search);
		close(human);
		si.open(human);
	}

	public void open(HumanEntity human) {
		if (inventory == null) {
			inventory = Bukkit.createInventory(this, inventorySize, title(human));
		}
		if (previous != null) {
			previous.close(human);
		}
		update(human);
		human.openInventory(inventory);
	}
	
	public void close(HumanEntity human) {
		human.closeInventory();
	}

	public void openPrevius(HumanEntity human) {
		human.closeInventory();
		previous.open(human);
	}

	public void onClose() {
		if (autoUpdate()) {
			Bukkit.getServer().getScheduler().cancelTask(updateInvId);
		}
	}

	@Override
	public final Inventory getInventory() {
		return inventory;
	}

	protected final void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	protected final NamespacedKey getStringKey() {
		return stringKey;
	}

	protected final NamespacedKey getKey() {
		return key;
	}

	protected final PersistentDataType<?, T> getDataType() {
		return dataType;
	}

	public Plugin getPlugin() {
		return plugin;
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
}
