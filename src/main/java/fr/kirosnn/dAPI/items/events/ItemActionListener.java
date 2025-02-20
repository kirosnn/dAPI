package fr.kirosnn.dAPI.items.events;

import fr.kirosnn.dAPI.items.CustomItemManager;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

/**
 * The type Item action listener.
 */
public class ItemActionListener implements Listener {
    private static final NamespacedKey ITEM_ID_KEY = new NamespacedKey("kirosnn", "custom_id");

    /**
     * On item use.
     *
     * @param event the event
     */
    @EventHandler
    public void onItemUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) return;

        String id = item.getItemMeta().getPersistentDataContainer().get(ITEM_ID_KEY, PersistentDataType.STRING);
        if (id == null) return;

        CustomItemManager.getItem(id);
    }
}