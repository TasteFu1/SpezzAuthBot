package ru.taste.discord.override;

import net.dv8tion.jda.api.interactions.modals.Modal;

public class ValueModal {

    public static Modal.Builder create(String id, String label, String... values) {
        StringBuilder modalId = new StringBuilder(id);

        for (String value : values) {
            modalId.append("/").append(value);
        }

        return Modal.create(modalId.toString(), label);
    }
}
