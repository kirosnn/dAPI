package fr.kirosnn.dAPI.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.Set;

/**
 * The type Listener registrar.
 */
public class ListenerRegistrar {

    /**
     * Register all.
     *
     * @param plugin      the plugin
     * @param packageName the package name
     */
    public static void registerAll(Plugin plugin, String packageName) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<?>> listenerClasses = reflections.getTypesAnnotatedWith(AutoListener.class);

        for (Class<?> clazz : listenerClasses) {
            try {
                if (!Listener.class.isAssignableFrom(clazz)) {
                    plugin.getLogger().warning(clazz.getName() + " is annotated with @AutoListener but does not implement Listener !");
                    continue;
                }

                Constructor<?> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                Listener listener = (Listener) constructor.newInstance();

                Bukkit.getPluginManager().registerEvents(listener, plugin);

            } catch (Exception e) {
                plugin.getLogger().severe("Unable to registrar : " + clazz.getName());
                e.printStackTrace();
            }
        }
    }
}
