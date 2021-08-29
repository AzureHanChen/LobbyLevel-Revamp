package me.yuzegod.lobbylevel.Event;

import org.bukkit.event.*;
import org.bukkit.entity.*;

public class ExpChangeEvent extends Event implements Cancellable
{
    private static final HandlerList handlers;
    private boolean isCancelled;
    private Player player;
    private int amount;
    
    static {
        handlers = new HandlerList();
    }
    
    public ExpChangeEvent(final Player player, final int amount) {
        this.isCancelled = false;
        this.player = player;
        this.amount = amount;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public int getAmount() {
        return this.amount;
    }
    
    public void setAmount(final int amount) {
        this.amount = amount;
    }
    
    public HandlerList getHandlers() {
        return ExpChangeEvent.handlers;
    }
    
    public static HandlerList getHandlerList() {
        return ExpChangeEvent.handlers;
    }
    
    public boolean isCancelled() {
        return this.isCancelled;
    }
    
    public void setCancelled(final boolean celled) {
        this.isCancelled = celled;
    }
}
