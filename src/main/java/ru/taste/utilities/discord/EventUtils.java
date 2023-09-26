package ru.taste.utilities.discord;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class EventUtils {
    public static String getDiscordId(GenericEvent event) {
        if (event instanceof SlashCommandInteractionEvent eventIn) {
            return eventIn.getUser().getId();
        } else if (event instanceof ButtonInteractionEvent eventIn) {
            return eventIn.getUser().getId();
        } else if (event instanceof ModalInteractionEvent eventIn) {
            return eventIn.getUser().getId();
        } else if (event instanceof StringSelectInteractionEvent eventIn) {
            return eventIn.getUser().getId();
        }

        return null;
    }

    public static ReplyCallbackAction getCallback(GenericEvent event) {
        if (event instanceof SlashCommandInteractionEvent eventIn) {
            return eventIn.deferReply();
        } else if (event instanceof ButtonInteractionEvent eventIn) {
            return eventIn.deferReply();
        } else if (event instanceof ModalInteractionEvent eventIn) {
            return eventIn.deferReply();
        } else if (event instanceof StringSelectInteractionEvent eventIn) {
            return eventIn.deferReply();
        }

        return null;
    }

    public static InteractionHook getHook(GenericEvent event) {
        if (event instanceof SlashCommandInteractionEvent eventIn) {
            return eventIn.getHook();
        } else if (event instanceof ButtonInteractionEvent eventIn) {
            return eventIn.getHook();
        } else if (event instanceof ModalInteractionEvent eventIn) {
            return eventIn.getHook();
        } else if (event instanceof StringSelectInteractionEvent eventIn) {
            return eventIn.getHook();
        }

        return null;
    }

    public static boolean isAcknowledged(GenericEvent event) {
        if (event instanceof SlashCommandInteractionEvent eventIn) {
            return eventIn.isAcknowledged();
        } else if (event instanceof ButtonInteractionEvent eventIn) {
            return eventIn.isAcknowledged();
        } else if (event instanceof ModalInteractionEvent eventIn) {
            return eventIn.isAcknowledged();
        } else if (event instanceof StringSelectInteractionEvent eventIn) {
            return eventIn.isAcknowledged();
        }

        return false;
    }
}
