package it.dominick.lzp.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtils {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public static void send(Player player, String message, String... placeholders) {
        if (placeholders.length % 2 == 0) {
            for (int i = 0; i < placeholders.length; i += 2) {
                String placeholder = placeholders[i];
                String replacement = placeholders[i + 1];
                message = message.replace(placeholder, replacement);
            }
        } else {
            send(player, "&cErrore: I placeholder devono essere forniti in coppia.");
            return;
        }

       send(player, message);
    }

    public static void send(CommandSender sender, String message) {
        sender.sendMessage(color(message));
    }

    public static String color(final String message) {
        final char colorChar = ChatColor.COLOR_CHAR;

        final Matcher hexMatcher = HEX_PATTERN.matcher(message);
        final StringBuilder buffer = new StringBuilder(message.length() + 4 * 8);

        while (hexMatcher.find()) {
            final String group = hexMatcher.group(1);

            hexMatcher.appendReplacement(buffer, colorChar + "x"
                    + colorChar + group.charAt(0) + colorChar + group.charAt(1)
                    + colorChar + group.charAt(2) + colorChar + group.charAt(3)
                    + colorChar + group.charAt(4) + colorChar + group.charAt(5));
        }

        final String partiallyTranslated = hexMatcher.appendTail(buffer).toString();

        return ChatColor.translateAlternateColorCodes('&', partiallyTranslated);
    }
}