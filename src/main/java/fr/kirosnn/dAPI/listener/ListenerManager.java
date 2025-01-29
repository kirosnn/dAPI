package fr.kirosnn.dAPI.listener;

import fr.kirosnn.dAPI.utils.LoggerUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * The type Listener manager.
 */
public class ListenerManager {
    /**
     * Register all listeners.
     *
     * @param plugin        the plugin
     * @param extraPackages the extra packages
     */
    public static void registerAllListeners(@NotNull Plugin plugin, String... extraPackages) {
        Set<String> packagesToScan = new HashSet<>();
        packagesToScan.add(plugin.getClass().getPackage().getName());
        packagesToScan.addAll(Arrays.asList(extraPackages));

        for (String pkg : packagesToScan) {
            Reflections reflections = new Reflections(pkg);
            Set<Class<?>> listenerClasses = reflections.getTypesAnnotatedWith(AutoListener.class)
                    .stream()
                    .filter(Listener.class::isAssignableFrom)
                    .collect(Collectors.toSet());

            for (Class<?> clazz : listenerClasses) {
                try {
                    Listener listener = (Listener) clazz.getDeclaredConstructor().newInstance();
                    Bukkit.getPluginManager().registerEvents(listener, plugin);
                } catch (ReflectiveOperationException e) {
                    new LoggerUtils((JavaPlugin) plugin).infoPlugin("This error is from dAPI.");
                    plugin.getLogger().severe("[dAPI] Failed to instantiate listener: " + clazz.getName());
                } catch (Exception e) {
                    plugin.getLogger().severe("[dAPI] Unexpected error while registering: " + clazz.getName());
                }
            }
        }
    }
}