package ru.taste.discord.handlers;

import net.dv8tion.jda.api.events.GenericEvent;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import ru.taste.discord.handlers.impl.ButtonHandler;
import ru.taste.discord.handlers.impl.ModalHandler;
import ru.taste.discord.handlers.impl.SelectMenuHandler;
import ru.taste.discord.handlers.impl.SlashCommandHandler;


public class EventListener implements net.dv8tion.jda.api.hooks.EventListener {
    List<IHandler> handlers = new ArrayList<>();

    public EventListener() {
        handlers.add(new SlashCommandHandler());
        handlers.add(new ButtonHandler());
        handlers.add(new ModalHandler());
        handlers.add(new SelectMenuHandler());
    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        handlers.forEach(handler -> handler.onEvent(event));
    }
}
