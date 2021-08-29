package me.yuzegod.lobbylevel.Util;

import org.bukkit.inventory.*;

public class PageInventoryHolder implements InventoryHolder
{
    private Inventory inventory;
    private int page;
    
    public PageInventoryHolder(final int page) {
        this.page = page;
    }
    
    public Inventory getInventory() {
        return this.inventory;
    }
    
    public void setInventory(final Inventory inventory) {
        this.inventory = inventory;
    }
    
    public int getPage() {
        return this.page;
    }
}
