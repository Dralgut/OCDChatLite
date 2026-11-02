Very simple chat formatting plugin

## Config

```yaml
# ===== [ ERROR MESSAGES ] =====
messages:
  nobody_saw: "<red>Nobody saw your message</red>"

# ===== [ CHAT SETTINGS ] =====
chat:
  types:
    global_chat:
      prefix: "!"
      format: "<yellow>[G] <gray>[<player>] <white><message>"
      distance: -2
    local_chat:
      prefix: ""
      format: "<yellow>[L] <gray>[<player>] <white><message>"
      distance: 48
```

## Placeholders
```
<player> - Displays the player's name.
<message> - Displays the chat message.
```

## PlaceholderAPI integration
```
<papi:placegolder> = %placeholder%
```

## OCDText format
```
<black> - Applies black color.
<dark_blue> - Applies dark blue color.
<dark_green> - Applies dark green color.
<dark_aqua> - Applies dark aqua color.
<dark_red> - Applies dark red color.
<dark_purple> - Applies dark purple color.
<gold> - Applies gold color.
<gray> - Applies gray color.
<dark_gray> - Applies dark gray color.
<blue> - Applies blue color.
<green> - Applies green color.
<aqua> - Applies aqua color.
<red> - Applies red color.
<light_purple> - Applies light purple color.
<yellow> - Applies yellow color.
<white> - Applies white color.
<color:...> - Applies a custom color.
<b> - Enables bold text.
</b> - Disables bold text.
<i> - Enables italic text.
</i> - Disables italic text.
<u> - Enables underlined text.
</u> - Disables underlined text.
<s> - Enables strikethrough text.
</s> - Disables strikethrough text.
<o> - Enables obfuscated text.
</o> - Disables obfuscated text.
<reset> - Resets all formatting.
```
