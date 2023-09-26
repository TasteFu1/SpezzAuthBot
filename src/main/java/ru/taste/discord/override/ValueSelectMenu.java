package ru.taste.discord.override;

import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

public class ValueSelectMenu {

    public static StringSelectMenu.Builder create(String id, String... values) {
        StringBuilder menuId = new StringBuilder(id);

        for (String value : values) {
            menuId.append("/").append(value);
        }

        return StringSelectMenu.create(menuId.toString());
    }
}
