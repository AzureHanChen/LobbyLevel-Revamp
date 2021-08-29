package me.yuzegod.lobbylevel.Event;

import org.bukkit.event.*;
import org.bukkit.entity.*;

public class LevelUpEvent extends Event
{
    private static final HandlerList handlers;
    private Player player;
    private int before;
    private int now;
    
    static {
        handlers = new HandlerList();
    }
    
    public LevelUpEvent(final Player player, final int before, final int now) {
        this.player = player;
        this.before = before;
        this.now = now;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public int getBefore() {
        return this.before;
    }
    
    public int getNow() {
        return this.now;
    }
    
    public HandlerList getHandlers() {
        return LevelUpEvent.handlers;
    }
    
    public static HandlerList getHandlerList() {
        return LevelUpEvent.handlers;
    }
}
