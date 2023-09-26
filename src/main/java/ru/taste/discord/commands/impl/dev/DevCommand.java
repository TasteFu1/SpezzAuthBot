package ru.taste.discord.commands.impl.dev;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.util.concurrent.TimeUnit;

import ru.taste.database.entities.Account;
import ru.taste.database.entities.Subscription;
import ru.taste.discord.commands.Command;
import ru.taste.discord.handlers.impl.ButtonHandler;
import ru.taste.discord.handlers.impl.ModalHandler;
import ru.taste.discord.handlers.impl.SelectMenuHandler;
import ru.taste.discord.handlers.impl.SlashCommandHandler;
import ru.taste.discord.override.AdvancedButton;
import ru.taste.utilities.java.StringUtils;

public class DevCommand extends Command {
    public DevCommand() {
        super(Commands.slash("dev", "param"));
    }

    @Override
    public void execute(SlashCommandHandler handler) {
        SlashCommandInteractionEvent event = handler.getEvent();
        Account account = accountRepository.findByDiscordId(event.getUser().getId());

        if (account == null || account.getRole() != Account.Role.ADMIN) {
            event.replyEmbeds(error().setDescription("Insufficent permissions").build()).queue();
            return;
        }

        ReplyCallbackAction callback = event.replyEmbeds(info().setTitle("Developer Panel").build());

        callback.addActionRow( //
                AdvancedButton.success().id("dev_generate_user_button").label("Generate User Credentials").build(), //
                AdvancedButton.success().id("dev_generate_subscription_button").label("Generate Subscription Code").build() //
        );

        callback.addActionRow( //
                AdvancedButton.secondary().id("dev_clear_user_repository_button").label("Clear User Repository").build(), //
                AdvancedButton.secondary().id("dev_clear_subscription_repository_button").label("Clear Subscription Repository").build() //
        );

        callback.queue();
    }

    @Override
    public void execute(ButtonHandler handler) {
        ButtonInteractionEvent event = handler.getEvent();

        switch (handler.getButtonId()) {
            case "dev_generate_user_button" -> {
                String email = String.format("user%s@email.com", StringUtils.randomDigit(8).toLowerCase());
                String password = StringUtils.random(12).toLowerCase();

                Account account;

                try {
                    account = Account.builder().email(email).password(password).role(Account.Role.MEMBER).build();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                accountRepository.save(account);

                MessageEmbed embed = success() //
                        .setTitle("Credentials") //
                        .addField(new MessageEmbed.Field("Email", account.getEmail(), false)) //
                        .addField(new MessageEmbed.Field("Password", account.getPassword(), false)) //
                        .build();

                event.replyEmbeds(embed).queue();
            }

            case "dev_generate_subscription_button" -> {
                Subscription subscription = Subscription.builder() //
                        .expirationDate(TimeUnit.DAYS.toMillis(90)) //
                        .type(Subscription.Type.PROFESSIONAL) //
                        .build();

                subscriptionRepository.save(subscription);

                MessageEmbed embed = success() //
                        .setTitle("Subscription") //
                        .addField(new MessageEmbed.Field("Code", subscription.getCode(), false)) //
                        .build();

                event.replyEmbeds(embed).queue();
            }

            case "dev_clear_user_repository_button" -> {
                accountRepository.deleteAll();
                event.replyEmbeds(info().setDescription("User Repository was cleared successfully").build()).queue();
            }

            case "dev_clear_subscription_repository_button" -> {
                subscriptionRepository.deleteAll();
                event.replyEmbeds(info().setDescription("Subscription Repository was cleared successfully").build()).queue();
            }

            case "dev_generate_slot_button" -> {
                StringSelectMenu selectMenu = StringSelectMenu.create("dev_generate_slot_menu") //
                        .setPlaceholder("Choose slots quantity") //
                        .addOption("1", "1") //
                        .addOption("3", "3") //
                        .addOption("5", "5") //
                        .addOption("10", "10") //
                        .addOption("100", "100") //
                        .build();

                event.deferReply().addActionRow(selectMenu).queue();
            }
        }
    }

    @Override
    public void execute(ModalHandler handler) {

    }

    @Override
    public void execute(SelectMenuHandler handler) {

    }
}
