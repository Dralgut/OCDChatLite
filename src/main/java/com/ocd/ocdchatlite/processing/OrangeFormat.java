package com.ocd.ocdchatlite.processing;

import com.ocd.ocdchatlite.OCDChatLite;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class OrangeFormat {
    private final Section[] sections;

    private record Section(SectionType type, Component value, Style style, PlaceholderExpansion expansion,
                           String args) {
    }

    private enum SectionType {LITERAL, PLAYER, MESSAGE, PAPI, STYLE}

    public OrangeFormat(String format) {
        List<Section> parsed = parse(format);
        sections = parsed.toArray(Section[]::new);
    }

    public Component format(Player player, Component message) {
        TextComponent.Builder builder = Component.text();
        Component playerName = Component.text(player.getName());
        for (Section section : sections) {
            switch (section.type) {
                case LITERAL -> builder.append(section.value());
                case PLAYER -> builder.append(playerName.style(section.style()));
                case MESSAGE -> builder.append(message.style(section.style()));
                case PAPI -> {
                    String value = section.expansion().onRequest(player, section.args());
                    if (value != null) builder.append(Component.text(value, section.style()));
                }
            }
        }
        return builder.build();
    }

    private static List<Section> parse(String format) {
        List<Section> list = new ArrayList<>();
        Style.Builder sb = Style.style();

        int lp = 0;
        int sp = format.indexOf('<');
        int ep;

        while (lp <= sp) {
            ep = format.indexOf('>', sp + 1);
            if (ep == -1) break;

            String placeholder = format.substring(sp + 1, ep);
            SectionType type = placeholder.startsWith("papi:") && OCDChatLite.isPapiEnabled() ? SectionType.PAPI : placeholder.startsWith("color:") ? SectionType.STYLE : switch (placeholder) {
                case "player" -> SectionType.PLAYER;
                case "message" -> SectionType.MESSAGE;
                case "reset", "black", "dark_blue", "dark_green", "dark_aqua", "dark_red", "dark_purple", "gold",
                     "gray", "dark_gray", "blue", "green", "aqua", "red", "light_purple", "yellow", "white", "b", "i",
                     "u", "s", "o", "/b", "/i", "/u", "/s", "/o" -> SectionType.STYLE;
                default -> null;
            };

            if (type != null) {
                if (sp - lp > 0)
                    list.add(new Section(SectionType.LITERAL, Component.text(format.substring(lp, sp), sb.build()), null, null, null));
                lp = ep + 1;
                if (type == SectionType.PAPI) {
                    String papi = placeholder.substring(5);
                    int sep = papi.indexOf('_');

                    String identifier = sep == -1 ? papi : papi.substring(0, sep);
                    String args = sep == -1 ? "" : papi.substring(sep + 1);

                    PlaceholderExpansion expansion = PlaceholderAPIPlugin.getInstance().getLocalExpansionManager().getExpansion(identifier);
                    if (expansion != null)
                        list.add(new Section(SectionType.PAPI, null, sb.build(), expansion, args));
                    else
                        list.add(new Section(SectionType.LITERAL, Component.text("<" + placeholder + ">", sb.build()), null, null, null));
                } else if (type == SectionType.STYLE) {
                    if (placeholder.startsWith("color:")) {
                        String color = placeholder.substring(6);
                        sb.color(parseColorOrDefault(color));
                    } else switch (placeholder) {
                        case "reset" -> sb = Style.style();
                        case "black" -> sb.color(NamedTextColor.BLACK);
                        case "dark_blue" -> sb.color(NamedTextColor.DARK_BLUE);
                        case "dark_green" -> sb.color(NamedTextColor.DARK_GREEN);
                        case "dark_aqua" -> sb.color(NamedTextColor.DARK_AQUA);
                        case "dark_red" -> sb.color(NamedTextColor.DARK_RED);
                        case "dark_purple" -> sb.color(NamedTextColor.DARK_PURPLE);
                        case "gold" -> sb.color(NamedTextColor.GOLD);
                        case "gray" -> sb.color(NamedTextColor.GRAY);
                        case "dark_gray" -> sb.color(NamedTextColor.DARK_GRAY);
                        case "blue" -> sb.color(NamedTextColor.BLUE);
                        case "green" -> sb.color(NamedTextColor.GREEN);
                        case "aqua" -> sb.color(NamedTextColor.AQUA);
                        case "red" -> sb.color(NamedTextColor.RED);
                        case "light_purple" -> sb.color(NamedTextColor.LIGHT_PURPLE);
                        case "yellow" -> sb.color(NamedTextColor.YELLOW);
                        case "white" -> sb.color(NamedTextColor.WHITE);
                        case "b" -> sb.decoration(TextDecoration.BOLD, true);
                        case "i" -> sb.decoration(TextDecoration.ITALIC, true);
                        case "u" -> sb.decoration(TextDecoration.UNDERLINED, true);
                        case "s" -> sb.decoration(TextDecoration.STRIKETHROUGH, true);
                        case "o" -> sb.decoration(TextDecoration.OBFUSCATED, true);
                        case "/b" -> sb.decoration(TextDecoration.BOLD, false);
                        case "/i" -> sb.decoration(TextDecoration.ITALIC, false);
                        case "/u" -> sb.decoration(TextDecoration.UNDERLINED, false);
                        case "/s" -> sb.decoration(TextDecoration.STRIKETHROUGH, false);
                        case "/o" -> sb.decoration(TextDecoration.OBFUSCATED, false);
                    }
                } else list.add(new Section(type, null, sb.build(), null, null));
            }
            sp = format.indexOf('<', ep);
        }
        if (lp < format.length())
            list.add(new Section(SectionType.LITERAL, Component.text(format.substring(lp), sb.build()), null, null, null));

        for (int i = 1; i < list.size(); i++) {
            Section ns = list.get(i);
            Section ps = list.get(i - 1);

            if (ns.type() == SectionType.LITERAL && ps.type() == SectionType.LITERAL) {
                list.remove(i--);
                TextComponent.Builder cb = Component.text();
                cb.append(ps.value());
                cb.append(ns.value());
                list.set(i, new Section(SectionType.LITERAL, cb.build(), null, null, null));
            }
        }
        return list;
    }

    private static TextColor parseColorOrDefault(String input) {
        try {
            return TextColor.color(Integer.parseInt(input.trim()));
        } catch (NumberFormatException e) {
            return NamedTextColor.WHITE;
        }
    }
}