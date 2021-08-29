package me.yuzegod.lobbylevel.Util;

import org.bukkit.inventory.*;
import org.bukkit.enchantments.*;
import org.bukkit.inventory.meta.*;
import java.util.*;
import org.bukkit.*;

public class ItemBuilder
{
    private ItemStack itemStack;
    
    public ItemBuilder(final Material material) {
        this(material, 1);
    }
    
    public ItemBuilder(final ItemStack itemStack) {
        this.itemStack = itemStack;
    }
    
    public ItemBuilder(final Material material, final int amount) {
        this.itemStack = new ItemStack(material, amount);
    }
    
    public ItemBuilder(final Material material, final int amount, final byte durability) {
        this.itemStack = new ItemStack(material, amount, (short)durability);
    }
    
    public ItemBuilder clone() {
        return new ItemBuilder(this.itemStack);
    }
    
    public ItemBuilder setDurability(final short durability) {
        this.itemStack.setDurability(durability);
        return this;
    }
    
    public ItemBuilder setUnbreakable(final boolean unbreakable) {
        final ItemMeta meta = this.itemStack.getItemMeta();
        meta.spigot().setUnbreakable(unbreakable);
        this.itemStack.setItemMeta(meta);
        return this;
    }
    
    public ItemBuilder setDisplayName(final String name) {
        final ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }
    
    public ItemBuilder addUnsafeEnchantment(final Enchantment enchantment, final int level) {
        this.itemStack.addUnsafeEnchantment(enchantment, level);
        return this;
    }
    
    public ItemBuilder removeEnchantment(final Enchantment enchantment) {
        this.itemStack.removeEnchantment(enchantment);
        return this;
    }
    
    public ItemBuilder setSkullOwner(final String owner) {
        final SkullMeta im = (SkullMeta)this.itemStack.getItemMeta();
        im.setOwner(owner);
        this.itemStack.setItemMeta((ItemMeta)im);
        return this;
    }
    
    public ItemBuilder addEnchantment(final Enchantment enchantment, final int level) {
        final ItemMeta im = this.itemStack.getItemMeta();
        im.addEnchant(enchantment, level, true);
        this.itemStack.setItemMeta(im);
        return this;
    }
    
    public ItemBuilder setInfinityDurability() {
        this.itemStack.setDurability((short)32767);
        return this;
    }
    
    public ItemBuilder setLore(final String... lore) {
        final ItemMeta im = this.itemStack.getItemMeta();
        final List<String> lores = new ArrayList<String>();
        for (final String line : lore) {
            lores.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        im.setLore((List)lores);
        this.itemStack.setItemMeta(im);
        return this;
    }
    
    public ItemBuilder setDyeColor(final DyeColor color) {
        this.itemStack.setDurability((short)color.getData());
        return this;
    }
    
    public ItemStack build() {
        return this.itemStack;
    }
}
