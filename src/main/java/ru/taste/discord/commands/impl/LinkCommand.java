package ru.taste.discord.commands.impl;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import ru.taste.database.entities.Account;
import ru.taste.discord.commands.Command;
import ru.taste.discord.handlers.impl.ButtonHandler;
import ru.taste.discord.handlers.impl.ModalHandler;
import ru.taste.discord.handlers.impl.SelectMenuHandler;
import ru.taste.discord.handlers.impl.SlashCommandHandler;
import ru.taste.utilities.security.EncryptionUtils;
import ru.taste.utilities.throwable.ReportUtils;

public class LinkCommand extends Command {
    public LinkCommand() {
        super(Commands.slash("link", "param"));
    }

    @Override
    public void execute(SlashCommandHandler handler) {
        SlashCommandInteractionEvent event = handler.getEvent();

        String discordId = event.getUser().getId();
        Account account = accountRepository.findByDiscordId(discordId);

        if (account == null) {
            String link;

            try {
                link = String.format("https://spezz.space/login?token=%s", EncryptionUtils.encrypt(discordId));
            } catch (Exception exception) {
                event.replyEmbeds(ReportUtils.reportEmbed(exception)).queue();
                return;
            }

            MessageEmbed embed = info() //
                    .setTitle("Account Linking") //
                    .setDescription(String.format("Link your account by following this link %s", link)) //
                    .build();

            event.replyEmbeds(embed).queue();
            return;
        }

        MessageEmbed embed = warning() //
                .setDescription("Your discord is already linked to your spezz account") //
                .build();

        event.replyEmbeds(embed).queue();
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
