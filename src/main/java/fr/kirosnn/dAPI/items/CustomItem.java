package fr.kirosnn.dAPI.items;

import org.bukkit.inventory.ItemStack;

/**
 * The interface Custom item.
 */
public interface CustomItem {
    /**
     * Gets id.
     *
     * @return the id
     */
    String getId();

    /**
     * Create item stack.
     *
     * @return the item stack
     */
    ItemStack create();
}