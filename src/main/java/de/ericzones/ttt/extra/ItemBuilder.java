// Created by Eric B. 30.05.2020 17:38
package de.ericzones.ttt.extra;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

public class ItemBuilder {

    private ItemStack item;
    private ItemMeta itemMeta;
    private SkullMeta skullMeta;

    public ItemBuilder(Material material) {
        item = new ItemStack(material);
        itemMeta = item.getItemMeta();
    }

    public ItemBuilder(Material material, int id) {
        item = new ItemStack(material, 1, (short) id);
        itemMeta = item.getItemMeta();
    }

    public ItemBuilder(Material material, int id, String owner) {
        item = new ItemStack(material, 1, (short) id);
        skullMeta = (SkullMeta) item.getItemMeta();
        skullMeta.setOwner(owner);
        item.setItemMeta(skullMeta);
        itemMeta = item.getItemMeta();
    }

    public ItemBuilder setDisplayName(String name) {
        itemMeta.setDisplayName(name);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        itemMeta.setLore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        itemMeta.spigot().setUnbreakable(unbreakable);
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        return this;
    }

    public ItemBuilder setEnchanted(boolean enchanted) {
        if(enchanted) {
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            return this;
        }
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(itemMeta);
        return item;
    }

}
