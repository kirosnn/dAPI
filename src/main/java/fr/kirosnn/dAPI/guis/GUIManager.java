package fr.kirosnn.dAPI.guis;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Gestionnaire global pour les événements de GUI.
 * Empêche toute interaction illégitime avec les inventaires gérés.
 */
public class GUIManager implements Listener {

    /**
     * Capture les clics dans les inventaires et empêche toute modification non autorisée.
     *
     * @param event L'événement de clic.
     */
    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof BaseGUI baseGUI) {
            event.setCancelled(true);

            if (event.getClickedInventory() != null && event.getClickedInventory().equals(event.getView().getTopInventory())) {
                baseGUI.handleClick(event);
            } else {
                event.setCancelled(true);
            }

            if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY
                    || event.getAction() == InventoryAction.HOTBAR_SWAP
                    || event.getClick() == ClickType.MIDDLE) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Capture les drag and drop dans les inventaires et empêche toute modification non autorisée.
     *
     * @param event L'événement de drag and drop.
     */
    @EventHandler
    public void onInventoryDrag(@NotNull InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof BaseGUI) {
            event.setCancelled(true);
        }
    }
}