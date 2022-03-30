package com.github.lorisdemicheli.inventory;

import java.io.Serializable;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import com.github.lorisdemicheli.inventory.util.BaseInventory;
import com.github.lorisdemicheli.inventory.util.DataType;

public abstract class ChestInventory<T extends Serializable> extends AbstractBaseInventory {

	private NamespacedKey key;
	private NamespacedKey stringKey;
	private PersistentDataType<?, T> dataType;
	private int size = 27;

	public ChestInventory(Plugin plugin) {
		super(plugin);
		key = new NamespacedKey(plugin, "menu-item-T");
		stringKey = new NamespacedKey(plugin, "menu-item-string");
	}

	public ChestInventory(Plugin plugin, int size) {
		this(plugin);
		this.size = size;
	}

	public ChestInventory(BaseInventory previous) {
		super(previous);
		key = new NamespacedKey(getPlugin(), "menu-item-T");
		stringKey = new NamespacedKey(getPlugin(), "menu-item-string");
	}

	public ChestInventory(BaseInventory previous, int size) {
		this(previous);
		this.size = size;
	}

	protected boolean autoUpdate() {
		return false;
	}

	protected long periodTickUpdate() {
		return 0;
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
	
	protected void setItem(int index, ItemStack item, T value) {
		setItem(index, setKey(item, value));
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
	
	@Override
	protected Inventory createInventory(HumanEntity human) {
		if(size % 9 != 0 || size > 54 || size < 9) {
			throw new IllegalArgumentException("Size " + size + " is not allowed");
		}
		return Bukkit.createInventory(this, size, title(human));
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
}
