package me.yuzegod.lobbylevel.Reward;

import org.bukkit.entity.*;
import org.bukkit.*;
import org.bukkit.inventory.*;
import java.util.*;
import org.bukkit.command.*;
import me.yuzegod.lobbylevel.*;
import me.yuzegod.lobbylevel.Database.*;
import me.yuzegod.lobbylevel.Config.*;
import me.yuzegod.lobbylevel.Util.*;
import org.bukkit.configuration.*;

public class RewardManager
{
    private static final List<Reward> REWARDS;
    private static final Map<OfflinePlayer, List<Reward>> CACHES;
    public static final int[] SLOT;
    
    static {
        REWARDS = new ArrayList<Reward>();
        CACHES = new HashMap<OfflinePlayer, List<Reward>>();
        SLOT = new int[] { 10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43 };
    }
    
    public static void openInventory(final Player player, int page) {
        if (page > getMaxPages()) {
            page = getMaxPages();
        }
        if (page < 1) {
            page = 1;
        }
        final Account account = Account.get(player);
        final PageInventoryHolder holder = new PageInventoryHolder(page);
        final Inventory inv = Bukkit.createInventory((InventoryHolder)holder, 54, "¡ìb¡ìl\u7b49\u7ea7\u7cfb\u7edf");
        holder.setInventory(inv);
        int i = 0;
        final int from = RewardManager.SLOT.length * (page - 1) + 1;
        final int to = RewardManager.SLOT.length * page;
        final Reward[] rewards = RewardManager.REWARDS.toArray(new Reward[0]);
        for (int h = from; h <= to && i <= RewardManager.SLOT.length && h <= rewards.length; ++i, ++h) {
            final Reward reward = rewards[h - 1];
            if (reward != null) {
                final boolean unlocked = hasReward((OfflinePlayer)player, reward.getLevel());
                final boolean moreThanLevel = account.getLevel() >= reward.getLevel();
                final List<String> lore = new ArrayList<String>();
                for (final String line : reward.getDescription()) {
                    lore.add("¡ìf" + ChatColor.stripColor(line));
                }
                lore.add(" ");
                Material type = Material.GLASS_BOTTLE;
                if (moreThanLevel) {
                    if (!unlocked) {
                        type = Material.EXP_BOTTLE;
                    }
                    lore.add(unlocked ? "¡ìc\u5df2\u9886\u53d6" : "¡ìc\u672a\u9886\u53d6");
                }
                else {
                    lore.add("¡ìc\u9700\u8981\u66f4\u9ad8\u7b49\u7ea7,\u65e0\u6cd5\u9886\u53d6!");
                }
                inv.setItem(RewardManager.SLOT[i], new ItemBuilder(type, 1).setDisplayName(reward.getName()).setLore((String[])lore.toArray(new String[0])).build());
            }
        }
        if (getMaxPages() > 1) {
            if (page == 1) {
                inv.setItem(53, new ItemBuilder(Material.ARROW).setDisplayName("¡ìa\u4e0b\u4e00\u9875").setLore("¡ìe\u9875\u5e8f\u53f7: " + (page + 1)).build());
            }
            else if (page > 1 && page < getMaxPages()) {
                inv.setItem(45, new ItemBuilder(Material.ARROW).setDisplayName("¡ìa\u4e0a\u4e00\u9875").setLore("¡ìe\u9875\u5e8f\u53f7: " + (page - 1)).build());
                inv.setItem(53, new ItemBuilder(Material.ARROW).setDisplayName("¡ìa\u4e0b\u4e00\u9875").setLore("¡ìe\u9875\u5e8f\u53f7: " + (page + 1)).build());
            }
            else if (page == getMaxPages()) {
                inv.setItem(45, new ItemBuilder(Material.ARROW).setDisplayName("¡ìa\u4e0a\u4e00\u9875").setLore("¡ìe\u9875\u5e8f\u53f7: " + (page - 1)).build());
            }
        }
        player.openInventory(inv);
    }
    
    private static int getMaxPages() {
        final int i = RewardManager.REWARDS.size();
        if (i % RewardManager.SLOT.length == 0) {
            return i / RewardManager.SLOT.length;
        }
        final double j = i / RewardManager.SLOT.length;
        final int h = (int)Math.floor(j * 100.0) / 100;
        return h + 1;
    }
    
    public static boolean reward(final Player player, final int level) {
        if (getByLevel(level) == null) {
            return false;
        }
        final Reward reward = getByLevel(level);
        for (final String cmd : reward.getCommands()) {
            Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName()));
        }
        final List<Reward> list = fetchRewards((OfflinePlayer)player);
        list.add(reward);
        saveRewards((OfflinePlayer)player, list);
        return true;
    }
    
    public static List<Reward> fetchRewards(final OfflinePlayer player) {
        if (RewardManager.CACHES.containsKey(player)) {
            return RewardManager.CACHES.get(player);
        }
        final List<Reward> list = new ArrayList<Reward>();
        if (!LobbyLevel.getInstance().getDataBase().isValueExists(LobbyLevel.TABLENAME, LobbyLevel.KV, new KeyValue("name", player.getName()))) {
            LobbyLevel.getInstance().getDataBase().dbInsert(LobbyLevel.TABLENAME, new KeyValue("name", player.getName()));
        }
        final Iterator<KeyValue> iterator = LobbyLevel.getInstance().getDataBase().dbSelect(LobbyLevel.TABLENAME, new KeyValue("rewards", "VARCHAR"), new KeyValue("name", player.getName())).iterator();
        if (iterator.hasNext()) {
            final KeyValue kv = iterator.next();
            if (kv.getString("rewards").equals("")) {
                return new ArrayList<Reward>();
            }
            String[] split;
            for (int length = (split = kv.getString("rewards").split("#")).length, i = 0; i < length; ++i) {
                final String s = split[i];
                if (!s.equals("")) {
                    list.add(getByLevel(Integer.parseInt(s)));
                }
            }
        }
        RewardManager.CACHES.put(player, list);
        return list;
    }
    
    public static int saveRewards(final OfflinePlayer player, final List<Reward> rewards) {
        String line = "";
        for (final Reward reward : rewards) {
            line = String.valueOf(line) + reward.getLevel() + "#";
        }
        RewardManager.CACHES.put(player, rewards);
        return LobbyLevel.getInstance().getDataBase().dbUpdate(LobbyLevel.TABLENAME, new KeyValue("name", player.getName()).add("rewards", line), new KeyValue("name", player.getName()));
    }
    
    public static boolean hasReward(final OfflinePlayer player, final int level) {
        final List<Reward> list = RewardManager.CACHES.getOrDefault(player, fetchRewards(player));
        for (final Reward reward : list) {
            if (reward.getLevel() == level) {
                return true;
            }
        }
        return false;
    }
    
    public static Reward getByLevel(final int level) {
        for (final Reward reward : RewardManager.REWARDS) {
            if (reward.getLevel() == level) {
                return reward;
            }
        }
        return null;
    }
    
    public static void loadRewards(final FileConfig config) {
        final ConfigurationSection sec = config.getConfigurationSection("rewards");
        for (final String s : sec.getKeys(false)) {
            boolean isNumber = false;
            int level = 1;
            try {
                level = Integer.parseInt(s);
                isNumber = true;
            }
            catch (NumberFormatException ex) {}
            if (!isNumber) {
                continue;
            }
            final ConfigurationSection sec2 = sec.getConfigurationSection(s);
            final String name = Log.translate(sec2.getString("name", s));
            final List<String> description = Log.translate(sec2.getStringList("description"));
            final List<String> commands = (List<String>)sec2.getStringList("commands");
            RewardManager.REWARDS.add(new Reward(level, name, description, commands));
        }
    }
}
