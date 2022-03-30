package com.github.lorisdemicheli.inventory;

import java.io.Serializable;
import java.util.List;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.github.lorisdemicheli.inventory.util.BaseInventory;
import com.github.lorisdemicheli.inventory.util.CustomHead;

public abstract class PagedChestInventory<E extends Serializable> extends ChestInventory<E>{
	
	private List<E> elements;
	private int page = 0;
	
	private String nextPageText = "NEXT";
	private String previousPageText = "PREVIOUS";

	public PagedChestInventory(BaseInventory previous, int size) {
		super(previous, size);
	}

	public PagedChestInventory(Plugin plugin,int size) {
		super(plugin, size);
	}

	protected abstract List<E> listElements(HumanEntity human);

	protected abstract ItemStack itemList(HumanEntity human,E element);

	protected abstract void onItemListSelected(InventoryClickEvent event,E element);

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
		E element = getItemValue(item);
		if (element != null) {
			onItemListSelected(event, element);
		}
	}

	@Override
	public void beforePlaceItem(HumanEntity human) {
		elements = listElements(human);
		int realSize = getInventory().getSize();
		int size = realSize - 9;
		if(!validPosition(realSize)) {
			throw new IllegalArgumentException("Wrong position for one item");
		}
		if (elements != null) {
			int skip = page * size;
			if (skip + size < elements.size()) {
				setItem(realSize-1, setStringKey(getArrowRight(), "%freccia%destra%"));
			}
			if (page > 0) {
				setItem(realSize-9, setStringKey(getArrowLeft(), "%freccia%sinistra%"));
			}
			for (int i = 0; i < size; i++) {
				if (skip + i < elements.size()) {
					E current = elements.get(skip + i);
					setItem(i, itemList(human,current), current);
				}
			}
		}
	}
	
	private boolean validPosition(int size) {
		if(size==9) return false;
		
		return !getItems().keySet()
				.parallelStream()
				.anyMatch(i->(i < (size-10) || i == (size-9) || i == (size-1)));
		
	}

	protected ItemStack getArrowRight() {
		return CustomHead.arrowRight(nextPageText);
	}

	protected ItemStack getArrowLeft() {
		return CustomHead.arrowLeft(previousPageText);
	}

	public List<E> getElements() {
		return elements;
	}

	public String getPreviousPageText() {
		return previousPageText;
	}

	public void setPreviousPageText(String previousPageText) {
		this.previousPageText = previousPageText;
	}

	public String getNextPageText() {
		return nextPageText;
	}

	public void setNextPageText(String nextPageText) {
		this.nextPageText = nextPageText;
	}
}
