package com.github.lorisdemicheli.inventory.util;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtil {
	
	public static ItemStack basicItem(Material material,String displayName,int quantity) {
		ItemStack item = new ItemStack(material,quantity);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack itemDisplayName(ItemStack item,String displayName) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack loreItem(Material material,String displayName,int quantity,List<String> lore) {
		ItemStack item = new ItemStack(material,quantity);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
		meta.setLore(translateAlternateColorCodesList(lore));
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack loreItem(ItemStack item,String displayName,List<String> lore) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
		meta.setLore(translateAlternateColorCodesList(lore));
		item.setItemMeta(meta);
		return item;
	}
	
	private static List<String> translateAlternateColorCodesList(List<String> list){
		return list.stream()
				.map(s->ChatColor.translateAlternateColorCodes('&', s))
				.collect(Collectors.toList());
	}
}

