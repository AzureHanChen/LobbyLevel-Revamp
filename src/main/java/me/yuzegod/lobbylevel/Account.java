package me.yuzegod.lobbylevel;

import org.bukkit.entity.*;
import org.bukkit.scheduler.*;
import me.yuzegod.lobbylevel.Database.*;
import java.util.*;
import java.text.*;
import org.bukkit.*;
import org.bukkit.event.*;
import me.yuzegod.lobbylevel.Event.*;
import org.bukkit.plugin.*;

public class Account
{
    private static HashMap<Player, Account> accounts;
    private static boolean initialized;
    private static BukkitRunnable refreshTask;
    private DataBase database;
    private Player player;
    private int level;
    private int exp;
    
    static {
        Account.accounts = new HashMap<Player, Account>();
        Account.initialized = false;
    }
    
    private Account(final Player player) {
        this.player = player;
        this.database = LobbyLevel.getInstance().getDataBase();
        if (!this.database.isValueExists(LobbyLevel.TABLENAME, LobbyLevel.KV, new KeyValue("name", player.getName()))) {
            this.database.dbInsert(LobbyLevel.TABLENAME, new KeyValue("name", player.getName()).add("exp", 0).add("level", 1).add("rewards", ""));
        }
        this.refresh();
    }
    
    public void refresh() {
        for (final KeyValue kv : this.database.dbSelect(LobbyLevel.TABLENAME, LobbyLevel.KV, new KeyValue("name", this.player.getName()))) {
            this.exp = Integer.parseInt(kv.getString("exp"));
            this.level = Integer.parseInt(kv.getString("level"));
        }
        this.setExp(this.exp);
        this.setLevel(this.level);
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public void reset() {
        this.setLevel(1);
        this.setExp(0);
    }
    
    public int getLevel() {
        return this.level;
    }
    
    public int getExp() {
        return this.exp;
    }
    
    public int getUpExp() {
        return this.getTotalExp(this.level) - this.exp;
    }
    
    public int getTotalExp(final int Level) {
        return 1000 + Level * 200;
    }
    
    public float getExpFloat() {
        float f = this.exp / (float)this.getTotalExp(this.level);
        if (f > 1.0f) {
            f = 0.99f;
        }
        else if (f < 0.0f) {
            f = 0.0f;
        }
        return f;
    }
    
    public String getLevelJinDu() {
        final double i = new Double(this.exp / (double)this.getTotalExp(this.level)) * 10.0;
        final StringBuilder stb1 = new StringBuilder("¡ìb");
        for (int a = 0; a < (int)i; ++a) {
            stb1.append("\u25a0");
        }
        final StringBuilder stb2 = new StringBuilder("¡ì7");
        for (int b = 0; b < 10 - (int)i; ++b) {
            stb2.append("\u25a0");
        }
        return String.valueOf(stb1.toString()) + stb2.toString();
    }
    
    public String getLevelBaiFenBi() {
        final double i = new Double(this.exp / (double)this.getTotalExp(this.level));
        final NumberFormat nt = NumberFormat.getPercentInstance();
        nt.setMinimumFractionDigits(1);
        return nt.format(i);
    }
    
    public void setLevel(final int i) {
        if (i <= 0) {
            return;
        }
        this.level = i;
        this.database.dbUpdate(LobbyLevel.TABLENAME, new KeyValue("level", i), new KeyValue("name", this.player.getName()));
        if (LobbyLevel.getInstance().getConfig().getBoolean("show-Action", false)) {
            this.player.setLevel(this.level);
        }
    }
    
    private void setExp(final int i) {
        if (i < 0) {
            return;
        }
        this.exp = i;
        this.database.dbUpdate(LobbyLevel.TABLENAME, new KeyValue("exp", i), new KeyValue("name", this.player.getName()));
        if (LobbyLevel.getInstance().getConfig().getBoolean("show-Action", false)) {
            this.player.setExp(this.getExpFloat());
        }
    }
    
    public void giveExp(final int key) {
        if (key <= 0 || key >= 1400) {
            return;
        }
        final ExpChangeEvent expchange = new ExpChangeEvent(this.player, key);
        Bukkit.getPluginManager().callEvent((Event)expchange);
        if (expchange.isCancelled()) {
            return;
        }
        int amount = expchange.getAmount();
        if (amount >= 1400) {
            amount = 1399;
        }
        if (amount < 1400) {
            if (amount >= this.getUpExp()) {
                final LevelUpEvent event = new LevelUpEvent(this.player, this.level, this.level + 1);
                Bukkit.getPluginManager().callEvent((Event)event);
                this.setExp(amount - this.getUpExp());
                this.setLevel(this.level + 1);
            }
            else {
                this.setExp(this.exp + amount);
            }
        }
        if (LobbyLevel.getInstance().getConfig().getBoolean("show-Action", false)) {
            this.player.setExp(this.getExpFloat());
        }
    }
    
    public static boolean init() {
        if (Account.initialized) {
            return false;
        }
        (Account.refreshTask = new BukkitRunnable() {
            public void run() {
                for (final Account set : Account.accounts.values()) {
                    set.refresh();
                }
            }
        }).runTaskTimer((Plugin)LobbyLevel.getInstance(), 1200L, 1200L);
        return true;
    }
    
    public static Account get(final Player player) {
        Account set = null;
        if (Account.accounts.containsKey(player)) {
            return Account.accounts.get(player);
        }
        set = new Account(player);
        Account.accounts.put(player, set);
        return set;
    }
    
    public static boolean remove(final Player player) {
        if (Account.accounts.containsKey(player)) {
            Account.accounts.remove(player);
        }
        return false;
    }
}
