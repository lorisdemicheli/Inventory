package com.github.lorisdemicheli.inventory;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.lorisdemicheli.inventory.util.Ask;
import com.github.lorisdemicheli.inventory.util.BaseInventory;
import com.github.lorisdemicheli.inventory.util.CustomHead;

public class AskInventory extends ChestInventory<Boolean> {
	
	private ItemStack yes;
	private ItemStack no;
	private ItemStack question;
	private Ask ask;

	public AskInventory(BaseInventory previous,Ask ask) {
		super(previous,45);
		this.ask = ask;
	}

	@Override
	public void onItemSelected(InventoryClickEvent event, ItemStack item) {
		Boolean value = getItemValue(item);
		if(value != null) {
			ask.onResult(value);
		}
	}

	@Override
	public String title() {
		return ask.getQuestion();
	}

	@Override
	public void placeItem(HumanEntity human) {
		setItem(10, getItemYes(), true);
		setItem(11, getItemYes(), true);
		setItem(12, getItemYes(), true);
		setItem(19, getItemYes(), true);
		setItem(20, getItemYes(), true);
		setItem(21, getItemYes(), true);
		setItem(28, getItemYes(), true);
		setItem(29, getItemYes(), true);
		setItem(30, getItemYes(), true);
		
		setItem(22,getItemQuestion());
		
		setItem(14, getItemNo(), false);
		setItem(15, getItemNo(), false);
		setItem(16, getItemNo(), false);
		setItem(23, getItemNo(), false);
		setItem(24, getItemNo(), false);
		setItem(25, getItemNo(), false);
		setItem(32, getItemNo(), false);
		setItem(33, getItemNo(), false);
		setItem(34, getItemNo(), false);
	}
	
	protected ItemStack getItemYes() {
		if(yes == null) {
			ItemStack item = new ItemStack(Material.EMERALD_BLOCK);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName("YES");
			item.setItemMeta(meta);
			yes = item;
		}
		return yes;
	}
	
	protected ItemStack getItemNo() {
		if(no == null) {
			ItemStack item = new ItemStack(Material.REDSTONE_BLOCK);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName("NO");
			item.setItemMeta(meta);
			no = item;
		}
		return no;
	}
	
	protected ItemStack getItemQuestion() {
		if(question == null) {
			question = CustomHead.questionMark(ask.getQuestion());;
		}
		return question;
	}

}
