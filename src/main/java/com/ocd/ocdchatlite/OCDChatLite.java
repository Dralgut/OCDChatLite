package com.ocd.ocdchatlite;

import com.ocd.ocdchatlite.listeners.ChatListener;
import com.ocd.ocdchatlite.processing.ChatTypeManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class OCDChatLite extends JavaPlugin {
    private static ChatTypeManager manager;
    private static FileConfiguration config;
    private static boolean papiEnabled;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();

        papiEnabled = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;

        config = getConfig();
        manager = new ChatTypeManager(config);

        getServer().getPluginManager().registerEvents(new ChatListener(manager, config), this);
    }

    public static ChatTypeManager getManager() {
        return manager;
    }
    public static boolean isPapiEnabled() {
        return papiEnabled;
    }
}
