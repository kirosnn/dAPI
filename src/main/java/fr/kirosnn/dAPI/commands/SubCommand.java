package fr.kirosnn.dAPI.commands;

import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

/**
 * Interface for creating subcommands.
 */
public interface SubCommand {

    /**
     * Executes the subcommand.
     *
     * @param sender The command sender.
     * @param args   The command arguments.
     * @return True if the subcommand is valid.
     */
    boolean execute(CommandSender sender, String[] args);

    /**
     * Auto-completes options for this subcommand.
     *
     * @param sender The command sender.
     * @param args   The command arguments.
     * @return A list of available options.
     */
    default List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    /**
     * Checks if the sender has permission to execute this command.
     *
     * @param sender The command sender.
     * @return True if the sender has permission.
     */
    default boolean hasPermission(CommandSender sender) {
        return true;
    }
}