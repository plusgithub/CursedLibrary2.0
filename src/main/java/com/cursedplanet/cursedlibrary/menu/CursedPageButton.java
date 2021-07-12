package com.cursedplanet.cursedlibrary.menu;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

public class CursedPageButton {

	@Getter
	private ItemStack nextButtonSuccess;
	@Getter
	private ItemStack nextButtonDeny;
	@Getter
	private int nextButtonSlot;

	@Getter
	private ItemStack previousButtonSuccess;
	@Getter
	private ItemStack previousButtonDeny;
	@Getter
	private int previousButtonSlot;

	public CursedPageButton() {
		this.nextButtonSuccess = null;
		this.nextButtonDeny = null;
		this.nextButtonSlot = 999;

		this.previousButtonDeny = null;
		this.previousButtonSuccess = null;
		this.previousButtonSlot = 999;
	}

	public void setNextButtonSuccess(ItemStack item) {
		this.nextButtonSuccess = item;
	}

	public void setNextButtonDeny(ItemStack item) {
		this.nextButtonDeny = item;
	}

	public void setNextButtonSlot(int slot) {
		this.nextButtonSlot = slot;
	}

	public void setPreviousButtonSuccess(ItemStack item) {
		this.previousButtonSuccess = item;
	}

	public void setPreviousButtonDeny(ItemStack item) {
		this.previousButtonDeny = item;
	}

	public void setPreviousButtonSlot(int slot) {
		this.previousButtonSlot = slot;
	}

	public boolean isNextButtonApplicable() {
		return (nextButtonDeny != null && nextButtonSuccess != null && nextButtonSlot != 999);
	}

	public boolean isPreviousButtonApplicable() {
		return (previousButtonDeny != null && previousButtonSuccess != null && previousButtonSlot != 999);
	}
}
