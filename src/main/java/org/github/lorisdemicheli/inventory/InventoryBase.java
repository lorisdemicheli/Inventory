package org.github.lorisdemicheli.inventory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.github.lorisdemicheli.inventory.util.Ask;
import org.github.lorisdemicheli.inventory.util.DataType;
import org.github.lorisdemicheli.inventory.util.ReflectionUtils;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftContainer;

import net.minecraft.server.v1_16_R3.ChatComponentText;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.PacketPlayOutOpenWindow;

public abstract class InventoryBase<T> implements InventoryHolder {

	private Inventory inventory;
	private int updateInvId;
	private NamespacedKey key;
	private NamespacedKey stringKey;
	private InventoryBase<?> sub;
	private InventoryBase<?> previous;
	private Map<Integer, ItemStack> items = new HashMap<>();
	private PersistentDataType<?, T> dataType;

	protected final Map<String, String> placeHolder = new HashMap<>();

	protected Plugin plugin;

	public InventoryBase(Plugin plugin) {
		this.plugin = plugin;
		key = new NamespacedKey(plugin, "menu-item");
		stringKey = new NamespacedKey(plugin, "menu-item-string");
	}

	public InventoryBase(Plugin plugin, Inventory inventory) {
		this(plugin);
		this.inventory = inventory;
	}

	public InventoryBase(Plugin plugin, InventoryBase<?> previous) {
		this(plugin, previous.getInventory());
		previous.setSub(this);
		this.previous = previous;
		renameInventory();
	}

	protected abstract void onItemSelected(InventoryClickEvent event, ItemStack item);

	protected abstract boolean autoUpdate();

	protected abstract String title();

	protected abstract void placeItem(HumanEntity human);

	public void previusInv(HumanEntity human) {
		previous.setSub(null);
		previous.renameInventory();
		previous.update(human);
	}

	public void openPreviousInv(HumanEntity human) {
		previous.setSub(null);
		previous.open(human);
		previous.renameInventory();
	}

	protected void updateAsync(HumanEntity human) {
		placeItem(human);
		Bukkit.getServer().getScheduler().runTask(plugin, () -> updateSync(human));
	}

	public final void update(HumanEntity human) {
		if (sub != null) {
			sub.update(human);
		} else {
			Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> updateAsync(human));
		}
	}

	protected void updateSync(HumanEntity human) {
		getInventory().clear();
		Set<Entry<Integer, ItemStack>> i = items.entrySet();
		Iterator<Entry<Integer, ItemStack>> value = i.iterator();
		while (value.hasNext()) {
			Entry<Integer, ItemStack> itempos = value.next();
			getInventory().setItem(itempos.getKey(), itempos.getValue());
		}
		clearItem();
	}

	protected void setItem(int index, ItemStack item) {
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

	protected final void ask(Ask ask, HumanEntity owner) {
		InventoryAsk ia = new InventoryAsk(plugin, ask.getQuestion(), ask);
		ia.open(owner);
	}

	public void open(HumanEntity human) {
		if (previous == null) {
			if (autoUpdate()) {
				updateInvId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> update(human),
						0, 10);
			} else {
				update(human);
			}
			human.openInventory(inventory);
		} else {
			previous.open(human);
		}
	}

	public void onClose() {
		if (autoUpdate()) {
			Bukkit.getServer().getScheduler().cancelTask(updateInvId);
		}
	}

	public final void onItemSelectedChoice(InventoryClickEvent event, ItemStack item) {
		if (sub != null) {
			sub.onItemSelectedChoice(event, item);
		} else {
			onItemSelected(event, item);
		}
		event.setCancelled(true);
	}

	protected void renameInventory() {
		Player player = (Player) inventory.getViewers().get(0);
		try {
			Object entityPlayer = ReflectionUtils.callMethod(player, "getHandle");
			Object activeContainer = ReflectionUtils.fieldValue(entityPlayer, "activeContainer");
			Object windowId = ReflectionUtils.fieldValue(activeContainer, "windowId");
			Object craftContainer = ReflectionUtils.callStaticMethod(ReflectionUtils.getCBVClass("inventory.CraftContainer"),
					"getNotchInventoryType",inventory);
			Object chat = ReflectionUtils.newInstance(ReflectionUtils.getMVClass("ChatComponentText"),replacePlaceHolder(title()));
			Object packet = ReflectionUtils.newInstance(ReflectionUtils.getMVClass("PacketPlayOutOpenWindow"), windowId,
					craftContainer, chat);
			Object playerConnection = ReflectionUtils.fieldValue(entityPlayer, "playerConnection");
			ReflectionUtils.callMethod(playerConnection, "sendPacket", packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected String replacePlaceHolder(String str) {
		Iterator<Entry<String, String>> row = placeHolder.entrySet().iterator();
		while (row.hasNext()) {
			Entry<String, String> value = row.next();
			if (str.contains(value.getKey())) {
				str = str.replace(value.getKey(), value.getValue());
			}
		}
		return str;
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	protected final void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	public InventoryBase<?> getPrevious() {
		return previous;
	}

	public void setPrevious(InventoryBase<?> previous) {
		this.previous = previous;
	}

	public InventoryBase<?> getSub() {
		return sub;
	}

	public void setSub(InventoryBase<?> sub) {
		this.sub = sub;
	}

	protected NamespacedKey getStringKey() {
		return stringKey;
	}
}
