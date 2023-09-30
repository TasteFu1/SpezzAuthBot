package ru.taste.controllers;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import ru.taste.Instance;
import ru.taste.database.entities.Account;
import ru.taste.database.repositories.AccountRepository;
import ru.taste.utilities.discord.ChannelUtils;
import ru.taste.utilities.discord.EmbedUtils;
import ru.taste.utilities.security.EncryptionUtils;
import ru.taste.utilities.throwable.ReportUtils;
import ru.taste.utilities.web.NetworkUtils;
import ru.taste.utilities.web.ResponseUtils;

@RestController
public class LoginController {
    private Instance instance() {
        return Instance.get();
    }

    private AccountRepository accountRepository() {
        return instance().getAccountRepository();
    }

    private PrivateChannel privateChannel(String discordId) {
        return ChannelUtils.getChannelByUserId(discordId);
    }

    @GetMapping("/login")
    private ModelAndView loginGet(HttpServletRequest request, @RequestParam String token) {
        try {
            if (NetworkUtils.isUsingVPN(request)) {
                return ResponseUtils.error("Turn off VPN services.");
            }

        } catch (IOException exception) {
            return ReportUtils.reportModel(exception);
        }

        return new ModelAndView("Login").addObject("token", token);
    }

    @PostMapping("/login")
    private ModelAndView loginPost(HttpServletRequest request, @RequestParam("h-captcha-response") String captchaResponse, @RequestParam String token, @RequestParam String email, @RequestParam String password) {
        try {
            if (NetworkUtils.isUsingVPN(request)) {
                return ResponseUtils.error("Turn off VPN services.");
            }

            if (NetworkUtils.isCaptchaInvalid(captchaResponse)) {
                return ResponseUtils.error("Invalid captcha.");
            }

        } catch (IOException exception) {
            return ReportUtils.reportModel(exception);
        }

        Account account = accountRepository().findByEmail(email);

        if (account == null) {
            return ResponseUtils.error("Unauthorized user.");
        }

        try {
            if (!EncryptionUtils.encrypt(password).equals(account.getPassword())) {
                return ResponseUtils.error("Invalid credentials.");
            }

        } catch (Exception exception) {
            return ReportUtils.reportModel(exception);
        }

        String discordId;

        try {
            discordId = EncryptionUtils.decrypt(token);
        } catch (Exception exception) {
            return ReportUtils.reportModel(exception);
        }

        if (accountRepository().findByDiscordId(discordId) != null) {
            return ResponseUtils.warning("Discord account is already linked to another account.");
        }

        account.setDiscordId(discordId);
        accountRepository().save(account);

        if (privateChannel(discordId) != null) {
            String title = "Account Linking";
            String message = "Account has been linked successfully, run `/user` command.";
            MessageEmbed embed = EmbedUtils.success().setTitle(title).setDescription(message).build();

            privateChannel(discordId).sendMessageEmbeds(embed).queue();
        }

        return ResponseUtils.success("Account has been linked successfully, return to discord.");
    }
}
