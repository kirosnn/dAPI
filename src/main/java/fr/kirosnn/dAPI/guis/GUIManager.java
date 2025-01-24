package fr.kirosnn.dAPI.guis;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.jetbrains.annotations.NotNull;

public class GUIManager implements Listener {

    /**
     * On inventory click.
     *
     * @param event the event
     */
    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof BaseGUI) {
            BaseGUI baseGUI = (BaseGUI) event.getView().getTopInventory().getHolder();
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
     * On inventory drag.
     *
     * @param event the event
     */
    @EventHandler
    public void onInventoryDrag(@NotNull InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof BaseGUI) {
            event.setCancelled(true);
        }
    }
}