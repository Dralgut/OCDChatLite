package com.ocd.ocdchatlite.commands;

import com.ocd.ocdchatlite.OCDChatLite;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OCDChatLiteCommand implements CommandExecutor, TabCompleter {
    private final OCDChatLite plugin;

    public OCDChatLiteCommand(OCDChatLite plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0)
            return false;

        if (args[0].equals("reload")) {
            plugin.reload();
            sender.sendMessage(Component.text("§aOCDChatLite reloaded!"));
            return true;
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1)
            return List.of("reload");
        return List.of();
    }
}