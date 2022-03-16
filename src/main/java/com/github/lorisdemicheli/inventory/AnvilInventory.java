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
		Class<?> craftPlayer = ReflectionUtils.getCBVClass("entity.CraftPlayer");
		return ReflectionUtils.callMethod(craftPlayer.cast(human), "getHandle");
	}
	
	private Object chatMessageTitle() {
		return ReflectionUtils.newInstanceWithConstructor(
				ReflectionUtils.constractorValue(ReflectionUtils.getMVClass("ChatMessage"), String.class), title());
	}

	@Override
	public void open(HumanEntity human) {
		Player player = (Player) human;
		Object playerHandle = playerHandle(human);
		//default
		Class<?> craftEventFactoryClass = ReflectionUtils.getCBVClass("event.CraftEventFactory");
		ReflectionUtils.callStaticMethod(craftEventFactoryClass, "handleInventoryCloseEvent", playerHandle);
		ReflectionUtils.setFieldValue(playerHandle, "activeContainer", 
				ReflectionUtils.fieldValue(playerHandle, "defaultContainer"));
		
		//container
		int nextContainerCounter = (int) ReflectionUtils.callMethod(playerHandle, "nextContainerCounter");
		Object playerInventory = ReflectionUtils.fieldValue(playerHandle, "inventory");
		Class<?> containerAccessClass = ReflectionUtils.getMVClass("ContainerAccess");
		Class<?> craftWorldClass = ReflectionUtils.getCBVClass("CraftWorld");
		Object craftWorld = craftWorldClass.cast(player.getWorld());
		Object handleCraftWorld = ReflectionUtils.callMethod(craftWorld, "getHandle");
		Class<?> blockPositionClass = ReflectionUtils.getMVClass("BlockPosition");
		Object blockPosition = ReflectionUtils.newInstanceWithConstructor(ReflectionUtils.constractorValue(blockPositionClass,
				int.class,int.class,int.class), 0,0,0);
		Object containerAccess = ReflectionUtils.callStaticMethod(containerAccessClass,"at",handleCraftWorld,blockPosition);
		Class<?> anvilClass = ReflectionUtils.getMVClass("ContainerAnvil");
		Object anvil = ReflectionUtils.newInstance(anvilClass, nextContainerCounter,playerInventory,containerAccess);
		ReflectionUtils.setFieldValue(anvil, "checkReachable", false);
		Object levelCost = ReflectionUtils.fieldValue(anvil, "levelCost");
		ReflectionUtils.callMethod(levelCost, ReflectionUtils.findMethod(levelCost.getClass(), "set", int.class), 0);
		
		//set holder
		Object inventorySubcontainer = ReflectionUtils.fieldValue(anvil, "repairInventory");
		ReflectionUtils.setFieldValue(inventorySubcontainer, "bukkitOwner", this);
		
		//inv
		ReflectionUtils.callMethod(anvil, "setTitle", chatMessageTitle());
		this.inv = (Inventory) ReflectionUtils.callMethod(ReflectionUtils.callMethod(anvil, "getBukkitView"), "getTopInventory");
		placeItem(human);
		
		//open/send
		Class<?> containersClass = ReflectionUtils.getMVClass("Containers");
		Object containerAnvil = ReflectionUtils.staticFieldValue(containersClass,"ANVIL");
		int anvilId = (int) ReflectionUtils.fieldValue(anvil, "windowId");
		Class<?> iChatBaseComponentClass = ReflectionUtils.getMVClass("IChatBaseComponent");
		Class<?> packetPlayOutOpenWindowClass = ReflectionUtils.getMVClass("PacketPlayOutOpenWindow");
		Object packetPlayOutOpenWindow = ReflectionUtils.newInstanceWithConstructor(ReflectionUtils.constractorValue(
				packetPlayOutOpenWindowClass, int.class,containersClass,iChatBaseComponentClass),
				anvilId,containerAnvil,chatMessageTitle());
		Object playerConnection = ReflectionUtils.fieldValue(playerHandle, "playerConnection");
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
		throw new RuntimeException("Sub inventory not supported");
	}

	@Override
	public void setSub(BaseInventory sub) {
		throw new RuntimeException("Sub inventory not supported");
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