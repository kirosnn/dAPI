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
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Classe de base pour simplifier la création de GUI.
 */
public abstract class BaseGUI implements Listener {

    private final Inventory inventory;
    private final Map<Integer, Consumer<InventoryClickEvent>> actions;

    /**
     * Constructeur de la GUI.
     *
     * @param title Le titre de l'inventaire.
     * @param rows Le nombre de lignes (1 à 6).
     */
    public BaseGUI(String title, int rows) {
        this.inventory = Bukkit.createInventory(null, rows * 9, title);
        this.actions = new HashMap<>();
        registerListenerAutomatically();
    }

    /**
     * Enregistre automatiquement la classe comme Listener auprès de Bukkit.
     */
    private void registerListenerAutomatically() {
        Plugin plugin = getPlugin();
        if (plugin != null) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
        } else {
            throw new IllegalStateException("Le plugin ne peut pas être null. Il faut utiliser @NotNull Plugin getPlugin();");
        }
    }

    /**
     * Méthode abstraite pour obtenir le plugin.
     *
     * @return Le plugin appelant.
     */
    protected abstract @NotNull Plugin getPlugin();

    /**
     * Définit un item dans plusieurs emplacements avec une action et configuration complète.
     *
     * @param slots   Les emplacements.
     * @param item    L'item à placer.
     * @param action  L'action à exécuter lors du clic.
     * @param enchants Les enchantements à ajouter (null si aucun enchantement).
     * @param flags    Les flags d'item à ajouter (null si aucun flag).
     * @param lore     Le lore à définir (null si aucun lore).
     */
    public void setItems(@NotNull List<Integer> slots, ItemStack item, Consumer<InventoryClickEvent> action,
                         Map<Enchantment, Integer> enchants, List<ItemFlag> flags, List<String> lore) {
        for (int slot : slots) {
            setItem(slot, item, action, enchants, flags, lore);
        }
    }

    /**
     * Définit un item à un emplacement donné avec configuration complète.
     *
     * @param slot    L'emplacement (0 à rows * 9 - 1).
     * @param item    L'item à placer.
     * @param action  L'action à exécuter lors du clic (null si aucune action).
     * @param enchants Les enchantements à ajouter (null si aucun enchantement).
     * @param flags    Les flags d'item à ajouter (null si aucun flag).
     * @param lore     Le lore à définir (null si aucun lore).
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
     * Actions prédéfinies : Fermer le menu.
     */
    @Contract(pure = true)
    public static @NotNull Consumer<InventoryClickEvent> closeAction() {
        return event -> event.getWhoClicked().closeInventory();
    }

    /**
     * Actions prédéfinies : Exécuter une commande.
     */
    @Contract(pure = true)
    public static @NotNull Consumer<InventoryClickEvent> commandAction(String command) {
        return event -> {
            Player player = (Player) event.getWhoClicked();
            player.performCommand(command);
            player.closeInventory();
        };
    }

    /**
     * Actions prédéfinies : Ouvrir un site Web avec un message personnalisé.
     */
    @Contract(pure = true)
    public static @NotNull Consumer<InventoryClickEvent> openWebsiteAction(String url, String message) {
        return event -> {
            Player player = (Player) event.getWhoClicked();
            player.closeInventory();
            player.sendMessage(message.replace("{url}", url));
        };
    }

    /**
     * Actions prédéfinies : Ouvrir une autre GUI.
     */
    @Contract(pure = true)
    public static @NotNull Consumer<InventoryClickEvent> openGUIAction(BaseGUI gui, Player player) {
        return event -> gui.open(player);
    }

    /**
     * Définit un item avec une action qui alterne entre deux items lorsqu'il est cliqué,
     * tout en permettant de configurer des enchantements, flags et lore.
     *
     * @param slot              Le slot où placer l'item.
     * @param initialItem       L'item initial à placer.
     * @param alternateItem     L'item qui remplace l'item initial lors du clic.
     * @param additionalAction  Une action supplémentaire à exécuter lors du clic (peut être null).
     * @param initialEnchants   Enchantements pour l'item initial.
     * @param alternateEnchants Enchantements pour l'item alternatif.
     * @param initialFlags      Flags pour l'item initial.
     * @param alternateFlags    Flags pour l'item alternatif.
     * @param initialLore       Lore pour l'item initial.
     * @param alternateLore     Lore pour l'item alternatif.
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
     * Ouvre la GUI pour un joueur.
     *
     * @param player Le joueur.
     */
    public void open(@NotNull Player player) {
        initialize();
        player.openInventory(inventory);
    }

    /**
     * Méthode appelée lorsqu'un joueur interagit avec cette GUI.
     *
     * @param event L'événement de clic.
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
     * Configure un item avec des enchantements, flags et lore.
     *
     * @param item       L'item à configurer.
     * @param enchants   Les enchantements à appliquer.
     * @param flags      Les flags à ajouter.
     * @param lore       Le lore à définir.
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
     * Méthode appelée lorsqu'un joueur interagit avec cette GUI.
     *
     * @param event L'événement de clic.
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
     * Méthode appelée lorsqu'un joueur effectue un drag and drop dans cette GUI.
     *
     * @param event L'événement de drag and drop.
     */
    @EventHandler
    public void onInventoryDrag(@NotNull InventoryDragEvent event) {
        if (event.getInventory().equals(inventory)) {
            event.setCancelled(true);
        }
    }

    /**
     * Méthode pour initialiser la GUI (définir les items, etc.).
     */
    public abstract void initialize();
}