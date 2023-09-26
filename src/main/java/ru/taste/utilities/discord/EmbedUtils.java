package ru.taste.utilities.discord;

import net.dv8tion.jda.api.EmbedBuilder;

import ru.taste.utilities.java.ColorInstance;

public class EmbedUtils {
    public static EmbedBuilder builder() {
        return new EmbedBuilder();
    }

    public static EmbedBuilder info() {
        return builder().setTitle("Info").setColor(ColorInstance.INFO_COLOR);
    }

    public static EmbedBuilder error() {
        return builder().setTitle("Error").setColor(ColorInstance.ERROR_COLOR);
    }

    public static EmbedBuilder success() {
        return builder().setTitle("Success").setColor(ColorInstance.SUCCESS_COLOR);
    }

    public static EmbedBuilder warning() {
        return builder().setTitle("Warning").setColor(ColorInstance.WARNING_COLOR);
    }

    public static String[] spezzFooter() {
        return new String[]{"spezz.com", "https://spezz.space/assets/images/SE-512.png"};
    }
}
