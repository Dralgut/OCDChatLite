package com.ocd.ocdchatlite.processing;

import com.ocd.ocdchatlite.OCDChatLite;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.ArrayList;
import java.util.List;

public class OrangeFormat {
    private final static MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private final Section[] sections;

    private enum SectionType {LITERAL, PLAYER, MESSAGE, PAPI}
    private record Section(SectionType type, Component value) {}

    public OrangeFormat(String format) {
        List<Section> parsed = parse(format);
        this.sections = parsed.toArray(Section[]::new);
    }

    public Component format(String player, Component message) {
        TextComponent.Builder builder = Component.text();
        for (Section section : sections) {
            switch (section.type) {
                case LITERAL -> builder.append(section.value);
                case PLAYER -> builder.append(Component.text(player));
                case MESSAGE -> builder.append(message);
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

            SectionType type = switch (format.substring(sp + 1, ep)) {
                case "player" -> SectionType.PLAYER;
                case "message" -> SectionType.MESSAGE;
                default -> null;
            };

            if (type != null) {
                if (sp > lp) list.add(new Section(SectionType.LITERAL, MINI_MESSAGE.deserialize(format.substring(lp, sp))));
                list.add(new Section(type, null));
                lp = ep + 1;
            }

            sp = format.indexOf('{', ep + 1);
        }

        if (lp < format.length()) list.add(new Section(SectionType.LITERAL, MINI_MESSAGE.deserialize(format.substring(lp))));

        return list;
    }
}