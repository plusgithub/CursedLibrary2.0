package com.cursedplanet.cursedlibrary.menu.slot;

import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class CursedSlot {

	private final Integer slot;
	private Consumer<InventoryClickEvent> task;
	private final List<ClickType> allowedClicks;
	private final List<InventoryAction> allowedActions;
	private ItemStack item;


	public CursedSlot(int slot) {
		this.slot = slot;
		this.task = e -> {
		};
		this.allowedClicks = new ArrayList<>();
		this.allowedActions = new ArrayList<>();
		this.item = new ItemStack(Material.AIR);
	}

	public CursedSlot(int slot, Consumer<InventoryClickEvent> task) {
		this.slot = slot;
		this.task = task;
		this.allowedClicks = new ArrayList<>();
		this.allowedActions = new ArrayList<>();
		this.item = new ItemStack(Material.AIR);
	}

	public CursedSlot(int slot, ItemStack item) {
		this.slot = slot;
		this.task = e -> {
		};
		this.allowedClicks = new ArrayList<>();
		this.allowedActions = new ArrayList<>();
		this.item = new ItemStack(item);
	}

	public CursedSlot(int slot, ItemStack item, Consumer<InventoryClickEvent> task) {
		this.slot = slot;
		this.task = task;
		this.allowedClicks = new ArrayList<>();
		this.allowedActions = new ArrayList<>();
		this.item = new ItemStack(item);
	}

	/**
	 * @deprecated DO NOT USE! Only for single purpose inside of CursedGUI
	 */
	public CursedSlot(ItemStack item, Consumer<InventoryClickEvent> task) {
		this.slot = 0;
		this.task = task;
		this.allowedClicks = new ArrayList<>();
		this.allowedActions = new ArrayList<>();
		this.item = new ItemStack(item);
	}

	/**
	 * Returns the index slot of this Slot instance
	 */
	public int getSlot() {
		return slot;
	}

	/**
	 * Returns the item that is currently in this item slot.
	 */
	public ItemStack getItem() {
		return item;
	}

	/**
	 * Get the task for this slot
	 *
	 * @return Consumer<InventoryClickEvent> the task
	 */
	public Consumer<InventoryClickEvent> getTask() {
		return task;
	}

	/**
	 * Add allowed ClickTypes to the slot
	 *
	 * @param type The clicktype that should be allowed
	 */
	public CursedSlot allow(ClickType... type) {
		allowedClicks.addAll(Arrays.asList(type));
		return this;
	}

	/**
	 * Add allowed InventoryActions to the slot
	 *
	 * @param action The inventoryaction that should be allowed
	 */
	public CursedSlot allow(InventoryAction... action) {
		allowedActions.addAll(Arrays.asList(action));
		return this;
	}

	/**
	 * Remove allowed ClickTypes to the slot
	 *
	 * @param type The clicktype that should be disallowed
	 */
	public CursedSlot disallow(ClickType... type) {
		allowedClicks.removeAll(Arrays.asList(type));
		return this;
	}

	/**
	 * Remove allowed InventoryActions to the slot
	 *
	 * @param action The inventoryaction that should be disallowed
	 */
	public CursedSlot disallow(InventoryAction... action) {
		allowedActions.removeAll(Arrays.asList(action));
		return this;
	}

	/**
	 * Quickly allow all ClickTypes instead of doing it manually
	 */
	public CursedSlot allowAllClicks() {
		allowedClicks.addAll(Arrays.asList(ClickType.values()));
		return this;
	}

	/**
	 * Quickly allow all InventoryActions instead of doing it manually
	 */
	public CursedSlot allowAllActions() {
		allowedActions.addAll(Arrays.asList(InventoryAction.values()));
		return this;
	}

	/**
	 * Runs the task, really only for use in CursedListener
	 *
	 * @param event The event that is being run
	 */
	public void pass(InventoryClickEvent event) {
		this.task.accept(event);
	}

	/**
	 * Check if a ClickType is allowed, aka, if it is in the allowedClicks register
	 */
	public boolean isClickAllowed(ClickType type) {
		return allowedClicks.contains(type);
	}

	/**
	 * Check if an InventoryAction is allowed, aka, if it is in the allowedActions register
	 */
	public boolean isActionAllowed(InventoryAction action) {
		return allowedActions.contains(action);
	}

	/**
	 * Sets the item for this slot
	 *
	 * @param item is the item that will be in that slot
	 */
	public CursedSlot setItem(ItemStack item) {
		this.item = item;
		return this;
	}

	/**
	 * Set the click action that is performed when this slot is clicked
	 *
	 * @param action The runnable action when this slot is clicked
	 */
	public CursedSlot setAction(Consumer<InventoryClickEvent> action) {
		this.task = action;
		return this;
	}
}
