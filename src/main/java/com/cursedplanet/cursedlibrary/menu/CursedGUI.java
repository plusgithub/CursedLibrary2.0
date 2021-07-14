package com.cursedplanet.cursedlibrary.menu;

import com.cursedplanet.cursedlibrary.LibraryPlugin;
import com.cursedplanet.cursedlibrary.menu.slot.CursedSlot;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.PlayerUtil;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CursedGUI {

	private final Random r = new Random();

	private final Integer size;
	@Getter
	private final String title;
	protected HashMap<Integer, CursedSlot> contents;
	protected Inventory inv;
	private final UUID id;
	private final HashMap<CursedGUI, HashMap<Runnable, String>> runnables;
	private final HashMap<CursedGUI, List<BukkitTask>> runningTasks;
	protected Consumer<InventoryCloseEvent> consumer;
	protected Consumer<InventoryClickEvent> updateTask;
	protected Sound openSound;
	protected Sound closeSound;
	private String[] pattern;
	private final HashMap<Character, Map<ItemStack, Consumer<InventoryClickEvent>>> assignedPatterns;


	// Pagination
	private String pagePatternSingle;
	private List<CursedSlot> pageItems;
	private Integer currentPage;
	private CursedPageButton pageButtons;

	public CursedGUI(int size, String title) {
		this.size = size;
		this.title = Common.colorize(title);

		this.inv = Bukkit.createInventory(new CursedHolder(), this.size, this.title);
		this.id = ((CursedHolder) inv.getHolder()).getId();

		this.contents = new HashMap<>();
		for (int i = 0; i < size; i++) {
			contents.put(i, new CursedSlot(i));
		}

		this.runnables = new HashMap<>();
		this.runningTasks = new HashMap<>();
		consumer = null;
		updateTask = null;
		openSound = null;
		closeSound = null;
		pattern = null;
		assignedPatterns = new HashMap<>();

		this.pagePatternSingle = null;
		this.pageItems = new ArrayList<>();
		this.currentPage = 1;
		pageButtons = new CursedPageButton();

		MenuHandler.addInventory(this);
	}

	/**
	 * Get the viewers of this menu
	 *
	 * @return a list of all players currently viewing the menu
	 */
	public List<Player> getViewers() {
		return inv.getViewers().stream()
				.map(e -> (Player) e)
				.collect(Collectors.toList());
	}

	/**
	 * Returns the ID of this menu
	 */
	public UUID getId() {
		return this.id;
	}

	/**
	 * Returns the size of this menu
	 */
	public Integer getSize() {
		return this.size;
	}

	/**
	 * Get the amount of usable slots on this page
	 *
	 * @return the amount of usable slots
	 */
	public int getSlots() {
		return getSize() - 1;
	}

	/**
	 * Returns the slot without creating a new object, if you
	 * want to change the item/action without affecting eachother.
	 */
	public CursedSlot getSlot(int slot) {
		return contents.get(slot);
	}

	/**
	 * Gets the itemstack in a slot
	 *
	 * @return the itemstack
	 */
	public ItemStack getItemAt(int slot) {
		return inv.getItem(slot);
	}

	/**
	 * Gets the itemstack in a slot
	 *
	 * @return the itemstack
	 */
	public ItemStack getItemAt(CursedSlot slot) {
		return slot.getItem();
	}

	/**
	 * Returns a random slot you can do anything with
	 */
	private CursedSlot getRandomSlot() {
		ArrayList<Integer> emptySlots = new ArrayList<>();
		for (int i = 0; i < inv.getSize(); i++) {
			if (inv.getItem(i) == null) {
				emptySlots.add(i);
			}

		}
		if (emptySlots.isEmpty())
			return null;
		return contents.get(emptySlots.get(r.nextInt(emptySlots.size())));
	}

	/**
	 * Update the title of the menu for that player
	 *
	 * @param title the new title
	 */
	public void updateTitle(String title) {
		for (Player player : getViewers()) {
			PlayerUtil.updateInventoryTitle(player, title);
		}
	}

	/**
	 * Update the title of the menu for that player for a period of time
	 * until it resets back to the old title
	 *
	 * @param title the new title
	 * @param ticks the time in ticks before the old title is set back
	 */
	public void updateTitleTimed(String title, int ticks) {
		String oldTitle = getTitle();

		updateTitle(title);

		Common.runLater(ticks, () -> {
			if (getViewers().get(0).getInventory().getHolder() instanceof CursedHolder) {
				UUID id = ((CursedHolder) getViewers().get(0).getInventory().getHolder()).getId();

				if (id.equals(getId()))
					updateTitle(oldTitle);
			}
		});
	}

	/**
	 * Add an item, that on clicked, does nothing
	 */
	public CursedSlot addStatic(ItemStack item, int slot) {
		this.contents.put(slot, new CursedSlot(slot, item));
		this.inv.setItem(slot, item);
		return contents.get(slot);
	}

	/**
	 * Add a clickable item
	 */
	public CursedSlot addClickable(ItemStack item, int slot, Consumer<InventoryClickEvent> task) {
		this.contents.put(slot, new CursedSlot(slot, item, task));
		this.inv.setItem(slot, item);
		return contents.get(slot);
	}

	/**
	 * Add a random clickable somewhere that is available in the menu
	 *
	 * @return a new CursedSlot or null if no slot is available
	 */
	public CursedSlot randomClickable(ItemStack item, Consumer<InventoryClickEvent> consumer) {
		if (getRandomSlot() != null)
			return new CursedSlot(getRandomSlot().getSlot(), item, consumer);
		return null;
	}

	/**
	 * Add a random static somewhere that is available in the menu
	 *
	 * @return a new CursedSlot
	 */
	public CursedSlot randomStatic(ItemStack item) {
		if (getRandomSlot() != null)
			return new CursedSlot(getRandomSlot().getSlot(), item);
		return null;
	}

	private int getEmptyAmount() {
		int i = 0;
		for (ItemStack item : inv.getContents()) {
			if (item == null)
				i++;
		}
		return i;
	}

	/**
	 * Fill every empty slot with a static item, with the choice to skip some slots
	 *
	 * @return an array of the filled CursedSlots
	 */
	public CursedSlot[] fillStatic(ItemStack item) {
		return fillStatic(item, new ArrayList<>());
	}

	/**
	 * Fill every empty slot with a static item, with the choice to skip some slots
	 *
	 * @return an array of the filled CursedSlots
	 */
	public CursedSlot[] fillStatic(ItemStack item, Integer... skippedSlots) {
		return fillStatic(item, (List<Integer>) Arrays.asList(skippedSlots));
	}

	/**
	 * Fill every empty slot with a static item, with the choice to skip some slots
	 *
	 * @return an array of the filled CursedSlots
	 */
	public CursedSlot[] fillStatic(ItemStack item, List<Integer> skippedSlots) {
		int i = 0;

		CursedSlot[] menuItems = new CursedSlot[getEmptyAmount()];

		while (i < getSize()) {
			if (inv.getItem(i) == null && !skippedSlots.contains(i)) {
				menuItems[i] = addStatic(item, i);
			}
			i++;
		}

		return Arrays.stream(menuItems).filter(Objects::nonNull).toArray(CursedSlot[]::new);
	}

	/**
	 * Fill every empty slot with a clickable item, with the choice to skip some slots
	 *
	 * @return an array of the filled CursedSlots
	 */
	public CursedSlot[] fillClickable(ItemStack item, Consumer<InventoryClickEvent> consumer) {
		return fillClickable(item, new ArrayList<>(), consumer);
	}

	/**
	 * Fill every empty slot with a clickable item, with the choice to skip some slots
	 *
	 * @return an array of the filled CursedSlots
	 */
	public CursedSlot[] fillClickable(ItemStack item, Consumer<InventoryClickEvent> consumer, Integer...
			skippedSlots) {
		return fillClickable(item, (List<Integer>) Arrays.asList(skippedSlots), consumer);
	}

	/**
	 * Fill every empty slot with a clickable item, with the choice to skip some slots
	 *
	 * @return an array of the filled CursedSlots
	 */
	public CursedSlot[] fillClickable(ItemStack
											  item, List<Integer> skippedSlots, Consumer<InventoryClickEvent> consumer) {
		int i = 0;

		CursedSlot[] menuItems = new CursedSlot[getEmptyAmount()];

		while (i < getSize()) {
			if (inv.getItem(i) == null && !skippedSlots.contains(i)) {
				menuItems[i] = addClickable(item, i, consumer);
			}
			i++;
		}

		return Arrays.stream(menuItems).filter(Objects::nonNull).toArray(CursedSlot[]::new);
	}

	/**
	 * Fill the borders of the gui with an item
	 *
	 * @param item the border item
	 * @return an array of CursedSlots
	 */
	public CursedSlot[] fillBorders(ItemStack item) {
		return fillBorders(item, 0, getSize() - 1);
	}

	/***
	 * Fill the borders of the gui with an item, creating a rectangle out of the 2 points highlighted
	 * @param item the border item
	 * @param startSlot the top left corner of the rectangle
	 * @param endSlot the bottom right corner of the rectangle
	 * @return an array of CursedSlots
	 */
	public CursedSlot[] fillBorders(ItemStack item, int startSlot, int endSlot) {
		int iterator = startSlot;

		CursedSlot[] menuItems = new CursedSlot[getSize()];

		int length = (endSlot - startSlot) % 9;
		int corner1 = startSlot + length;
		int corner2 = endSlot - length;

		while (iterator <= endSlot) {

			boolean b = iterator % 9 == startSlot % 9 || iterator % 9 == endSlot % 9;


			if (iterator >= startSlot && iterator <= corner1)
				menuItems[iterator] = addStatic(item, iterator);
			if (iterator >= corner2 && iterator <= endSlot)
				menuItems[iterator] = addStatic(item, iterator);
			if (b)
				menuItems[iterator] = addStatic(item, iterator);

			iterator++;
		}
		return Arrays.stream(menuItems).filter(Objects::nonNull).toArray(CursedSlot[]::new);
	}

	/***
	 * Fill the borders of the gui with an item
	 * @param item the border item
	 * @param consumer the click event of any border item
	 * @return an array of CursedSlots
	 */
	public CursedSlot[] fillBorders(ItemStack item, Consumer<InventoryClickEvent> consumer) {
		return fillBorders(item, 0, getSize() - 1, consumer);
	}

	/***
	 * Fill the borders of the gui with an item, creating a rectangle out of the 2 points highlighted
	 * @param item the border item
	 * @param startSlot the top left corner of the rectangle
	 * @param endSlot the bottom right corner of the rectangle
	 * @param consumer the click event of any border item
	 * @return an array of CursedSlots
	 */
	public CursedSlot[] fillBorders(ItemStack item, int startSlot, int endSlot, Consumer<
			InventoryClickEvent> consumer) {
		int iterator = startSlot;

		CursedSlot[] menuItems = new CursedSlot[getSize()];

		int length = (endSlot - startSlot) % 9;
		int corner1 = startSlot + length;
		int corner2 = endSlot - length;

		while (iterator <= endSlot) {

			boolean b = iterator % 9 == startSlot % 9 || iterator % 9 == endSlot % 9;


			if (iterator >= startSlot && iterator <= corner1)
				menuItems[iterator] = addClickable(item, iterator, consumer);
			if (iterator >= corner2 && iterator <= endSlot)
				menuItems[iterator] = addClickable(item, iterator, consumer);
			if (b)
				menuItems[iterator] = addClickable(item, iterator, consumer);

			iterator++;
		}
		return Arrays.stream(menuItems).filter(Objects::nonNull).toArray(CursedSlot[]::new);
	}


	/***
	 * Fill a rectangle in a gui with items
	 * @param item the item to be filled
	 * @param startSlot the top left corner of the rectangle
	 * @param endSlot the bottom right corner of the rectangle
	 * @return an array of all CursedSlots filled by the rectangle
	 */
	public CursedSlot[] fillRectangle(ItemStack item, int startSlot, int endSlot) {
		int iterator = startSlot;

		CursedSlot[] menuItems = new CursedSlot[getSize()];

		while (iterator <= endSlot) {
			if (inRectangle(iterator, startSlot, endSlot))
				menuItems[iterator] = addStatic(item, iterator);

			iterator++;
		}
		return Arrays.stream(menuItems).filter(Objects::nonNull).toArray(CursedSlot[]::new);
	}

	/***
	 * Fill a rectangle in a gui with items
	 * @param item the item to be filled
	 * @param startSlot the top left corner of the rectangle
	 * @param endSlot the bottom right corner of the rectangle
	 * @param consumer the click event for any item in the rectangle
	 * @return an array of all CursedSlots filled by the rectangle
	 */
	public CursedSlot[] fillRectangle(ItemStack item, int startSlot, int endSlot, Consumer<
			InventoryClickEvent> consumer) {
		int iterator = startSlot;

		CursedSlot[] menuItems = new CursedSlot[getSize()];

		while (iterator <= endSlot) {
			if (inRectangle(iterator, startSlot, endSlot))
				menuItems[iterator] = addClickable(item, iterator, consumer);

			iterator++;
		}
		return Arrays.stream(menuItems).filter(Objects::nonNull).toArray(CursedSlot[]::new);
	}

	/***
	 * A method to check if an item is in a rectangular space
	 */
	private boolean inRectangle(int slot, int startSlot, int endSlot) {
		return slot % 9 <= endSlot % 9 && slot % 9 >= startSlot % 9;
	}

	/***
	 * Add a callback event for the closure of this menu
	 * @param consumer the task to be run on closure
	 */
	public void addCloseCallback(Consumer<InventoryCloseEvent> consumer) {
		this.consumer = consumer;
	}

	/***
	 * Add a task to happen universally when a player clicks anywhere in the menu
	 * @param task the task to run when a click is registered
	 */
	public void addGlobalClick(Consumer<InventoryClickEvent> task) {
		this.updateTask = task;
	}

	/***
	 * Set an open sound for players when the gui is displayed to them
	 * @param sound the sound effect
	 */
	public void setOpenSound(Sound sound) {
		this.openSound = sound;
	}

	/***
	 * Set an open sound for players when the gui is closed
	 * @param sound the sound effect
	 */
	public void setCloseSound(Sound sound) {
		this.closeSound = sound;
	}

	/***
	 * Set a custom character pattern to save manual creation of items
	 * @param rowPatterns the 9 character long pattern for a row, in order of definition
	 */
	public void setPattern(String... rowPatterns) {
		this.pattern = rowPatterns;
	}

	/***
	 * Assign a character defined in the pattern to a specific itemstack
	 * @param character the character that is being set
	 * @param item the itemstack to mask this character
	 */
	public void assignPatternItem(char character, ItemStack item) {
		Map<ItemStack, Consumer<InventoryClickEvent>> map = new HashMap<>();
		map.put(item, e -> {
		});
		this.assignedPatterns.put(character, map);
	}

	/***
	 * Assign a character defined in the pattern to a specific itemstack
	 * @param character the character that is being set
	 * @param item the itemstack to mask this character
	 * @param consumer optional addition of a consumer for these items
	 */
	public void assignPatternItem(char character, ItemStack item, Consumer<InventoryClickEvent> consumer) {
		Map<ItemStack, Consumer<InventoryClickEvent>> map = new HashMap<>();
		map.put(item, consumer);
		this.assignedPatterns.put(character, map);
	}

	/***
	 * Fill the pattern with the predefined items
	 */
	public CursedSlot[] fillPattern() {
		return fillPattern(slot -> {
		});
	}

	/***
	 * Fill the pattern with the predefined items
	 * @param consumer Perform a forEach on each slot allowing different actions & clicks after they have been set
	 */
	public CursedSlot[] fillPattern(Consumer<CursedSlot> consumer) {
		CursedSlot[] menuItems = new CursedSlot[pattern.length * 9];
		for (int i = 0; i < pattern.length; i++) {
			for (int o = 0; o < pattern[i].toCharArray().length; o++) {
				ItemStack item = new ItemStack(Material.AIR);
				Consumer<InventoryClickEvent> task = e -> {
				};
				if (assignedPatterns.containsKey(pattern[i].toCharArray()[o])) {
					item = (ItemStack) assignedPatterns.get(pattern[i].toCharArray()[o]).keySet().toArray()[0];
					task = assignedPatterns.get(pattern[i].toCharArray()[o]).get(item);
				}
				menuItems[(i * 9) + o] = addClickable(item, (i * 9) + o, task);
			}
		}
		menuItems = Arrays.stream(menuItems).filter(Objects::nonNull).toArray(CursedSlot[]::new); //Remove all null values from the list
		Arrays.stream(menuItems).forEach(consumer);
		return menuItems;
	}

	/***
	 * Set a custom character pattern to save manual creation of items
	 * @param rowPatterns the 9 character long pattern for a row, in order of definition
	 *                    NOTE: A # (Hash) represents an item, any other character will be discarded
	 */
	public void setPagePattern(String... rowPatterns) {
		this.pagePatternSingle = String.join("", rowPatterns);
	}

	/***
	 * Add the static items that will be on the page
	 * @param items The array of items to be added
	 */
	public void addPageItems(ItemStack... items) {
		for (ItemStack item : items) {
			this.pageItems.add(new CursedSlot(item, e -> {
			}));
		}
	}

	/***
	 * Add static items one by one to the page items list
	 * @param item The itemstack to be presented
	 */
	public void addPageItem(ItemStack item) {
		this.pageItems.add(new CursedSlot(item, e -> {
		}));
	}

	/***
	 * Add the clickable items that will be on the page
	 * @param task The click event for these items
	 * @param items The array of items to be added
	 */
	public void addPageItems(Consumer<InventoryClickEvent> task, ItemStack... items) {
		for (ItemStack item : items) {
			this.pageItems.add(new CursedSlot(item, task));
		}
	}

	/***
	 * Add clickable items one by one to the page items list
	 * @param item The itemstack to be presented
	 * @param task The click event for this item
	 */
	public void addPageItem(Consumer<InventoryClickEvent> task, ItemStack item) {
		this.pageItems.add(new CursedSlot(item, task));
	}

	private int getStartSlot() {
		for (int o = 0; o < pagePatternSingle.toCharArray().length; o++) {
			if (pagePatternSingle.charAt(o) == '#')
				return o;
		}
		return 0;
	}

	/***
	 * Check if the specified slot will be set a paged item
	 * @return boolean if slot will be a paged item
	 */
	public boolean isPagedSlot(int slot) {
		return pagePatternSingle.charAt(slot) == '#';
	}

	/***
	 * Check if the specified slot will be set a paged item
	 * @return boolean if slot will be a paged item
	 */
	public boolean isPagedSlot(CursedSlot slot) {
		return pagePatternSingle.charAt(slot.getSlot()) == '#';
	}

	/***
	 * Fill the page layout with all currently added items
	 * @return the filled CursedSlots
	 */
	public CursedSlot[] fillPages() {
		return fillPages(slot -> {
		});
	}

	/***
	 * Fill the page layout with all currently added items
	 * @param consumer Perform a forEach on each slot allowing different actions & clicks after they have been set
	 * @return the filled CursedSlots
	 */
	public CursedSlot[] fillPages(Consumer<CursedSlot> consumer) {
		int itemsPerPage = getFillableSlots();
		int startSlot = getStartSlot();
		int firstIndex = (itemsPerPage * (currentPage - 1));
		int slots = getSlots() - (getSlots() - pagePatternSingle.length()) - 1;
		int iterator = 0;
		CursedSlot[] menuItems = new CursedSlot[slots];

		pageItems.stream().forEach(slot -> {
			Common.log(String.valueOf(slot.getItem()));
		});

		for (int i = 0; i < slots; i++) {
			if ((i + startSlot) < getSize() && isPagedSlot(i + startSlot)) {
				try {
					menuItems[i] = addClickable(pageItems.get(firstIndex + iterator).getItem(), i + startSlot, pageItems.get(firstIndex + iterator).getTask());
				} catch (Exception e) {
					menuItems[i] = addStatic(new ItemStack(Material.AIR), i + startSlot);
				}
				iterator++;
			}
		}
		//Arrays.asList(menuItems).forEach(consumer);
		menuItems = Arrays.stream(menuItems).filter(Objects::nonNull).toArray(CursedSlot[]::new); //Remove all null values from the list
		Arrays.stream(menuItems).forEach(consumer);

		if (this.pageButtons.isNextButtonApplicable())
			addClickable(isLastPage() ? this.pageButtons.getNextButtonDeny() : this.pageButtons.getNextButtonSuccess(), this.pageButtons.getNextButtonSlot(), e -> {
				nextPage(consumer);
			});

		if (this.pageButtons.isPreviousButtonApplicable())
			addClickable(isFirstPage() ? this.pageButtons.getPreviousButtonDeny() : this.pageButtons.getPreviousButtonSuccess(), this.pageButtons.getPreviousButtonSlot(), e -> {
				previousPage(consumer);
			});

		return menuItems;
	}

	/***
	 * Set the current page index
	 * @param page the page index
	 */
	public void setPage(int page) {
		this.currentPage = page;
		fillPages();
	}

	/***
	 * Get the current page index
	 * @return the page index
	 */
	public Integer getPage() {
		return this.currentPage;
	}

	/***
	 * Check if the current page is the first page
	 * @return boolean if the page is the first page
	 */
	public boolean isFirstPage() {
		return getPage() == 1;
	}

	/***
	 * Check if the current page is the last page
	 * @return boolean if the page is the last page
	 */
	public boolean isLastPage() {
		return getPage() == getMaxPages();
	}

	/***
	 * Switch the page index to the next page
	 */
	public CursedSlot[] nextPage() {
		if (!(getPage() >= getMaxPages())) {
			this.currentPage++;
			return fillPages();
		}
		return new CursedSlot[0];
	}

	/***
	 * Switch the page index to the next page
	 * @param consumer If switching page manually, set the same consumer here as fillPages() as it calls the same method
	 */
	public CursedSlot[] nextPage(Consumer<CursedSlot> consumer) {
		if (!(getPage() >= getMaxPages())) {
			this.currentPage++;
			return fillPages(consumer);
		}
		return new CursedSlot[0];
	}

	/***
	 * Switch the page index to the previous page
	 * @param consumer If switching page manually, set the same consumer here as fillPages() as it calls the same method
	 */
	public CursedSlot[] previousPage(Consumer<CursedSlot> consumer) {
		if (getPage() != 1) {
			this.currentPage--;
			return fillPages(consumer);
		}
		return new CursedSlot[0];
	}

	/***
	 * Switch the page index to the previous page
	 */
	public CursedSlot[] previousPage() {
		if (getPage() != 1) {
			this.currentPage--;
			return fillPages();
		}
		return new CursedSlot[0];
	}

	/**
	 * Set the next page button
	 * NOTE: Make sure you include this method BEFORE you add your items, or else it won't show.
	 *
	 * @param success The item if there is a next page
	 * @param deny    The item if there isn't a next page
	 * @param slot    The slot for this item
	 */
	public void addNextPageButton(ItemStack success, ItemStack deny, int slot) {
		this.pageButtons.setNextButtonSuccess(success);
		this.pageButtons.setNextButtonDeny(deny);
		this.pageButtons.setNextButtonSlot(slot);
	}

	/**
	 * Set the previous page button
	 * NOTE: Make sure you include this method BEFORE you add your items, or else it won't show.
	 *
	 * @param success The item if there is a previous page
	 * @param deny    The item if there isn't a previous page
	 * @param slot    The slot for this item
	 */
	public void addPreviousPageButton(ItemStack success, ItemStack deny, int slot) {
		this.pageButtons.setPreviousButtonSuccess(success);
		this.pageButtons.setPreviousButtonDeny(deny);
		this.pageButtons.setPreviousButtonSlot(slot);
	}

	/***
	 * Get the maximum amount of pages with the current added items
	 */
	public int getMaxPages() {
		return (int) Math.ceil((double) pageItems.size() / (double) getFillableSlots());
	}


	/**
	 * Get the amount of fillable slots, mainly for the function above
	 */
	private int getFillableSlots() {
		int fillableSlots = 0;

		for (int i = 0; i < pagePatternSingle.length(); i++) {
			if (pagePatternSingle.charAt(i) == '#')
				fillableSlots++;
		}

		return fillableSlots;
	}

	/**
	 * Schedule a repeating task whilst the inventory is open for this player
	 *
	 * @param repeat How long in between the task runs
	 * @param task   The task runnable
	 */
	public void repeatingTask(long repeat, Runnable task) {
		repeatingTask(0L, repeat, task);
	}

	/**
	 * Schedule a repeating task whilst the inventory is open for this player
	 *
	 * @param delay  The initial delay before the task starts to run
	 * @param repeat How long in between the task runs
	 * @param task   The task runnable
	 */
	public void repeatingTask(long delay, long repeat, Runnable task) {
		String newTimings = delay + "," + repeat;
		HashMap<Runnable, String> temp = new HashMap<>();
		if (runnables.containsKey(this)) {
			temp = runnables.get(this);
		}
		temp.put(task, newTimings);
		runnables.put(this, temp);
	}

	private void startRunnables() {
		Map<Runnable, String> temp = runnables.get(this);
		if (temp != null && !temp.isEmpty()) {
			List<BukkitTask> tasks = new ArrayList<>();
			for (Runnable task : temp.keySet()) {
				String[] timings = temp.get(task).split(",");
				tasks.add(Bukkit.getScheduler().runTaskTimer(LibraryPlugin.getInstance(), task, Long.decode(timings[0]), Long.decode(timings[1])));
			}
			runnables.remove(this);
			runningTasks.put(this, tasks);
		}
	}

	/**
	 * Stop the current running runnables
	 */
	public void stopRunnables() {
		List<BukkitTask> temp = runningTasks.get(this);
		if (temp != null) {
			for (BukkitTask task : temp) {
				task.cancel();
			}
			runningTasks.remove(this);
		}
	}

	/**
	 * Resets all the slots in the menu, starting from fresh without creating a new menu
	 */
	public void reset() {
		//this.contents = new HashMap<>();
		ItemStack empty = new ItemStack(Material.AIR);
		for (int i : contents.keySet()) {
			addStatic(empty, i).allowAllActions().allowAllClicks();
		}
		this.pagePatternSingle = null;
		this.pageItems = new ArrayList<>();
		this.currentPage = 1;
		this.pageButtons = new CursedPageButton();
		//return contents.keySet().toArray(CursedSlot[]::new);
	}

	/**
	 * Method to close inventory for a player
	 */
	public void close(Player player) {
		player.closeInventory();
	}

	/**
	 * Display the menu to a player
	 *
	 * @param player, the player to show the menu to
	 */
	public void display(Player player) {
		player.openInventory(this.inv);

		if (openSound != null)
			player.playSound(player.getLocation(), this.openSound, 1, 1);

		startRunnables();
	}
}
