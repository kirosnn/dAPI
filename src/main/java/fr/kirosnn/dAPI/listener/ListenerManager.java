package fr.kirosnn.dAPI.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.Set;

/**
 * Gestionnaire automatique des listeners.
 */
public class ListenerManager {

    /**
     * Enregistre tous les listeners annotés avec @AutoListener.
     *
     * @param plugin Le plugin principal.
     */
    public static void registerAllListeners(Plugin plugin) {
        Set<Class<?>> listenerClasses = new Reflections(plugin.getClass().getPackage().getName())
                .getTypesAnnotatedWith(AutoListener.class);

        for (Class<?> clazz : listenerClasses) {
            if (!Listener.class.isAssignableFrom(clazz)) continue;

            try {
                Listener listener = instantiateListener(clazz, plugin);
                Bukkit.getPluginManager().registerEvents(listener, plugin);
                plugin.getLogger().info("[dAPI] Listener enregistré: " + clazz.getSimpleName());
            } catch (Exception e) {
                plugin.getLogger().severe("[dAPI] Erreur en enregistrant: " + clazz.getName());
                e.printStackTrace();
            }
        }
    }

    /**
     * Instancie un listener en injectant le plugin si nécessaire.
     */
    private static Listener instantiateListener(Class<?> clazz, Plugin plugin) throws Exception {
        for (Constructor<?> constructor : clazz.getConstructors()) {
            if (constructor.getParameterCount() == 1 && constructor.getParameterTypes()[0].isAssignableFrom(plugin.getClass())) {
                return (Listener) constructor.newInstance(plugin);
            }
        }
        return (Listener) clazz.getDeclaredConstructor().newInstance();
    }
}
