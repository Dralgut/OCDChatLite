package com.ocd.ocdchatlite.listeners;

import com.ocd.ocdchatlite.processing.ChatTypeManager;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ChatListener implements Listener {
    private static final MiniMessage MM = MiniMessage.miniMessage();
    private static final PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.plainText();

    private final static int UNLIMITED = -2;
    private final static int UNLIMITED_FOR_WORLD = -1;

    private final ChatTypeManager manager;
    private final Component nobodySawMessage;

    public ChatListener(ChatTypeManager manager, FileConfiguration config) {
        this.manager = manager;
        this.nobodySawMessage = MM.deserialize(config.getString("message.nobody_saw", "<red>Nobody saw your message</red>"));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onChat(AsyncChatEvent event){
        event.setCancelled(true);

        String message = PLAIN.serialize(event.message());
        Player sender = event.getPlayer();

        ChatTypeManager.ChatType type = manager.match(message);
        if (type == null) return;

        message = message.substring(type.prefix().length()).stripLeading();
        if (message.isEmpty()) return;

        Component formatedMessage = type.format().format(sender, PLAIN.deserialize(message));

        sendMessage(sender, formatedMessage, type.distance());
    }

    private void sendMessage(Player sender, Component message, double distance) {
        if (distance == UNLIMITED)
            for (Player player : Bukkit.getOnlinePlayers()) player.sendMessage(message);
        else {
            boolean ds = true;
            if (distance == UNLIMITED_FOR_WORLD)
                for (Player player : sender.getWorld().getPlayers()) {
                    player.sendMessage(message);
                    ds = false;
                }
            else {
                Location sl = sender.getLocation();
                double sd = distance * distance;

                for (Player player : sender.getWorld().getPlayers())
                    if (sl.distanceSquared(player.getLocation()) < sd) {
                        player.sendMessage(message);
                        ds = false;
                    }
            }

            if (ds)
                sender.sendMessage(nobodySawMessage);
        }
    }
}
