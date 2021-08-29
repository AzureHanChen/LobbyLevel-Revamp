package me.yuzegod.lobbylevel;

import org.bukkit.plugin.java.*;
import me.yuzegod.lobbylevel.Config.*;
import me.yuzegod.lobbylevel.Database.*;
import org.bukkit.plugin.*;
import me.yuzegod.lobbylevel.Reward.*;
import me.yuzegod.lobbylevel.Util.*;
import org.bukkit.*;
import me.yuzegod.lobbylevel.Listener.*;
import org.bukkit.event.*;
import me.yuzegod.lobbylevel.Command.*;
import org.bukkit.configuration.file.*;

public class LobbyLevel extends JavaPlugin
{
    private static LobbyLevel instance;
    private FileConfig config;
    private DataBase database;
    public static KeyValue KV;
    public static String TABLENAME;
    
    static {
        LobbyLevel.KV = new KeyValue("name", "VARCHAR(16) PRIMARY KEY").add("exp", "INTEGER").add("level", "INTEGER").add("rewards", "VARCHAR(999)");
        LobbyLevel.TABLENAME = "lobbylevel";
    }
    
    public void onEnable() {
        LobbyLevel.instance = this;
        RewardManager.loadRewards(this.config = new FileConfig((Plugin)this));
        Log.init();
        Account.init();
        this.database = DataBase.create(this.config.getConfigurationSection("MySQL"));
        if (!this.database.isTableExists(LobbyLevel.TABLENAME)) {
            this.database.createTables(LobbyLevel.TABLENAME, LobbyLevel.KV, null);
        }
        Bukkit.getPluginManager().registerEvents((Listener)new PlayerListener(), (Plugin)this);
        new CommandHandler("LobbyLevel");
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            if ((new Placeholders(this)).register()){
                Bukkit.getConsoleSender().sendMessage("§f[§eLobbyLevel§f] §f成功注册PlaceholderAPI变量");
            }
            else {
                Bukkit.getConsoleSender().sendMessage("§f[§eLobbyLevel§f] §c未能成功注册PlaceholderAPI变量");
            }
        }
        else {
            Bukkit.getConsoleSender().sendMessage("§f[§eLobbyLevel§f] §c未能成功注册PlaceholderAPI变量");
        }
        Bukkit.getConsoleSender().sendMessage(" ");
        Bukkit.getConsoleSender().sendMessage(" §fLobbyLevel §7Revamp §f(§a"+getDescription().getVersion()+"§f)");
        Bukkit.getConsoleSender().sendMessage(" ");
        Bukkit.getConsoleSender().sendMessage(" §fAuthor §eYuze §7| §fEditor §eAzureHanChen");
        Bukkit.getConsoleSender().sendMessage(" ");
    }
    
    public static LobbyLevel getInstance() {
        return LobbyLevel.instance;
    }
    
    public FileConfig getConfig() {
        return this.config;
    }
    
    public DataBase getDataBase() {
        return this.database;
    }
}
