package ru.taste.discord.handlers.impl;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import ru.taste.Instance;
import ru.taste.discord.commands.Command;
import ru.taste.discord.handlers.IHandler;

@SuppressWarnings("LombokGetterMayBeUsed")
public class ButtonHandler implements IHandler {
    @Getter
    private ButtonInteractionEvent event;

    @Override
    public void onEvent(GenericEvent event) {
        if (!(event instanceof ButtonInteractionEvent eventIn)) {
            return;
        }

        for (Command command : Instance.get().getCommandHandler().getCommandList()) {
            this.event = eventIn;
            command.execute(this);
        }
    }

    public String getButtonId() {
        String buttonId = event.getComponentId();
        return buttonId.contains("/") ? buttonId.split("/")[0] : buttonId;
    }

    public List<String> getValues() {
        String buttonId = event.getComponentId();
        List<String> values = new ArrayList<>();

        if (buttonId.contains("/")) {
            String[] split = buttonId.split("/");
            values.addAll(Arrays.asList(split).subList(1, split.length));
        }

        return values;
    }
}
