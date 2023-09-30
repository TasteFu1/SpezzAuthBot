package ru.taste.controllers;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

import ru.taste.Instance;
import ru.taste.database.entities.DiscordToken;
import ru.taste.database.repositories.AccountRepository;
import ru.taste.database.repositories.DiscordTokenRepository;
import ru.taste.discord.oauth.DiscordOAuth;
import ru.taste.discord.oauth.model.TokensResponse;
import ru.taste.utilities.discord.ChannelUtils;
import ru.taste.utilities.discord.EmbedUtils;
import ru.taste.utilities.throwable.ReportUtils;
import ru.taste.utilities.web.ResponseUtils;

@RestController
public class DiscordController {

    private Instance instance() {
        return Instance.get();
    }

    private DiscordOAuth discordOAuth() {
        return instance().getDiscordOAuth();
    }

    private AccountRepository accountRepository() {
        return instance().getAccountRepository();
    }

    private DiscordTokenRepository discordTokenRepository() {
        return instance().getDiscordTokenRepository();
    }

    private PrivateChannel privateChannel(String discordId) {
        return ChannelUtils.getChannelByUserId(discordId);
    }

    @GetMapping("/discord/authorization")
    public ModelAndView discordAuthorizationGet(@RequestParam("code") String code) {
        try {
            return discordAuthorization(code);
        } catch (IOException exception) {
            return ReportUtils.reportModel(exception);
        }
    }

    public ModelAndView discordAuthorization(String code) throws IOException {
        TokensResponse tokensResponse = discordOAuth().getTokens(code);

        String discordId = tokensResponse.getDiscordId();
        String accessToken = tokensResponse.getAccessToken();
        String refreshToken = tokensResponse.getRefreshToken();

        long currentMillis = System.currentTimeMillis();
        int expiresIn = tokensResponse.getExpiresIn();

        if (accountRepository().findByDiscordId(discordId) == null) {
            return ResponseUtils.error("Discord not linked.");
        }

        DiscordToken discordToken = discordTokenRepository().findByDiscordId(discordId);

        if (discordToken == null) {
            discordToken = DiscordToken.builder() //
                    .discordId(discordId) //
                    .accessToken(accessToken) //
                    .refreshToken(refreshToken) //
                    .refreshDate(currentMillis) //
                    .expriesIn(expiresIn) //
                    .build();
        } else {
            discordToken.setAccessToken(accessToken);
            discordToken.setRefreshToken(refreshToken);
            discordToken.setRefreshDate(currentMillis);
            discordToken.setExpriesIn(expiresIn);
        }

        discordTokenRepository().save(discordToken);

        if (privateChannel(discordId) != null) {
            String title = "Discord Authorization";
            String message = "Account was authorized successfully.";
            MessageEmbed embed = EmbedUtils.info().setTitle(title).setDescription(message).build();

            privateChannel(discordId).sendMessageEmbeds(embed).queue();
        }

        String message = "User %s was authorized successfully, return to discord.";
        String username = tokensResponse.getUser().getUsername();

        return ResponseUtils.success(String.format(message, username));
    }
}
