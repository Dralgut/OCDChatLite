package com.ocd.ocdchatlite;

import com.ocd.ocdchatlite.commands.OCDChatLiteCommand;
import com.ocd.ocdchatlite.listeners.ChatListener;
import com.ocd.ocdchatlite.processing.ChatTypeManager;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public final class OCDChatLite extends JavaPlugin {
    private static ChatListener listener;
    private static ChatTypeManager manager;
    private static FileConfiguration config;
    private static boolean papiEnabled;

    @Override
    public void onEnable() {
        PluginCommand command = getCommand("ocdchatlite");

        if (command != null)
            command.setExecutor(new OCDChatLiteCommand(this));

        init();
    }

    @Override
    public void onDisable() {
        deinit();
    }

    public void init() {
        saveDefaultConfig();
        reloadConfig();

        papiEnabled = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;

        config = getConfig();
        manager = new ChatTypeManager(config);
        listener = new ChatListener(manager, config);

        getServer().getPluginManager().registerEvents(listener, this);
    }

    public void deinit() {
        if (listener != null)
            HandlerList.unregisterAll(listener);

        listener = null;
        manager = null;
    }

    public void reload() {
        deinit();
        init();
    }

    public static ChatTypeManager getManager() {
        return manager;
    }

    public static boolean isPapiEnabled() {
        return papiEnabled;
    }
}