package fr.kirosnn.dAPI.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class CommandBase implements CommandExecutor, TabCompleter {

    private final Map<String, SubCommand> subCommands = new HashMap<>();
    private String noPermissionMessage = "§cVous n'avez pas la permission pour utiliser cette commande.";

    /**
     * Définit le message à afficher si l'utilisateur n'a pas la permission.
     *
     * @param message Le message de refus de permission.
     */
    public void setNoPermissionMessage(@NotNull String message) {
        this.noPermissionMessage = message;
    }

    /**
     * Retourne le message actuel de refus de permission.
     *
     * @return Le message de refus de permission.
     */
    public String getNoPermissionMessage() {
        return noPermissionMessage;
    }

    /**
     * Enregistre une sous-commande.
     *
     * @param name       Le nom de la sous-commande.
     * @param subCommand La logique de la sous-commande.
     */
    public void registerSubCommand(@NotNull String name, SubCommand subCommand) {
        subCommands.put(name.toLowerCase(), subCommand);
    }

    /**
     * Exécute la commande principale ou une sous-commande.
     *
     * @param sender  L'émetteur de la commande.
     * @param command La commande exécutée.
     * @param label   L'alias utilisé.
     * @param args    Les arguments de la commande.
     * @return True si la commande est valide.
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (args.length > 0) {
            SubCommand subCommand = subCommands.get(args[0].toLowerCase());
            if (subCommand != null) {
                if (!subCommand.hasPermission(sender)) {
                    sender.sendMessage(noPermissionMessage);
                    return true;
                }
                return subCommand.execute(sender, args);
            }
        }

        return execute(sender, args);
    }

    /**
     * Complétion automatique des sous-commandes.
     *
     * @param sender L'émetteur de la commande.
     * @param command La commande.
     * @param alias L'alias utilisé.
     * @param args Les arguments de la commande.
     * @return Une liste d'options disponibles.
     */
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String @NotNull [] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            for (String subCommandName : subCommands.keySet()) {
                if (subCommandName.toLowerCase().startsWith(args[0].toLowerCase())) {
                    if (subCommands.get(subCommandName).hasPermission(sender)) {
                        completions.add(subCommandName);
                    }
                }
            }
            return completions;
        } else if (args.length > 1) {
            SubCommand subCommand = subCommands.get(args[0].toLowerCase());
            if (subCommand != null && subCommand.hasPermission(sender)) {
                return subCommand.tabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
            }
        }

        return new ArrayList<>();
    }

    /**
     * Implémenter cette méthode pour définir la logique de la commande principale.
     *
     * @param sender L'émetteur de la commande.
     * @param args   Les arguments de la commande.
     * @return True si la commande est valide.
     */
    public abstract boolean execute(CommandSender sender, String[] args);
}