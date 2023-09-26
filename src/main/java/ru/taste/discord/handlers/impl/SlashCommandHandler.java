package ru.taste.discord.handlers.impl;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import lombok.Getter;
import ru.taste.Instance;
import ru.taste.discord.commands.Command;
import ru.taste.discord.handlers.IHandler;
import ru.taste.utilities.java.ColorInstance;


@SuppressWarnings("LombokGetterMayBeUsed")
public class SlashCommandHandler implements IHandler {
    @Getter
    private SlashCommandInteractionEvent event;

    private boolean fromGuild(SlashCommandInteractionEvent eventIn, Command command) {
        if (eventIn.isFromGuild()) {
            MessageEmbed embed = command.warning() //
                    .setDescription("Bot can only be used in private messages") //
                    .setColor(ColorInstance.WARNING_COLOR) //
                    .build();

            eventIn.replyEmbeds(embed).queue();
            return true;
        }

        return false;
    }

    @Override
    public void onEvent(GenericEvent event) {
        if (!(event instanceof SlashCommandInteractionEvent eventIn)) {
            return;
        }

        Command command = Instance.get().getCommandHandler().getCommandByName(eventIn.getName());

        if (command == null || fromGuild(eventIn, command)) {
            return;
        }

        this.event = eventIn;
        command.execute(this);
    }
}
