package fr.kirosnn.dAPI.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The type Custom bukkit command.
 */
public class CustomBukkitCommand extends Command {

    private final CommandExecutor executor;
    private final TabCompleter completer;

    /**
     * Instantiates a new Custom bukkit command.
     *
     * @param name      the name
     * @param executor  the executor
     * @param completer the completer
     */
    protected CustomBukkitCommand(@NotNull String name, @NotNull CommandExecutor executor, @Nullable TabCompleter completer) {
        super(name);
        this.executor = executor;
        this.completer = completer;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        return executor.onCommand(sender, this, commandLabel, args);
    }

    @Override
    public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return completer != null ? completer.onTabComplete(sender, this, alias, args) : super.tabComplete(sender, alias, args);
    }
}