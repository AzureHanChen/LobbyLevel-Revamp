package me.yuzegod.lobbylevel;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.plugin.*;
import org.bukkit.entity.*;

public class Placeholders extends PlaceholderExpansion
{
    public Placeholders(final Plugin plugin) {}

    @Override
    public String getAuthor() {
        return "yuze, AzureHanChen";
    }

    @Override
    public String getIdentifier() {
        return "lobbylevel";
    }

    @Override
    public String getVersion() {
        return "1.0-Revamp";
    }

    @Override
    public String onPlaceholderRequest(Player player, String arg) {
        String string = "NULL";
        if (player == null) {
            return string;
        }
        final Account info = Account.get(player);
        if (arg.equalsIgnoreCase("Level")) {
            final int i = info.getLevel();
            string = String.valueOf(i);
        }
        if (arg.equalsIgnoreCase("Exp")) {
            final int i = info.getExp();
            string = String.valueOf(i);
        }
        if (arg.equalsIgnoreCase("TotalExp")) {
            final int i = info.getTotalExp(info.getLevel());
            string = String.valueOf(i);
        }
        if (arg.equalsIgnoreCase("UpExp")) {
            final int i = info.getUpExp();
            string = String.valueOf(i);
        }
        if (arg.equalsIgnoreCase("BaiFenBi")) {
            string = info.getLevelBaiFenBi();
        }
        if (arg.equalsIgnoreCase("JinDu")) {
            string = info.getLevelJinDu();
        }
        return string;
    }
}
