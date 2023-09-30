package ru.taste.discord.commands.impl.dev;

import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import ru.taste.database.entities.Report;
import ru.taste.discord.commands.Command;
import ru.taste.discord.handlers.impl.ButtonHandler;
import ru.taste.discord.handlers.impl.ModalHandler;
import ru.taste.discord.handlers.impl.SelectMenuHandler;
import ru.taste.discord.handlers.impl.SlashCommandHandler;
import ru.taste.utilities.java.StringUtils;
import ru.taste.utilities.security.EncryptionUtils;

public class TestCommand extends Command {
    public TestCommand() {
        super(Commands.slash("test", "param"));
    }

    @Override
    public void execute(SlashCommandHandler handler) {
        handler.getEvent().reply(instance.getDiscordOAuth().getAuthorizationURL()).queue();
    }

    @Override
    public void execute(ButtonHandler handler) {

    }

    @Override
    public void execute(ModalHandler handler) {

    }

    @Override
    public void execute(SelectMenuHandler handler) {

    }
}
