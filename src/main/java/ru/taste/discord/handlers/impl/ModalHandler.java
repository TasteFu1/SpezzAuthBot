package ru.taste.discord.handlers.impl;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import ru.taste.Instance;
import ru.taste.discord.commands.Command;
import ru.taste.discord.handlers.IHandler;

@SuppressWarnings("LombokGetterMayBeUsed")
public class ModalHandler implements IHandler {
    @Getter
    private ModalInteractionEvent event;

    @Override
    public void onEvent(GenericEvent event) {
        if (!(event instanceof ModalInteractionEvent eventIn)) {
            return;
        }

        for (Command command : Instance.get().getCommandHandler().getCommandList()) {
            this.event = eventIn;
            command.execute(this);
        }
    }

    public String getModalId() {
        String modalId = event.getModalId();
        return modalId.contains("/") ? modalId.split("/")[0] : modalId;
    }

    public List<String> getValues() {
        String modalId = event.getModalId();
        List<String> values = new ArrayList<>();

        if (modalId.contains("/")) {
            String[] split = modalId.split("/");
            values.addAll(Arrays.asList(split).subList(1, split.length));
        }

        return values;
    }
}
