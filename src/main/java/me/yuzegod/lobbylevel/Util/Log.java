package me.yuzegod.lobbylevel.Util;

import java.util.logging.*;
import org.bukkit.command.*;
import me.yuzegod.lobbylevel.*;
import org.bukkit.*;
import java.util.*;

public class Log
{
    private static boolean initialized;
    private static Logger logger;
    private static CommandSender console;
    private static String prefix;
    
    static {
        Log.initialized = false;
        Log.console = null;
    }
    
    public static boolean init() {
        if (Log.initialized) {
            return false;
        }
        Log.logger = LobbyLevel.getInstance().getLogger();
        Log.console = (CommandSender)Bukkit.getConsoleSender();
        Log.prefix = translate(LobbyLevel.getInstance().getConfig().getString("plugin-prefix"));
        return true;
    }
    
    public static void console(final String msg) {
        Log.console.sendMessage(String.valueOf(Log.prefix) + translate(msg));
    }
    
    public static void console(final List<String> msg) {
        for (final String line : msg) {
            console(line);
        }
    }
    
    public static void info(final String msg) {
        Log.logger.info(msg);
    }
    
    public static void warning(final String msg) {
        Log.logger.warning(msg);
    }
    
    public static void send(final CommandSender sender, final String msg) {
        sender.sendMessage(String.valueOf(Log.prefix) + translate(msg));
    }
    
    public static void send(final CommandSender sender, final List<String> msg) {
        for (final String line : msg) {
            send(sender, line);
        }
    }
    
    public static String translate(final String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
    
    public static List<String> translate(final List<String> text) {
        final List<String> list = new ArrayList<String>();
        for (final String line : text) {
            list.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        return list;
    }
}
