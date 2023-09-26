package ru.taste.discord.override.enums;

import net.dv8tion.jda.api.entities.emoji.Emoji;

import lombok.AllArgsConstructor;
import ru.taste.discord.override.AdvancedButton;

@AllArgsConstructor
public enum EmojiEnum {
    EDIT("edit", 1155991431329095810L), //
    KEY("key", 1155991432721612962L), //
    PANEL("panel", 1155993602506366986L), //
    APPS("apps", 1156251865529471029L), //
    ADD("add", 1156259728205299883L), //
    APP_GEAR("app_gear", 1156263791206613052L), //
    BACK("back", 1156265869920448543L), //
    HARDWARE_RESET("hardware_reset", 1156298905458319421L), //
    HARDWARE_EDIT("hardware_edit", 1156298903403122813L), //
    HARDWARE_PERIOD("hardware_period", 1156298900983009350L), //
    TOGGLE_LEFT("toggle_left", 1156319425872527461L), //
    TOGGLE_RIGHT("toggle_right", 1156336041876279339L), //
    KEYS_MANAGEMENT("keys_management", 1156343026910773409L), //
    USERS_MANAGEMENT("users_management", 1156340329449005196L), //
    STATISTIC("statistic", 1156343902899556352L), //
    DISCORD("discord", 1156344904486752397L), //
    CODE("code", 1156355035131809943L);

    private final String name;
    private final long id;

    public Emoji get() {
        return Emoji.fromCustom(name, id, false);
    }
}
