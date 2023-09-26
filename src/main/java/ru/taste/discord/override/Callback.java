package ru.taste.discord.override;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.taste.utilities.discord.EventUtils;

public class Callback {
    private final List<ItemComponent[]> actionRows = new ArrayList<>();
    private final List<MessageEmbed> embeds = new ArrayList<>();
    private final GenericEvent event;

    private Callback(GenericEvent event) {
        this.event = event;
    }

    public static Callback builder(GenericEvent event) {
        return new Callback(event);
    }

    public Callback addEmbeds(MessageEmbed... embeds) {
        this.embeds.addAll(Arrays.asList(embeds));
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public Callback addActionRow(ItemComponent... itemComponents) {
        this.actionRows.add(itemComponents);
        return this;
    }

    @SuppressWarnings("DataFlowIssue")
    public void queue() {
        if (EventUtils.isAcknowledged(event)) {
            WebhookMessageCreateAction<Message> messageCreateAction = EventUtils.getHook(event).sendMessageEmbeds(embeds);

            for (ItemComponent[] actionRow : this.actionRows) {
                messageCreateAction.addActionRow(actionRow);
            }

            messageCreateAction.queue();
        } else {
            ReplyCallbackAction callbackAction = EventUtils.getCallback(event).addEmbeds(embeds);

            for (ItemComponent[] actionRow : this.actionRows) {
                callbackAction.addActionRow(actionRow);
            }

            callbackAction.queue();
        }
    }
}
