package fr.kirosnn.dAPI.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * The type Command base.
 */
public abstract class CommandBase implements CommandExecutor, TabCompleter {

    private final Map<String, SubCommand> subCommands = new HashMap<>();
    private String noPermissionMessage = "§cYou don't have permission to use this command.";

    /**
     * Sets no permission message.
     *
     * @param message the message
     */
    public void setNoPermissionMessage(@NotNull String message) {
        this.noPermissionMessage = message;
    }

    /**
     * Gets no permission message.
     *
     * @return the no permission message
     */
    public String getNoPermissionMessage() {
        return noPermissionMessage;
    }

    /**
     * Register sub command.
     *
     * @param name       the name
     * @param subCommand the sub command
     */
    public void registerSubCommand(@NotNull String name, SubCommand subCommand) {
        subCommands.put(name.toLowerCase(), subCommand);
    }

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
     * Execute boolean.
     *
     * @param sender the sender
     * @param args   the args
     * @return the boolean
     */
    public abstract boolean execute(CommandSender sender, String[] args);
}