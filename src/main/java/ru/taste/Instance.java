package ru.taste;


import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

import lombok.Getter;
import ru.taste.database.entities.Account;
import ru.taste.database.repositories.ApplicationRepository;
import ru.taste.database.repositories.AppProfileRepository;
import ru.taste.database.repositories.DiscordTokenRepository;
import ru.taste.database.repositories.FileRequestRepository;
import ru.taste.database.repositories.LicenseRepository;
import ru.taste.database.repositories.ReportRepository;
import ru.taste.database.repositories.SubscriptionRepository;
import ru.taste.database.repositories.AccountRepository;
import ru.taste.discord.commands.CommandHandler;
import ru.taste.discord.handlers.EventListener;
import ru.taste.discord.oauth.DiscordOAuth;
import ru.taste.utilities.java.StringUtils;

@Getter
public class Instance {
    /*** discord api instance ***/
    private JDA jda;

    /*** discord oauth instance ***/
    private DiscordOAuth discordOAuth;

    /*** discord bot values ***/
    private final String BOT_TOKEN = "MTE0ODcwNTk1NTg1NzU4NDE0MA.GF3YV5.QaYowj144Bo4ys3YkN7NuwzP07p8JYqb4YLqqk";
    private final List<GatewayIntent> BOT_INTENTS = List.of(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES);

    /*** discord oauth values ***/
    private final String CLIENT_ID = "1127176221000736868";
    private final String CLIENT_SECRET = "fRLRD8nrVTG-VGYH69luMwYNG8-lS2TF";
    private final String REDIRECT_URI = "https://spezz.space/discord/authorization";
    private final String[] SCOPES = new String[]{"identify", "guilds", "guilds.join"};

    /*** repositories ***/
    private AccountRepository accountRepository;
    private SubscriptionRepository subscriptionRepository;
    private ReportRepository reportRepository;
    private ApplicationRepository applicationRepository;
    private LicenseRepository licenseRepository;
    private FileRequestRepository fileRequestRepository;
    private AppProfileRepository appProfileRepository;
    private DiscordTokenRepository discordTokenRepository;

    /*** handlers ***/
    private CommandHandler commandHandler;

    public void initialize(ConfigurableApplicationContext context) throws InterruptedException {
        this.accountRepository = context.getBean(AccountRepository.class);
        this.subscriptionRepository = context.getBean(SubscriptionRepository.class);
        this.reportRepository = context.getBean(ReportRepository.class);
        this.applicationRepository = context.getBean(ApplicationRepository.class);
        this.licenseRepository = context.getBean(LicenseRepository.class);
        this.fileRequestRepository = context.getBean(FileRequestRepository.class);
        this.appProfileRepository = context.getBean(AppProfileRepository.class);
        this.discordTokenRepository = context.getBean(DiscordTokenRepository.class);

        this.jda = JDABuilder.createDefault(BOT_TOKEN) //
                .enableIntents(BOT_INTENTS) //
                .addEventListeners(new EventListener()) //
                .build() //
                .awaitReady();

        this.discordOAuth = DiscordOAuth.builder() //
                .clientID(CLIENT_ID) //
                .clientSecret(CLIENT_SECRET) //
                .redirectUri(REDIRECT_URI) //
                .scopes(SCOPES) //
                .build();

        this.commandHandler = CommandHandler.builder() //
                .build(jda);

        //admins
        for (String discordId : List.of("1125786717526425640", "1012334624862642176", "782423823814754324", "1017719595546718259")) {
            setupAccount(discordId, Account.Role.ADMIN);
        }

        //test users
        for (String discordId : List.of("1127189075389055037")) {
            setupAccount(discordId, Account.Role.MEMBER);
        }
    }

    private void setupAccount(String discordId, Account.Role role) {
        Account account = accountRepository.findByDiscordId(discordId);

        if (account == null) {
            String email = String.format("user%s@email.com", StringUtils.randomDigit(8).toLowerCase());
            String password = StringUtils.random(12).toLowerCase();

            try {
                account = Account.builder().email(email).password(password).discordId(discordId).role(role).build();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            accountRepository.save(account);
        }
    }

    private enum Singleton {
        APPLICATION;

        private final Instance value;

        Singleton() {
            this.value = new Instance();
        }
    }

    public static Instance get() {
        return Singleton.APPLICATION.value;
    }
}
