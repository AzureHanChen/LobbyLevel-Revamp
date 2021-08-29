package me.yuzegod.lobbylevel.Command;

import org.bukkit.*;
import me.yuzegod.lobbylevel.Util.*;
import me.yuzegod.lobbylevel.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import me.yuzegod.lobbylevel.Reward.*;

public class Commands
{
    @Cmd(value = "giveexp", description = "<\u73a9\u5bb6> <\u7ecf\u9a8c\u503c> \u7ed9\u4e88\u73a9\u5bb6\u7ecf\u9a8c\u503c", minArgs = 3, permission = "LobbyLevel.admin")
    public void giveexp(final DefaultCommand defcmd) {
        final CommandSender sender = defcmd.getSender();
        final String[] args = defcmd.getArgs();
        final Player p = Bukkit.getPlayer(args[1]);
        int amount = 0;
        if (p == null) {
            Log.send(sender, "&c\u8be5\u73a9\u5bb6\u4e0d\u5728\u7ebf!");
            return;
        }
        try {
            amount = Integer.valueOf(args[2]);
        }
        catch (NumberFormatException e) {
            Log.send(sender, "&c\u8bf7\u8f93\u5165\u6709\u6548\u7684\u6570\u91cf!");
            return;
        }
        if (amount <= 0 || amount >= 1400) {
            Log.send(sender, "&c\u8bf7\u8f93\u5165\u6709\u6548\u7684\u6570\u91cf!");
            return;
        }
        Account.get(p).giveExp(amount);
        Log.send(sender, "&a\u5df2\u5c06&c " + amount + " &a\u7ecf\u9a8c\u7ed9\u4e88\u73a9\u5bb6 &b" + p.getName());
    }
    
    @Cmd(value = "rewards", description = "\u6253\u5f00\u793c\u5305\u754c\u9762", minArgs = 1, permission = "LobbyLevel.default", onlyPlayer = true)
    public void rewards(final DefaultCommand defcmd) {
        final Player player = (Player)defcmd.getSender();
        RewardManager.openInventory(player, 1);
    }
    
    @Cmd(value = "reset", description = "<\u73a9\u5bb6> \u91cd\u7f6e\u73a9\u5bb6\u7b49\u7ea7", minArgs = 2, permission = "LobbyLevel.admin")
    public void reset(final DefaultCommand defcmd) {
        final CommandSender sender = defcmd.getSender();
        final String[] args = defcmd.getArgs();
        final Player p = Bukkit.getPlayer(args[1]);
        if (p == null) {
            Log.send(sender, "&c\u8be5\u73a9\u5bb6\u4e0d\u5728\u7ebf!");
            return;
        }
        final Account set = Account.get(p);
        set.reset();
        p.setLevel(1);
        p.setExp(0.0f);
        Log.send(sender, "&a\u5df2\u5c06\u73a9\u5bb6&c " + p.getName() + " &a\u7684\u7b49\u7ea7\u91cd\u7f6e!");
    }
}
