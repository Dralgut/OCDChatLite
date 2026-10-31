package com.ocd.ocdchatlite.processing;

import com.ocd.ocdchatlite.OCDChatLite;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class OrangeFormat {
    private final static MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private final Section[] sections;

    private enum SectionType {LITERAL, PLAYER, MESSAGE, PAPI}
    private record Section(SectionType type, Component value, PlaceholderExpansion expansion, String params) {}

    public OrangeFormat(String format) {
        List<Section> parsed = parse(format);
        this.sections = parsed.toArray(Section[]::new);
    }

    public Component format(Player player, Component message) {
        TextComponent.Builder builder = Component.text();
        Component playerName = Component.text(player.getName());
        for (Section section : sections) {
            switch (section.type) {
                case LITERAL -> builder.append(section.value);
                case PLAYER -> builder.append(playerName);
                case MESSAGE -> builder.append(message);
                case PAPI -> {
                    if (section.expansion != null) {
                        String result = section.expansion.onRequest(player, section.params);

                        if (result != null)
                            builder.append(Component.text(result));
                    }
                }
            }
        }
        return builder.build();
    }

    private List<Section> parse(String format) {
        List<Section> list = new ArrayList<>();

        int lp = 0;
        int sp = format.indexOf('{');

        while (sp >= 0) {
            int ep = format.indexOf('}', sp + 1);
            if (ep < 0) break;

            String placeholder = format.substring(sp + 1, ep);
            SectionType type = placeholder.startsWith("papi:") ? SectionType.PAPI : switch (placeholder) {
                case "player" -> SectionType.PLAYER;
                case "message" -> SectionType.MESSAGE;
                default -> null;
            };

            if (type != null) {
                if (sp > lp) list.add(new Section(SectionType.LITERAL, MINI_MESSAGE.deserialize(format.substring(lp, sp)), null, null));
                if (type == SectionType.PAPI) {
                    String papi = placeholder.substring(5);
                    int sep = papi.indexOf('_');

                    if (sep > 0 && sep < papi.length() - 1) {
                        String identifier = papi.substring(0, sep);
                        String params = papi.substring(sep + 1);

                        PlaceholderExpansion expansion = null;

                        if (OCDChatLite.isPapiEnabled())
                            expansion = PlaceholderAPIPlugin.getInstance().getLocalExpansionManager().getExpansion(identifier);

                        list.add(new Section(SectionType.PAPI, null, expansion, params));
                    } else list.add(new Section(SectionType.LITERAL, MINI_MESSAGE.deserialize(format.substring(sp, ep + 1)), null, null));
                } else list.add(new Section(type, null, null, null));
                lp = ep + 1;
            }

            sp = format.indexOf('{', ep + 1);
        }

        if (lp < format.length()) list.add(new Section(SectionType.LITERAL, MINI_MESSAGE.deserialize(format.substring(lp)), null, null));

        return list;
    }
}