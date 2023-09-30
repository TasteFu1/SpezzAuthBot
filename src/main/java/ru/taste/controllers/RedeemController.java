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
import ru.taste.database.entities.AppProfile;
import ru.taste.database.entities.License;
import ru.taste.database.entities.Subscription;
import ru.taste.database.entities.Account;
import ru.taste.database.repositories.AppProfileRepository;
import ru.taste.database.repositories.LicenseRepository;
import ru.taste.database.repositories.SubscriptionRepository;
import ru.taste.database.repositories.AccountRepository;
import ru.taste.utilities.discord.ChannelUtils;
import ru.taste.utilities.discord.EmbedUtils;
import ru.taste.utilities.security.EncryptionUtils;
import ru.taste.utilities.throwable.ReportUtils;
import ru.taste.utilities.web.NetworkUtils;
import ru.taste.utilities.web.ResponseUtils;

@RestController
public class RedeemController {
    private Instance instance() {
        return Instance.get();
    }

    private AccountRepository accountRepository() {
        return instance().getAccountRepository();
    }

    private SubscriptionRepository subscriptionRepository() {
        return instance().getSubscriptionRepository();
    }

    private LicenseRepository licenseRepository() {
        return instance().getLicenseRepository();
    }

    private AppProfileRepository appProfileRepository() {
        return instance().getAppProfileRepository();
    }

    private PrivateChannel privateChannel(String discordId) {
        return ChannelUtils.getChannelByUserId(discordId);
    }

    @GetMapping("/redeem/subscription")
    private ModelAndView redeemSubscriptionGet(HttpServletRequest request, @RequestParam String token) {
        try {
            if (NetworkUtils.isUsingVPN(request)) {
                return ResponseUtils.error("Turn off VPN services.");
            }

        } catch (IOException exception) {
            return ReportUtils.reportModel(exception);
        }

        return new ModelAndView("Redeem").addObject("token", token).addObject("type", "subscription");
    }

    @PostMapping("/redeem/subscription")
    private ModelAndView redeemSubscriptionPost(HttpServletRequest request, @RequestParam("h-captcha-response") String captchaResponse, @RequestParam String token) {
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

        String decrypted;

        try {
            decrypted = EncryptionUtils.decrypt(token);
        } catch (Exception exception) {
            return ReportUtils.reportModel(exception);
        }

        String[] values = decrypted.split(":");

        if (values.length != 2) {
            return ResponseUtils.error("Invalid token.");
        }

        String discordId = values[0];
        String code = values[1];

        Account account = accountRepository().findByDiscordId(discordId);

        if (account == null) {
            return ResponseUtils.error("Unauthorized user.");
        }

        Subscription subscription = subscriptionRepository().findByCode(code);

        if (subscription == null) {
            return ResponseUtils.error("Invalid subscription code.");
        }

        if (subscription.getAccountId() != null) {
            return ResponseUtils.error("Subscription code has already been used.");
        }

        if (account.getSubscription() != null && !account.getSubscription().hasExpired()) {
            return ResponseUtils.error("You already have an active subscription.");
        }

        account.setSubscriptionId(subscription.getId());
        account.incrementAppLimit(subscription.getAppLimit());

        accountRepository().save(account);

        subscription.setIpAddress(NetworkUtils.getIpAddress(request));
        subscription.setUsageDate(System.currentTimeMillis());
        subscription.setAccountId(account.getId());

        subscriptionRepository().save(subscription);

        if (privateChannel(discordId) != null) {
            privateChannel(discordId).sendMessageEmbeds(EmbedUtils.success().setDescription("Subscription has been activated, run `/user` command.").build()).queue();
        }

        return ResponseUtils.success("Subscription has been activated, return to discord.");
    }

    @GetMapping("/redeem/license")
    private ModelAndView redeemLicenseGet(HttpServletRequest request, @RequestParam String token) {
        try {
            if (NetworkUtils.isUsingVPN(request)) {
                return ResponseUtils.error("Turn off VPN services.");
            }

        } catch (IOException exception) {
            return ReportUtils.reportModel(exception);
        }

        return new ModelAndView("Redeem").addObject("token", token).addObject("type", "license");
    }

    @PostMapping("/redeem/license")
    private ModelAndView redeemLicensePost(HttpServletRequest request, @RequestParam("h-captcha-response") String captchaResponse, @RequestParam String token) {
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

        String decrypted;

        try {
            decrypted = EncryptionUtils.decrypt(token);
        } catch (Exception exception) {
            return ReportUtils.reportModel(exception);
        }

        String[] values = decrypted.split(":");

        if (values.length != 2) {
            return ResponseUtils.error("Invalid token.");
        }

        String discordId = values[0];
        String code = values[1];

        Account account = accountRepository().findByDiscordId(discordId);

        if (account == null) {
            return ResponseUtils.error("Unauthorized user.");
        }

        License license = licenseRepository().findByCode(code);

        if (license == null) {
            return ResponseUtils.error("Invalid license code.");
        }

        if (license.getAccountId() != null) {
            return ResponseUtils.error("License code has already been used.");
        }

        if (licenseRepository().findAllByAccountIdAndApplicationId(account.getId(), license.getApplicationId()).stream().anyMatch(licenseIn -> !licenseIn.hasExpired())) {
            return ResponseUtils.warning("You already have this application.");
        }

        license.setIpAddress(NetworkUtils.getIpAddress(request));
        license.setUsageDate(System.currentTimeMillis());
        license.setAccountId(account.getId());

        licenseRepository().save(license);

        AppProfile appProfile = appProfileRepository().findByAccountIdAndApplicationId(account.getId(), license.getApplicationId());

        if (appProfile == null) {
            appProfile = AppProfile.builder().accountId(account.getId()).licenseId(license.getId()).applicationId(license.getApplicationId()).role(AppProfile.Role.CUSTOMER).build();
        } else {
            appProfile.setLicenseId(license.getId());
        }

        appProfileRepository().save(appProfile);

        if (privateChannel(discordId) != null) {
            String message = "License has been activated, run `/user` command and press `Library` button.";
            MessageEmbed embed = EmbedUtils.success().setDescription(message).build();

            privateChannel(discordId).sendMessageEmbeds(embed).queue();
        }

        return ResponseUtils.success("License has been activated, return to discord.");
    }
}
