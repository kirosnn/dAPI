package fr.kirosnn.dAPI.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Abstract class for creating custom commands with subcommands.
 */
public abstract class CommandBase implements CommandExecutor, TabCompleter {

    private final Map<String, SubCommand> subCommands = new HashMap<>();
    private String noPermissionMessage = "§cYou don't have permission to use this command.";

    /**
     * Sets the message to display if the user lacks permission.
     *
     * @param message The no-permission message.
     */
    public void setNoPermissionMessage(@NotNull String message) {
        this.noPermissionMessage = message;
    }

    /**
     * Gets the current no-permission message.
     *
     * @return The no-permission message.
     */
    public String getNoPermissionMessage() {
        return noPermissionMessage;
    }

    /**
     * Registers a subcommand.
     *
     * @param name       The name of the subcommand.
     * @param subCommand The logic for the subcommand.
     */
    public void registerSubCommand(@NotNull String name, SubCommand subCommand) {
        subCommands.put(name.toLowerCase(), subCommand);
    }

    /**
     * Executes the main command or a subcommand.
     *
     * @param sender  The command sender.
     * @param command The command being executed.
     * @param label   The alias used.
     * @param args    The command arguments.
     * @return True if the command is valid.
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
     * Auto-completes subcommands.
     *
     * @param sender  The command sender.
     * @param command The command.
     * @param alias   The alias used.
     * @param args    The command arguments.
     * @return A list of available options.
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
     * Implement this method to define the logic of the main command.
     *
     * @param sender The command sender.
     * @param args   The command arguments.
     * @return True if the command is valid.
     */
    public abstract boolean execute(CommandSender sender, String[] args);
}