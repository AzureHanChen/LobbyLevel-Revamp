package me.yuzegod.lobbylevel.Command;

import me.yuzegod.lobbylevel.*;
import org.bukkit.command.*;
import me.yuzegod.lobbylevel.Util.*;
import java.lang.reflect.*;
import org.bukkit.entity.*;

public class CommandHandler implements CommandExecutor
{
    private String CMD_USE;
    private String CMD_ERR;
    private String NO_PERM;
    private String ONLY_PLAYER;
    private String ONLY_CONSOLE;
    private String name;
    PluginCommand pcmd;
    
    public CommandHandler(final String name) {
        this.CMD_USE = "&6\u4f7f\u7528\u65b9\u6cd5: &e/%s %s %s";
        this.CMD_ERR = "&6\u9519\u8bef\u539f\u56e0: &4\u547d\u4ee4\u53c2\u6570\u4e0d\u6b63\u786e!";
        this.NO_PERM = "&c\u4f60\u9700\u8981\u6709 %s \u7684\u6743\u9650\u624d\u80fd\u6267\u884c\u6b64\u547d\u4ee4!";
        this.ONLY_PLAYER = "&c\u63a7\u5236\u53f0\u65e0\u6cd5\u4f7f\u7528\u6b64\u547d\u4ee4(&4\u8bf7\u5728\u6e38\u620f\u5185\u6267\u884c&c)!";
        this.ONLY_CONSOLE = "&c\u73a9\u5bb6\u65e0\u6cd5\u4f7f\u7528\u6b64\u547d\u4ee4(&4\u8bf7\u4f7f\u7528\u63a7\u5236\u53f0\u6267\u884c&c)!";
        this.name = name;
        (this.pcmd = LobbyLevel.getInstance().getCommand(name)).setExecutor((CommandExecutor)this);
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length == 0) {
            return true;
        }
        final DefaultCommand defcmd = new DefaultCommand(sender, command, label, args);
        return this.execute(defcmd);
    }
    
    public String getName() {
        return this.name;
    }
    
    public boolean execute(final DefaultCommand defcmd) {
        if (this.check(defcmd)) {
            final Method method = this.getMethodBySubCmd(defcmd.getArgs()[0]);
            if (method != null) {
                try {
                    method.invoke(Commands.class.newInstance(), defcmd);
                }
                catch (IllegalAccessException e) {
                    Log.warning("\u73a9\u5bb6 " + defcmd.getSender().getName() + " \u6267\u884c\u547d\u4ee4 " + defcmd.getArgs()[0] + " \u65f6\u51fa\u9519: " + e.getMessage());
                }
                catch (IllegalArgumentException e2) {
                    Log.warning("\u73a9\u5bb6 " + defcmd.getSender().getName() + " \u6267\u884c\u547d\u4ee4 " + defcmd.getArgs()[0] + " \u65f6\u51fa\u9519: " + e2.getMessage());
                }
                catch (InvocationTargetException e3) {
                    Log.warning("\u73a9\u5bb6 " + defcmd.getSender().getName() + " \u6267\u884c\u547d\u4ee4 " + defcmd.getArgs()[0] + " \u65f6\u51fa\u9519: " + e3.getMessage());
                }
                catch (InstantiationException e4) {
                    Log.warning("\u73a9\u5bb6 " + defcmd.getSender().getName() + " \u6267\u884c\u547d\u4ee4 " + defcmd.getArgs()[0] + " \u65f6\u51fa\u9519: " + e4.getMessage());
                }
                return true;
            }
        }
        return false;
    }
    
    public boolean check(final DefaultCommand defcmd) {
        final Method method = this.getMethodBySubCmd(defcmd.getArgs()[0]);
        if (method != null) {
            final Cmd cmd = method.getAnnotation(Cmd.class);
            if (cmd != null) {
                final CommandSender sender = defcmd.getSender();
                if (defcmd.getArgs().length < cmd.minArgs()) {
                    Log.send(sender, this.CMD_ERR);
                    Log.send(sender, String.format(this.CMD_USE, defcmd.getLabel(), cmd.value(), cmd.description()));
                    return false;
                }
                if (sender instanceof Player && cmd.onlyConsole()) {
                    Log.send(sender, this.ONLY_CONSOLE);
                    return false;
                }
                if (!(sender instanceof Player) && cmd.onlyPlayer()) {
                    Log.send(sender, this.ONLY_PLAYER);
                    return false;
                }
                final String perm = cmd.permission();
                if (perm != null && !sender.hasPermission(perm)) {
                    Log.send(sender, String.format(this.NO_PERM, perm));
                    return false;
                }
                return true;
            }
        }
        return false;
    }
    
    public Method getMethodBySubCmd(final String subcmd) {
        final Method[] methods = Commands.class.getMethods();
        Method[] array;
        for (int length = (array = methods).length, i = 0; i < length; ++i) {
            final Method method = array[i];
            final Cmd cmd = method.getAnnotation(Cmd.class);
            if (cmd != null && cmd.value().equalsIgnoreCase(subcmd)) {
                return method;
            }
        }
        return null;
    }
}
