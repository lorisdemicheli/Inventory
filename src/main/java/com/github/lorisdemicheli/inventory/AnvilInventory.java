package com.github.lorisdemicheli.inventory;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.github.lorisdemicheli.inventory.listener.InventoryListener;
import com.github.lorisdemicheli.inventory.util.BaseInventory;
import com.github.lorisdemicheli.inventory.util.ReflectionUtils;

public abstract class AnvilInventory implements BaseInventory{
	
	private Plugin plugin;
	private BaseInventory previous;
	private Inventory inv;

	public AnvilInventory(BaseInventory previous) {
		InventoryListener.of(plugin);
		this.previous = previous;
		this.previous.setSub(this);
		this.plugin = previous.getPlugin();	
	}

	@Override
	public void setItem(int index, ItemStack item) {
		if(!validPosition(index)) {
			throw new IllegalArgumentException(String.format("Position %d is not allowed", index));
		}
		getInventory().setItem(index, item);
	}
	
	private boolean validPosition(int index) {
		return index >= 0 && index < 3;
	}
	
	private Object playerHandle(HumanEntity human) {
		Class<?> craftPlayer = ReflectionUtils.getCraftBukkitVersionClass("entity.CraftPlayer");
		return ReflectionUtils.callMethod(craftPlayer.cast(human), "getHandle");
	}
	
	private Object chatMessageTitle() {
		return ReflectionUtils.newInstance(ReflectionUtils.getServerVersionClass("ChatMessage"), title());
	}

	@Override
	public void open(HumanEntity human) {
		Player player = (Player) human;
		Object playerHandle = playerHandle(human);
		//default
		Class<?> craftEventFactoryClass = ReflectionUtils.getCraftBukkitVersionClass("event.CraftEventFactory");
		ReflectionUtils.callStaticMethod(craftEventFactoryClass, "handleInventoryCloseEvent", playerHandle);
		ReflectionUtils.setFieldValue(playerHandle, "activeContainer", 
				ReflectionUtils.getFieldValue(playerHandle, "defaultContainer"));
		
		//container
		int nextContainerCounter = (int) ReflectionUtils.callMethod(playerHandle, "nextContainerCounter");
		Object playerInventory = ReflectionUtils.getFieldValue(playerHandle, "inventory");
		Class<?> containerAccessClass = ReflectionUtils.getServerVersionClass("ContainerAccess");
		Class<?> craftWorldClass = ReflectionUtils.getCraftBukkitVersionClass("CraftWorld");
		Object craftWorld = craftWorldClass.cast(player.getWorld());
		Object handleCraftWorld = ReflectionUtils.callMethod(craftWorld, "getHandle");
		Class<?> blockPositionClass = ReflectionUtils.getServerVersionClass("BlockPosition");
		Object blockPosition = ReflectionUtils.newInstance(blockPositionClass, 0,0,0);
		Object containerAccess = ReflectionUtils.callStaticMethod(containerAccessClass,"at",handleCraftWorld,blockPosition);
		Class<?> anvilClass = ReflectionUtils.getServerVersionClass("ContainerAnvil");
		Object anvil = ReflectionUtils.newInstance(anvilClass, nextContainerCounter,playerInventory,containerAccess);
		ReflectionUtils.setFieldValue(anvil, "checkReachable", false);
		Object levelCost = ReflectionUtils.getFieldValue(anvil, "levelCost");
		ReflectionUtils.callMethod(levelCost, "set", 0);
		
		//set holder
		Object inventorySubcontainer = ReflectionUtils.getFieldValue(anvil, "repairInventory");
		ReflectionUtils.setFieldValue(inventorySubcontainer, "bukkitOwner", this);
		
		//inv
		ReflectionUtils.callMethod(anvil, "setTitle", chatMessageTitle());
		this.inv = (Inventory) ReflectionUtils.callMethod(ReflectionUtils.callMethod(anvil, "getBukkitView"), "getTopInventory");
		placeItem(human);
		
		//open/send
		Class<?> containersClass = ReflectionUtils.getServerVersionClass("Containers");
		Object containerAnvil = ReflectionUtils.getStaticFieldValue(containersClass,"ANVIL");
		int anvilId = (int) ReflectionUtils.getFieldValue(anvil, "windowId");
		Class<?> packetPlayOutOpenWindowClass = ReflectionUtils.getServerVersionClass("PacketPlayOutOpenWindow");
		Object packetPlayOutOpenWindow = ReflectionUtils
				.newInstance(packetPlayOutOpenWindowClass, anvilId,containerAnvil,chatMessageTitle());
		Object playerConnection = ReflectionUtils.getFieldValue(playerHandle, "playerConnection");
		ReflectionUtils.callMethod(playerConnection, "sendPacket", packetPlayOutOpenWindow);
		ReflectionUtils.setFieldValue(playerHandle, "activeContainer", anvil);
		ReflectionUtils.callMethod(anvil, "addSlotListener", playerHandle);
	}

	@Override
	public void close(HumanEntity human) {
		human.closeInventory();
	}

	@Override
	public void onClose() {
		getInventory().clear();
	}
	
	@Override
	public boolean cancelledClick() {
		return true;
	}

	@Override
	public BaseInventory getSub() {
		throw new UnsupportedOperationException("Sub inventory not supported");
	}

	@Override
	public void setSub(BaseInventory sub) {
		throw new UnsupportedOperationException("Sub inventory not supported");
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
	public Inventory getInventory() {
		return inv;
	}

	@Override
	public void openPrevius(HumanEntity human) {
		human.closeInventory();
		previous.open(human);
	}

}