package fr.kirosnn.dAPI.items;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Custom item manager.
 */
public class CustomItemManager {
    private static final Map<String, CustomItem> registeredItems = new HashMap<>();

    /**
     * Register item.
     *
     * @param item the item
     */
    public static void registerItem(CustomItem item) {
        registeredItems.put(item.getId(), item);
    }

    /**
     * Gets item.
     *
     * @param id the id
     * @return the item
     */
    public static @Nullable ItemStack getItem(String id) {
        return registeredItems.containsKey(id) ? registeredItems.get(id).create() : null;
    }
}