# Inventory

**This is a simple method to create a dynamic Minecraft Inventory GUI**

## What does it do

- Dynamic inventory
- Dynamic paged inventory
- Anvil inventory
- Custom PersistentDataType (just implements Serializable)
- Inventory update without reopen

## How to implement

To implement with Maven add the dependency on your pom

```
<dependency>
  <groupId>com.github.lorisdemicheli</groupId>
  <artifactId>inventory</artifactId>
  <version>2.1.4</version>
</dependency>
```

## How to use

Create a new Class, this class rappresents the GUI, extends the type of inventory choice.
For example we use [ChestInventory](https://github.com/lorisdemicheli/Inventory/blob/main/src/main/java/com/github/lorisdemicheli/inventory/ChestInventory.java)
 and the type of inventory we will use String
 
 ```java
public class TestNormal extends ChestInventory<String>{

	public TestNormal(Plugin plugin) {
		super(plugin, 54);
	}

	@Override
	public void onItemSelected(InventoryClickEvent event, ItemStack item) {
		switch (getItemValue(item)) {
		case "QUESTION":
			ask(new Ask("You want close inventory?") {			
				@Override
				public void onResult(boolean result) {
					if(result) {
						getSub().close(event.getWhoClicked());
					} else {
						getSub().openPrevius(event.getWhoClicked());
					}
				}
			}, event.getWhoClicked());
			break;
		case "SEARCH":
			search(new StringSearch("Write a player name") {
				@Override
				public void onResult(String result) {
					event.getWhoClicked().sendMessage(result);
					getSub().openPrevius(event.getWhoClicked());
				}
			}, event.getWhoClicked());
			break;
		case "PAGED":
			new TestPaged(this).open(event.getWhoClicked());
			break;
		default:
			break;
		}
	}
	
	@Override
	public String title(HumanEntity human) {
		return "FIRST EXAMPLE";
	}

	@Override
	public void placeItem(HumanEntity human) {
		Player p = (Player) human;
		setItem(5, new ItemStack(Material.BLACK_SHULKER_BOX),"PAGED");
		setItem(7, new ItemStack(Material.BOOK),"QUESTION");
		setItem(11, Skull.getHead(p),"SEARCH");
	}
}
```

For the second example we use [PagedChestInventory](https://github.com/lorisdemicheli/Inventory/blob/main/src/main/java/com/github/lorisdemicheli/inventory/PagedChestInventory.java),
in the example we have a list of random number which will update every 5 second (100 tick) and a BOWL item for go to previous inv (first example)


In the second page of custom GUI, with random number and an update every 5 second (100 tick), we set an item for go to previous inventory

```java
public class TestPaged extends PagedChestInventory<Integer>{

	public TestPaged(BaseInventory previous) {
		super(previous, 54);
	}

	@Override
	protected List<Integer> listElements(HumanEntity human) {
		List<Integer> numbers = new ArrayList<Integer>();
		for(int i = 0; i < 100; i++) {
			Random randomGenerator = new Random();
			numbers.add(randomGenerator.nextInt());
		}
		return numbers;
	}
	
	@Override
	protected boolean autoUpdate() {
		return true;
	}
	
	@Override
	protected long periodTickUpdate() {
		return 100;
	}
	
	@Override
	public void placeItem(HumanEntity human) {
		setItem(49, setStringKey(ItemUtil.basicItem(Material.BOWL, "Previous inv", 1),"back"));
	}
	
	@Override
	public void onItemSelected(InventoryClickEvent event, ItemStack item) {
		super.onItemSelected(event, item);
		String value = getItemStringValue(item);
		if(value != null && value.equals("back")) {
			openPrevius(event.getWhoClicked());
		}
	}

	@Override
	protected ItemStack itemList(HumanEntity human, Integer element) {
		return ItemUtil.basicItem(Material.COMPASS, element.toString(), 1);
	}

	@Override
	protected void onItemListSelected(Integer element, InventoryClickEvent event) {
		event.getWhoClicked().sendMessage("Selected number: " + element);
	}

	@Override
	public String title(HumanEntity human) {
		return ChatColor.BLUE + "Numbers";
	}
}
```


## Tested version

- 1.16.X - @lorisdemicheli

If you test with a different version contact me if it works or have a bug for fix
