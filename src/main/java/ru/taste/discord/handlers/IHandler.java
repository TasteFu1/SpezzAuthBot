package ru.taste.discord.handlers;

import net.dv8tion.jda.api.events.GenericEvent;

public interface IHandler {
    void onEvent(GenericEvent event);
}
