package org.github.lorisdemicheli.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.github.lorisdemicheli.inventory.util.Ask;
import org.github.lorisdemicheli.inventory.util.CustomHead;

public class InventoryAsk extends InventoryBase<String> {
	
	private ItemStack yes;
	private ItemStack no;
	private ItemStack question;
	private Ask resultAction;
	
	public InventoryAsk(Plugin plugin,String ask,Ask result) {
		super(plugin);
		setInventory(Bukkit.createInventory(this, 45, ask));
		this.resultAction = result;
		question = CustomHead.questionMark(ask);
	}

	@Override
	public void onItemSelected(InventoryClickEvent event, ItemStack item) {
		if(item.equals(yes)) {
			resultAction.onResult(true);
		} else if(item.equals(no)) {
			resultAction.onResult(false);
		}
		event.setCancelled(true);
	}
	
	@Override
	protected void placeItem(HumanEntity human) {
		setItem(10, getYes());
		setItem(11, getYes());
		setItem(12, getYes());
		setItem(19, getYes());
		setItem(20, getYes());
		setItem(21, getYes());
		setItem(28, getYes());
		setItem(29, getYes());
		setItem(30, getYes());
		
		setItem(22,question);
		
		setItem(14, getNo());
		setItem(15, getNo());
		setItem(16, getNo());
		setItem(23, getNo());
		setItem(24, getNo());
		setItem(25, getNo());
		setItem(32, getNo());
		setItem(33, getNo());
		setItem(34, getNo());
	}

	private ItemStack getYes() {
		if(yes == null) {
			ItemStack item = new ItemStack(Material.EMERALD_BLOCK);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName("YES");
			item.setItemMeta(meta);
			yes = item;
		}
		return yes;
	}
	
	private ItemStack getNo() {
		if(no == null) {
			ItemStack item = new ItemStack(Material.REDSTONE_BLOCK);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName("NO");
			item.setItemMeta(meta);
			no = item;
		}
		return no;
	}

	@Override
	protected boolean autoUpdate() {
		return false;
	}

	@Override
	protected String title() {
		return resultAction.getQuestion();
	}
}
