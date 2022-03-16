package com.github.lorisdemicheli.inventory.util;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtil {

	
	public static ItemStack basicItem(Material material,String displayName,int quantity) {
		ItemStack item = new ItemStack(material,quantity);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack loreItem(ItemStack item,String displayName,int quantity) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack loreItem(Material material,String displayName,int quantity,List<String> lore) {
		ItemStack item = new ItemStack(material,quantity);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack loreItem(ItemStack item,String displayName,int quantity,List<String> lore) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
}

