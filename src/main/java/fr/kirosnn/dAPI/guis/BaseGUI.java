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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * The type Base gui.
 */
public abstract class BaseGUI implements Listener {

        private final String title;
        private final int rows;
        private final Inventory inventory;
        private final Map<Integer, Consumer<InventoryClickEvent>> actions;

    /**
     * Instantiates a new Base gui.
     *
     * @param title the title
     * @param rows  the rows
     */
    public BaseGUI(String title, int rows) {
            this.title = title;
            this.rows = rows;
            this.inventory = Bukkit.createInventory(null, rows * 9, title);
            this.actions = new HashMap<>();
            Bukkit.getPluginManager().registerEvents(this, Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("dCore")));
        }

    /**
     * Sets items.
     *
     * @param slots    the slots
     * @param item     the item
     * @param action   the action
     * @param enchants the enchants
     * @param flags    the flags
     * @param lore     the lore
     */
    public void setItems(@NotNull List<Integer> slots, @NotNull ItemStack item, Consumer<InventoryClickEvent> action,
                             Map<Enchantment, Integer> enchants, List<ItemFlag> flags, List<String> lore) {
            for (int slot : slots) {
                setItem(slot, item, action, enchants, flags, lore);
            }
        }

    /**
     * Sets item.
     *
     * @param slot     the slot
     * @param item     the item
     * @param action   the action
     * @param enchants the enchants
     * @param flags    the flags
     * @param lore     the lore
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
     * Close action consumer.
     *
     * @return the consumer
     */
    public static @NotNull Consumer<InventoryClickEvent> closeAction() {
            return event -> event.getWhoClicked().closeInventory();
        }

    /**
     * Command action consumer.
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
     * Open website action consumer.
     *
     * @param url     the url
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
     * Open gui action consumer.
     *
     * @param gui    the gui
     * @param player the player
     * @return the consumer
     */
    public static @NotNull Consumer<InventoryClickEvent> openGUIAction(BaseGUI gui, Player player) {
            return event -> gui.open(player);
        }

    /**
     * Sets item with toggle action.
     *
     * @param slot              the slot
     * @param initialItem       the initial item
     * @param alternateItem     the alternate item
     * @param additionalAction  the additional action
     * @param initialEnchants   the initial enchants
     * @param alternateEnchants the alternate enchants
     * @param initialFlags      the initial flags
     * @param alternateFlags    the alternate flags
     * @param initialLore       the initial lore
     * @param alternateLore     the alternate lore
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
     * Open.
     *
     * @param player the player
     */
    public void open(@NotNull Player player) {
            initialize();
            player.openInventory(inventory);
        }

    /**
     * Handle click.
     *
     * @param event the event
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
     * On inventory click.
     *
     * @param event the event
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
     * On inventory drag.
     *
     * @param event the event
     */
    @EventHandler
        public void onInventoryDrag(@NotNull InventoryDragEvent event) {
            if (event.getInventory().equals(inventory)) {
                event.setCancelled(true);
            }
        }

    /**
     * Initialize.
     */
    public abstract void initialize();
    }
