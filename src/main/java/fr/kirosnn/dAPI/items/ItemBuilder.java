package fr.kirosnn.dAPI.items;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * The type Item builder.
 */
public class ItemBuilder {
    private final ItemStack item;
    private final ItemMeta meta;
    private final PersistentDataContainer data;

    private Consumer<PlayerInteractEvent> clickAction;
    private Consumer<Player> dropAction;
    private final List<Condition> conditions = new ArrayList<>();

    /**
     * Instantiates a new Item builder.
     *
     * @param material the material
     */
    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
        this.data = meta.getPersistentDataContainer();
    }

    /**
     * Sets name.
     *
     * @param name the name
     * @return the name
     */
    public ItemBuilder setName(String name) {
        meta.setDisplayName(name);
        return this;
    }

    /**
     * Sets lore.
     *
     * @param lore the lore
     * @return the lore
     */
    public ItemBuilder setLore(List<String> lore) {
        meta.setLore(lore);
        return this;
    }

    /**
     * Add enchant item builder.
     *
     * @param enchant the enchant
     * @param level   the level
     * @return the item builder
     */
    public ItemBuilder addEnchant(Enchantment enchant, int level) {
        meta.addEnchant(enchant, level, true);
        return this;
    }

    /**
     * Add flag item builder.
     *
     * @param flags the flags
     * @return the item builder
     */
    public ItemBuilder addFlag(ItemFlag... flags) {
        meta.addItemFlags(flags);
        return this;
    }

    /**
     * Add custom data item builder.
     *
     * @param key   the key
     * @param value the value
     * @return the item builder
     */
    public ItemBuilder addCustomData(NamespacedKey key, String value) {
        data.set(key, PersistentDataType.STRING, value);
        return this;
    }

    /**
     * Add condition item builder.
     *
     * @param condition the condition
     * @return the item builder
     */
    public ItemBuilder addCondition(Condition condition) {
        conditions.add(condition);
        return this;
    }

    /**
     * On click item builder.
     *
     * @param action the action
     * @return the item builder
     */
    public ItemBuilder onClick(Consumer<PlayerInteractEvent> action) {
        this.clickAction = action;
        return this;
    }

    /**
     * On drop item builder.
     *
     * @param action the action
     * @return the item builder
     */
    public ItemBuilder onDrop(Consumer<Player> action) {
        this.dropAction = action;
        return this;
    }

    /**
     * Build item stack.
     *
     * @return the item stack
     */
    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Check conditions boolean.
     *
     * @param player the player
     * @return the boolean
     */
    public boolean checkConditions(Player player) {
        for (Condition condition : conditions) {
            if (!condition.test(player)) return false;
        }
        return true;
    }

    /**
     * Execute click.
     *
     * @param event the event
     */
    public void executeClick(PlayerInteractEvent event) {
        if (clickAction != null && checkConditions(event.getPlayer())) {
            clickAction.accept(event);
        }
    }

    /**
     * Execute drop.
     *
     * @param player the player
     */
    public void executeDrop(Player player) {
        if (dropAction != null && checkConditions(player)) {
            dropAction.accept(player);
        }
    }
}