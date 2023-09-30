package ru.taste.discord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.awt.Color;

import lombok.Getter;
import ru.taste.Instance;
import ru.taste.database.repositories.ApplicationRepository;
import ru.taste.database.repositories.AppProfileRepository;
import ru.taste.database.repositories.DiscordTokenRepository;
import ru.taste.database.repositories.FileRequestRepository;
import ru.taste.database.repositories.LicenseRepository;
import ru.taste.database.repositories.ReportRepository;
import ru.taste.database.repositories.SubscriptionRepository;
import ru.taste.database.repositories.AccountRepository;
import ru.taste.discord.handlers.impl.ButtonHandler;
import ru.taste.discord.handlers.impl.ModalHandler;
import ru.taste.discord.handlers.impl.SelectMenuHandler;
import ru.taste.discord.handlers.impl.SlashCommandHandler;

public abstract class Command {
    /*** application instance ***/
    protected final Instance instance = Instance.get();

    /*** repositories ***/
    protected final AccountRepository accountRepository = instance.getAccountRepository();
    protected final SubscriptionRepository subscriptionRepository = instance.getSubscriptionRepository();
    protected final ReportRepository reportRepository = instance.getReportRepository();
    protected final ApplicationRepository applicationRepository = instance.getApplicationRepository();
    protected final LicenseRepository licenseRepository = instance.getLicenseRepository();
    protected final FileRequestRepository fileRequestRepository = instance.getFileRequestRepository();
    protected final AppProfileRepository appProfileRepository = instance.getAppProfileRepository();
    protected final DiscordTokenRepository discordTokenRepository = instance.getDiscordTokenRepository();

    /*** colors ***/
    protected final Color INFO_COLOR = new Color(0x80ff);
    protected final Color ERROR_COLOR = new Color(0xFF0042);
    protected final Color SUCCESS_COLOR = new Color(0xff97);
    protected final Color WARNING_COLOR = new Color(0xffab00);

    /*** constants ***/
    @Getter
    private final SlashCommandData data;

    /*** constructor ***/
    public Command(SlashCommandData data) {
        this.data = data;
    }

    /*** embeds ***/
    public EmbedBuilder embed() {
        return new EmbedBuilder();
    }

    public EmbedBuilder info() {
        return embed().setTitle("Info").setColor(INFO_COLOR);
    }

    public EmbedBuilder error() {
        return embed().setTitle("Error").setColor(ERROR_COLOR);
    }

    public EmbedBuilder success() {
        return embed().setTitle("Success").setColor(SUCCESS_COLOR);
    }

    public EmbedBuilder warning() {
        return embed().setTitle("Warning").setColor(WARNING_COLOR);
    }

    public String[] spezzFooter() {
        return new String[]{"spezz.com", "https://spezz.space/assets/images/SE-512.png"};
    }

    public Emoji custom(String name, long id) {
        return Emoji.fromCustom(name, id, false);
    }

    /*** methods ***/
    public abstract void execute(SlashCommandHandler handler);

    public abstract void execute(ButtonHandler handler);

    public abstract void execute(ModalHandler handler);

    public abstract void execute(SelectMenuHandler handler);
}
