package fr.kirosnn.dAPI.commands;

import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public interface SubCommand {
    /**
     * Exécute la sous-commande.
     *
     * @param sender L'émetteur de la commande.
     * @param args   Les arguments de la commande.
     * @return True si la commande est valide.
     */
    boolean execute(CommandSender sender, String[] args);

    /**
     * Complétion automatique pour cette sous-commande.
     *
     * @param sender L'émetteur de la commande.
     * @param args   Les arguments de la commande.
     * @return Une liste d'options disponibles.
     */
    default List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    /**
     * Vérifie si le joueur a la permission pour exécuter cette commande.
     *
     * @param sender L'émetteur de la commande.
     * @return True si l'émetteur a la permission.
     */
    default boolean hasPermission(CommandSender sender) {
        return true;
    }
}