package me.yuzegod.lobbylevel.Listener;

import me.yuzegod.lobbylevel.*;
import org.bukkit.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.*;
import me.yuzegod.lobbylevel.Event.*;
import me.yuzegod.lobbylevel.Config.*;
import java.util.*;
import org.bukkit.event.inventory.*;
import me.yuzegod.lobbylevel.Util.*;
import me.yuzegod.lobbylevel.Reward.*;
import org.bukkit.command.*;
import org.bukkit.*;
import org.bukkit.inventory.*;

public class PlayerListener implements Listener
{
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        final Account set = Account.get(p);
        if (LobbyLevel.getInstance().getConfig().getBoolean("show-Action", false)) {
            p.setExp(set.getExpFloat());
            p.setLevel(set.getLevel());
        }
    }
    
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent e) {
        final Player p = e.getPlayer();
        Account.remove(p);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerExpChange(final PlayerExpChangeEvent e) {
        if (!LobbyLevel.getInstance().getConfig().getBoolean("show-Action", false)) {
            e.setAmount(0);
        }
    }
    
    @EventHandler
    public void onLevelUp(final LevelUpEvent e) {
        final FileConfig config = LobbyLevel.getInstance().getConfig();
        for (final String msg : config.getStringList("level-up.messages")) {
            e.getPlayer().sendMessage(Log.translate(msg.replace("%level%", String.valueOf(e.getNow()))));
        }
        final String title = config.getString("level-up.title", "&e&l\u270c\u5347\u7ea7\u5566!\u270e").replace("%level%", String.valueOf(e.getNow()));
        final String subtitle = config.getString("level-up.subtitle", "&a\u5f53\u524d\u7b49\u7ea7: &b%level%").replace("%level%", String.valueOf(e.getNow()));
        TitleAPI.sendFullTitle(e.getPlayer(), 20, 40, 20, Log.translate(title), Log.translate(subtitle));
    }
    
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }
        final ItemStack item = e.getCurrentItem();
        final Player player = (Player)e.getWhoClicked();
        if (e.getInventory().getTitle().equals("¡ìb¡ìl\u7b49\u7ea7\u7cfb\u7edf")) {
            e.setCancelled(true);
            if (item == null) {
                return;
            }
            if (item.getType() == Material.AIR) {
                return;
            }
            if (item.getType() == Material.EXP_BOTTLE) {
                if (e.getInventory().getHolder() != null && e.getInventory().getHolder() instanceof PageInventoryHolder) {
                    final PageInventoryHolder holder = (PageInventoryHolder)e.getInventory().getHolder();
                    player.closeInventory();
                    if (RewardManager.reward(player, this.getLevelBySlot(holder.getPage(), e.getRawSlot()))) {
                        Log.send((CommandSender)player, "&a\u6210\u529f\u9886\u53d6\u8be5\u7b49\u7ea7\u793c\u5305!");
                    }
                    RewardManager.openInventory(player, holder.getPage());
                }
            }
            else if (item.getType() == Material.ARROW) {
                final String action = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                if (e.getInventory().getHolder() != null && e.getInventory().getHolder() instanceof PageInventoryHolder) {
                    final PageInventoryHolder holder2 = (PageInventoryHolder)e.getInventory().getHolder();
                    player.closeInventory();
                    final String s;
                    switch (s = action) {
                        case "\u4e0a\u4e00\u9875": {
                            RewardManager.openInventory(player, holder2.getPage() - 1);
                            break;
                        }
                        case "\u4e0b\u4e00\u9875": {
                            RewardManager.openInventory(player, holder2.getPage() + 1);
                            break;
                        }
                        default:
                            break;
                    }
                }
            }
        }
    }
    
    private int getLevelBySlot(final int page, final int slot) {
        int level = (page - 1) * RewardManager.SLOT.length;
        for (int i = 0; i <= slot; ++i) {
            if (this.isSlot(i)) {
                ++level;
            }
        }
        return level;
    }
    
    private boolean isSlot(final int slot) {
        int[] slot2;
        for (int length = (slot2 = RewardManager.SLOT).length, j = 0; j < length; ++j) {
            final int i = slot2[j];
            if (i == slot) {
                return true;
            }
        }
        return false;
    }
}
