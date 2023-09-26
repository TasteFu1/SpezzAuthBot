package ru.taste.discord.handlers.impl;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import ru.taste.Instance;
import ru.taste.discord.commands.Command;
import ru.taste.discord.handlers.IHandler;

@SuppressWarnings("LombokGetterMayBeUsed")
public class SelectMenuHandler implements IHandler {
    @Getter
    private StringSelectInteractionEvent event;

    @Override
    public void onEvent(GenericEvent event) {
        if (!(event instanceof StringSelectInteractionEvent eventIn)) {
            return;
        }

        for (Command command : Instance.get().getCommandHandler().getCommandList()) {
            this.event = eventIn;
            command.execute(this);
        }
    }

    public String getMenuId() {
        String menuId = event.getComponentId();
        return menuId.contains("/") ? menuId.split("/")[0] : menuId;
    }

    public List<String> getValues() {
        String menuId = event.getComponentId();
        List<String> values = new ArrayList<>();

        if (menuId.contains("/")) {
            String[] split = menuId.split("/");
            values.addAll(Arrays.asList(split).subList(1, split.length));
        }

        return values;
    }
}
