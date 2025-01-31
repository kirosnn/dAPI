package fr.kirosnn.dAPI.commands;

import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

/**
 * The interface Sub command.
 */
public interface SubCommand {

    /**
     * Execute boolean.
     *
     * @param sender the sender
     * @param args   the args
     * @return the boolean
     */
    boolean execute(CommandSender sender, String[] args);

    /**
     * Tab complete list.
     *
     * @param sender the sender
     * @param args   the args
     * @return the list
     */
    default List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    /**
     * Has permission boolean.
     *
     * @param sender the sender
     * @return the boolean
     */
    default boolean hasPermission(CommandSender sender) {
        return true;
    }
}