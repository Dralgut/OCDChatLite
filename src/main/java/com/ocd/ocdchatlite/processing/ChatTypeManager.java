package com.ocd.ocdchatlite.processing;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ChatTypeManager {
    public ChatType[] types;

    public record ChatType(String prefix, OrangeFormat format, double distance) {}

    public ChatTypeManager(FileConfiguration config) {
        List<ChatType> list = new ArrayList<>();

        ConfigurationSection section = config.getConfigurationSection("chat.types");
        if (section == null) {
            types = new ChatType[0];
            return;
        }

        for (String key : section.getKeys(false)) {
            String prefix = section.getString(key + ".prefix", "");
            OrangeFormat format = new OrangeFormat(section.getString(key + ".format", "{player}: {message}"));
            double distance = section.getDouble(key + ".distance", -2);

            list.add(new ChatType(prefix, format, distance));
        }

        list.sort(Comparator.comparingInt((ChatType chat) -> chat.prefix().length()).reversed());
        types = list.toArray(ChatType[]::new);
    }

    public ChatType match(String message) {
        ChatType match = null;

        for (ChatType type : types) {
            if (message.startsWith(type.prefix())) {
                match = type;
                break;
            }
        }

        return match;
    }
}