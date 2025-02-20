package fr.kirosnn.dAPI.items;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

/**
 * The type Item data utils.
 */
public class ItemDataUtils {
    /**
     * Has custom data boolean.
     *
     * @param item the item
     * @param key  the key
     * @return the boolean
     */
    public static boolean hasCustomData(ItemStack item, NamespacedKey key) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.STRING);
    }

    /**
     * Gets custom data.
     *
     * @param item the item
     * @param key  the key
     * @return the custom data
     */
    public static String getCustomData(ItemStack item, NamespacedKey key) {
        if (item == null || !item.hasItemMeta()) return null;
        return item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
    }
}