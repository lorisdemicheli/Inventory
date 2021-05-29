package org.github.lorisdemicheli.inventory;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.github.lorisdemicheli.inventory.util.CustomHead;


public abstract class PagedInventory<E> extends InventoryBase<E> {

	private List<E> elements;
	private int page = 0;

	public PagedInventory(Plugin plugin) {
		super(plugin);
	}

	public PagedInventory(Plugin plugin, Inventory inv) {
		super(plugin, inv);
	}
	
	public PagedInventory(Plugin plugin, InventoryBase<?> prevoius) {
		super(plugin, prevoius);
	}

	protected abstract List<E> updateElements(HumanEntity human);

	protected abstract ItemStack itemList(E element);

	protected abstract void onItemSelectedList(E element, InventoryClickEvent event);

	@Override
	public void onItemSelected(InventoryClickEvent event, ItemStack item) {
		String value = getItemStringValue(item);
		if (value != null) {
			if (value.equals("%freccia%destra%")) {
				page++;
				update(event.getWhoClicked());
			} else if (value.equals("%freccia%sinistra%")) {
				page--;
				update(event.getWhoClicked());
			}
		}
		E val = getItemValue(item);
		if (val != null) {
			onItemSelectedList(val, event);
		}
	}

	@Override
	protected void updateAsyncBeforeSync(HumanEntity human) {
		elements = updateElements(human);
		int size = getInventory().getSize() - 9;
		if(checkItem(size)) {
			throw new IllegalArgumentException("Wrong position for one or more item");
		}
		if (elements != null) {
			int skip = page * size;
			if (skip + size < elements.size()) {
				setItem(53, setStringKey(getArrowRight(), "%freccia%destra%"));
			}
			if (page > 0) {
				setItem(45, setStringKey(getArrowLeft(), "%freccia%sinistra%"));
			}
			for (int i = 0; i < size; i++) {
				if (skip + i < elements.size()) {
					E current = elements.get(skip + i);
					setItem(i, itemList(current), current);
				}
			}
		}
	}
	
	private boolean checkItem(int size) {
		Iterator<Entry<Integer, ItemStack>> value = getItems().entrySet().iterator();
		while (value.hasNext()) {
			Entry<Integer, ItemStack> itempos = value.next();
			if(itempos.getKey() < size || itempos.getKey() == 45 || itempos.getKey() == 53) {
				return true;
			}
		}
		return false;
	}

	private ItemStack getArrowRight() {
		return CustomHead.arrowRight("NEXT");
	}

	private ItemStack getArrowLeft() {
		return CustomHead.arrowLeft("BACK");
	}

	public List<E> getElements() {
		return elements;
	}
}
