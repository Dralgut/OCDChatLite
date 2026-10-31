package com.ocd.ocdchatlite;

import com.ocd.ocdchatlite.listeners.ChatListener;
import com.ocd.ocdchatlite.processing.ChatTypeManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class OCDChatLite extends JavaPlugin {
    private static ChatTypeManager manager;
    private static FileConfiguration config;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();

        config = getConfig();
        manager = new ChatTypeManager(config);

        getServer().getPluginManager().registerEvents(new ChatListener(manager, config), this);
    }

    public static ChatTypeManager getManager() {
        return manager;
    }
}
