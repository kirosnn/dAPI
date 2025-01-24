package fr.kirosnn.dAPI.guis;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

    public abstract class BaseGUI implements Listener {

        private final String title;
        private final int rows;
        private final Inventory inventory;
        private final Map<Integer, Consumer<InventoryClickEvent>> actions;

        /**
         * Constructor for the GUI.
         *
         * @param title The title of the inventory.
         * @param rows  The number of rows (1 to 6).
         */
        public BaseGUI(String title, int rows) {
            this.title = title;
            this.rows = rows;
            this.inventory = Bukkit.createInventory(null, rows * 9, title);
            this.actions = new HashMap<>();
            Bukkit.getPluginManager().registerEvents(this, Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("dCore")));
        }

        /**
         * Sets an item in multiple slots with an action and full configuration.
         *
         * @param slots    The slots.
         * @param item     The item to place.
         * @param action   The action to execute on click.
         * @param enchants Enchantments to add (null if no enchantments).
         * @param flags    Item flags to add (null if no flags).
         * @param lore     The lore to set (null if no lore).
         */
        public void setItems(@NotNull List<Integer> slots, @NotNull ItemStack item, Consumer<InventoryClickEvent> action,
                             Map<Enchantment, Integer> enchants, List<ItemFlag> flags, List<String> lore) {
            for (int slot : slots) {
                setItem(slot, item, action, enchants, flags, lore);
            }
        }

        /**
         * Sets an item in a given slot with full configuration.
         *
         * @param slot     The slot (0 to rows * 9 - 1).
         * @param item     The item to place.
         * @param action   The action to execute on click (null if no action).
         * @param enchants Enchantments to add (null if no enchantments).
         * @param flags    Item flags to add (null if no flags).
         * @param lore     The lore to set (null if no lore).
         */
        public void setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> action,
                            Map<Enchantment, Integer> enchants, List<ItemFlag> flags, List<String> lore) {
            configureItem(item, enchants, flags, lore);

            inventory.setItem(slot, item);

            if (action != null) {
                actions.put(slot, action);
            }
        }

        /**
         * Predefined actions: Close the menu.
         *
         * @return the consumer
         */
        public static @NotNull Consumer<InventoryClickEvent> closeAction() {
            return event -> event.getWhoClicked().closeInventory();
        }

        /**
         * Predefined actions: Execute a command.
         *
         * @param command the command
         * @return the consumer
         */
        public static @NotNull Consumer<InventoryClickEvent> commandAction(String command) {
            return event -> {
                Player player = (Player) event.getWhoClicked();
                player.performCommand(command);
                player.closeInventory();
            };
        }

        /**
         * Predefined actions: Open a website with a custom message.
         *
         * @param url     the URL
         * @param message the message
         * @return the consumer
         */
        public static @NotNull Consumer<InventoryClickEvent> openWebsiteAction(String url, String message) {
            return event -> {
                Player player = (Player) event.getWhoClicked();
                player.closeInventory();
                player.sendMessage(message.replace("{url}", url));
            };
        }

        /**
         * Predefined actions: Open another GUI.
         *
         * @param gui    the GUI
         * @param player the player
         * @return the consumer
         */
        public static @NotNull Consumer<InventoryClickEvent> openGUIAction(BaseGUI gui, Player player) {
            return event -> gui.open(player);
        }

        /**
         * Sets an item with an action that toggles between two items when clicked,
         * allowing configuration of enchantments, flags, and lore.
         *
         * @param slot              The slot where the item will be placed.
         * @param initialItem       The initial item to place.
         * @param alternateItem     The item that replaces the initial item on click.
         * @param additionalAction  An additional action to execute on click (can be null).
         * @param initialEnchants   Enchantments for the initial item.
         * @param alternateEnchants Enchantments for the alternate item.
         * @param initialFlags      Flags for the initial item.
         * @param alternateFlags    Flags for the alternate item.
         * @param initialLore       Lore for the initial item.
         * @param alternateLore     Lore for the alternate item.
         */
        public void setItemWithToggleAction(
                int slot,
                ItemStack initialItem,
                ItemStack alternateItem,
                Consumer<InventoryClickEvent> additionalAction,
                Map<Enchantment, Integer> initialEnchants,
                Map<Enchantment, Integer> alternateEnchants,
                List<ItemFlag> initialFlags,
                List<ItemFlag> alternateFlags,
                List<String> initialLore,
                List<String> alternateLore) {
            configureItem(initialItem, initialEnchants, initialFlags, initialLore);
            configureItem(alternateItem, alternateEnchants, alternateFlags, alternateLore);

            setItem(slot, initialItem, event -> {
                ItemStack currentItem = event.getCurrentItem();

                if (currentItem != null && currentItem.isSimilar(initialItem)) {
                    setItem(slot, alternateItem, null, alternateEnchants, alternateFlags, alternateLore);
                } else if (currentItem != null && currentItem.isSimilar(alternateItem)) {
                    setItem(slot, initialItem, null, initialEnchants, initialFlags, initialLore);
                }

                if (additionalAction != null) {
                    additionalAction.accept(event);
                }
            }, initialEnchants, initialFlags, initialLore);
        }


        /**
         * Opens the GUI for a player.
         *
         * @param player The player.
         */
        public void open(@NotNull Player player) {
            initialize();
            player.openInventory(inventory);
        }

        /**
         * Method called when a player interacts with this GUI.
         *
         * @param event The click event.
         */
        public void handleClick(@NotNull InventoryClickEvent event) {
            if (event.getClickedInventory() != null && event.getClickedInventory().equals(inventory)) {
                event.setCancelled(true);
                Consumer<InventoryClickEvent> action = actions.get(event.getSlot());
                if (action != null) {
                    action.accept(event);
                }
            }
        }

        /**
         * Configures an item with enchantments, flags, and lore.
         *
         * @param item       The item to configure.
         * @param enchants   Enchantments to apply.
         * @param flags      Flags to add.
         * @param lore       Lore to set.
         */
        private void configureItem(@NotNull ItemStack item, Map<Enchantment, Integer> enchants, List<ItemFlag> flags, List<String> lore) {
            ItemMeta meta = item.getItemMeta();
            if (meta == null) return;

            if (enchants != null) {
                enchants.forEach((enchant, level) -> meta.addEnchant(enchant, level, true));
            }

            if (flags != null) {
                meta.addItemFlags(flags.toArray(new ItemFlag[0]));
            }

            if (lore != null) {
                meta.setLore(lore);
            }

            item.setItemMeta(meta);
        }

        /**
         * Method called when a player interacts with this GUI.
         *
         * @param event The click event.
         */
        @EventHandler
        public void onInventoryClick(@NotNull InventoryClickEvent event) {
            if (event.getClickedInventory() != null && event.getClickedInventory().equals(inventory)) {
                event.setCancelled(true);
                Consumer<InventoryClickEvent> action = actions.get(event.getSlot());
                if (action != null) {
                    action.accept(event);
                }
            }
        }

        /**
         * Method called when a player performs a drag-and-drop in this GUI.
         *
         * @param event The drag-and-drop event.
         */
        @EventHandler
        public void onInventoryDrag(@NotNull InventoryDragEvent event) {
            if (event.getInventory().equals(inventory)) {
                event.setCancelled(true);
            }
        }

        /**
         * Method to initialize the GUI (define items, etc.).
         */
        public abstract void initialize();
    }
