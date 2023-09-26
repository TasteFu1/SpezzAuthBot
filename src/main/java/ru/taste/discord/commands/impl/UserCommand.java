package ru.taste.discord.commands.impl;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Stream;

import ru.taste.database.entities.Account;
import ru.taste.database.entities.AppProfile;
import ru.taste.database.entities.Application;
import ru.taste.database.entities.FileRequest;
import ru.taste.database.entities.License;
import ru.taste.database.entities.Subscription;
import ru.taste.discord.commands.Command;
import ru.taste.discord.handlers.impl.ButtonHandler;
import ru.taste.discord.handlers.impl.ModalHandler;
import ru.taste.discord.handlers.impl.SelectMenuHandler;
import ru.taste.discord.handlers.impl.SlashCommandHandler;
import ru.taste.discord.override.AdvancedButton;
import ru.taste.discord.override.Callback;
import ru.taste.discord.override.ValueModal;
import ru.taste.discord.override.ValueSelectMenu;
import ru.taste.discord.override.enums.EmojiEnum;
import ru.taste.utilities.java.StringUtils;
import ru.taste.utilities.java.TimerUtils;
import ru.taste.utilities.security.EncryptionUtils;
import ru.taste.utilities.throwable.ReportUtils;
import ru.taste.utilities.xchart.ChartUtils;

@SuppressWarnings("DataFlowIssue")
public class UserCommand extends Command {
    public UserCommand() {
        super(Commands.slash("user", "param"));
    }

    @Override
    public void execute(SlashCommandHandler handler) {
        SlashCommandInteractionEvent event = handler.getEvent();
        Account account = accountRepository.findByDiscordId(event.getUser().getId());

        if (account == null) {
            nullUserMessage(event);
            return;
        }

        userPanel(event, account);
    }


    @Override
    public void execute(ButtonHandler handler) {
        ButtonInteractionEvent event = handler.getEvent();
        Account account = accountRepository.findByDiscordId(event.getUser().getId());

        if (account == null) {
            return;
        }

        List<String> values = handler.getValues();

        switch (handler.getButtonId()) {
            case "user_change_username_button" -> changeUsernameButton(event);

            case "user_redeem_subscription_button" -> redeemSubscriptionButton(event);

            case "user_dashboard_button" -> dashboardPanelButton(event, account);

            case "user_library_button" -> libraryButton(event, account);

            case "user_new_app_button" -> newApplicationButton(event);

            case "user_back_to_user_panel_button" -> userPanel(event, account);

            case "user_manage_app_button" -> manageApplicationButton(event, account);

            case "user_edit_app_data_button" -> editApplicationDataButton(event, values.get(0));

            case "user_set_hardware_reset_period_button" -> setHardwareIdResetPeriodButton(event, values.get(0));

            case "user_toggle_app_status_button" -> toggleApplicationButton(event, values.get(0));

            case "user_app_keys_management_button" -> keysManagementPanel(event, values.get(0));

            case "user_back_to_app_panel_button" -> applicationPanel(event, values.get(0));

            case "user_generate_app_keys_button" -> generateKeysButton(event, values.get(0));

            case "user_download_file_button" -> downloadFileButton(event, values.get(0));

            case "user_show_app_keys_button" -> showKeysButton(event, values.get(0));

            case "user_show_specific_app_keys_button" -> showSpecificKeysButton(event, values);

            case "user_download_keys_button" -> downloadKeysButton(event, values.get(0), values.get(1));

            case "user_show_specific_key_button" -> showSpecificKeyButton(event, values.get(0));

            case "user_delete_unused_app_keys_button" -> deleteUnusedKeysButton(event, values.get(0));

            case "user_app_sales_statistic_button" -> salesStatisticButton(event, values.get(0));

            case "user_redeem_license_button" -> redeemLicenseButton(event);

            case "user_app_users_management_button" -> usersManagementButton(event, values.get(0));

            case "user_show_app_users_button" -> showUsersButton(event, values.get(0));

            case "user_show_specific_users_button" -> showSpecificUsersButton(event, values);

            case "user_show_specific_app_user_button" -> showSpecificUserButton(event, values.get(0));

            case "user_grant_app_user_admin_button" -> grantAdminButton(event, values.get(0));

            case "user_ban_app_user_button" -> banAppProfileButton(event, values.get(0));

            case "user_delete_app_user_button" -> deleteAppProlileButton(event, values.get(0));

            case "user_reset_app_user_hardware_id_button" -> resetUserHardwareIdButton(event, values.get(0));

            case "user_set_app_user_hardware_id_button" -> setUserHardwareIdButton(event, values.get(0));

            case "user_manage_license_button" -> manageLicenseButton(event, account);

            case "user_show_app_profile_button" -> appProfilePanel(event, values.get(0));

            case "user_change_profile_name_button" -> changeProfileNameButton(event, values.get(0));

            case "user_manage_profile_hardware_button" -> manageProfileHardwareButton(event, values.get(0));
        }
    }

    @Override
    public void execute(ModalHandler handler) {
        ModalInteractionEvent event = handler.getEvent();
        Account account = accountRepository.findByDiscordId(event.getUser().getId());

        if (account == null) {
            return;
        }

        List<String> values = handler.getValues();

        switch (handler.getModalId()) {
            case "user_change_username_modal" -> changeUsernameModal(event, account);

            case "user_redeem_subscription_modal" -> redeemSubscriptionModal(event, account);

            case "user_new_app_modal" -> newApplicationModal(event, account);

            case "user_edit_app_data_modal" -> editApplicationDataModal(event, values.get(0));

            case "user_show_specific_key_modal" -> showSpecificKeyModal(event, values.get(0));

            case "user_delete_unused_app_keys_modal" -> deleteUnusedKeysModal(event, values.get(0));

            case "user_redeem_license_modal" -> redeemLicenseModal(event, account);

            case "user_show_specific_app_user_modal" -> showSpeicficUserModal(event, values.get(0));

            case "user_set_app_user_hardware_id_modal" -> setUserHardwareIdModal(event, values.get(0));

            case "user_change_profile_name_modal" -> changeProfileNameModal(event, values.get(0));

            case "user_manage_profile_hardware_modal" -> manageProfileHardwareModal(event, values.get(0));

        }
    }

    @Override
    public void execute(SelectMenuHandler handler) {
        StringSelectInteractionEvent event = handler.getEvent();
        Account account = accountRepository.findByDiscordId(event.getUser().getId());

        if (account == null) {
            return;
        }

        List<String> values = handler.getValues();

        switch (handler.getMenuId()) {
            case "user_manage_app_menu" -> applicationPanel(event, event.getValues().get(0));

            case "user_set_hardware_id_reset_period_menu" -> setHardwareIdResetPeriodMenu(event, values.get(0));

            case "user_select_keys_expiry_date_menu" -> selectKeysQuantityMenu(event, values.get(0));

            case "user_select_keys_qunatity_menu" -> generateKeysMenu(event, values.get(0), values.get(1));

            case "user_select_statistic_period_menu" -> salesStatisticMenu(event, values.get(0));

            case "user_manage_license_menu" -> manageLicenseMenu(event);
        }
    }

    private void nullUserMessage(SlashCommandInteractionEvent event) {
        MessageEmbed embed = warning() //
                .setTitle("Account not found") //
                .setDescription("If you have already registered on spezz run `/link` command.") //
                .setFooter(spezzFooter()[0], spezzFooter()[1]) //
                .build();

        event.replyEmbeds(embed).queue();
    }

    private void userPanel(GenericEvent event, Account account) {
        StringBuilder subscriptionInfo = new StringBuilder();
        Subscription subscription = account.getSubscription();

        if (subscription == null) {
            subscriptionInfo.append("None");
        } else {
            subscriptionInfo.append(String.format("* **Type:** %s", subscription.getType().getFuncyName()));
            subscriptionInfo.append("\n");
            subscriptionInfo.append(String.format("* **Since:** %s", StringUtils.timestamp(subscription.getUsageDate())));
            subscriptionInfo.append("\n");
            subscriptionInfo.append(String.format("* **Expiry:** %s", StringUtils.timestamp(subscription.getUsageDate(), subscription.getExpirationDate())));
            subscriptionInfo.append("\n");
        }

        MessageEmbed embed = info() //
                .setTitle("User Panel") //
                .addField("Username", account.getUsername(), true) //
                .addBlankField(true) //
                .addField("Registration Date", StringUtils.timestamp(account.getRegistrationDate()), true) //
                .addField("Email", account.getEmail(), false) //
                .addField("Role", account.getRole().getFuncyName(), false) //
                .addField("Subscription", subscriptionInfo.toString(), false) //
                .setFooter(spezzFooter()[0], spezzFooter()[1]) //
                .build();

        Callback callback = Callback.builder(event).addEmbeds(embed);

        callback.addActionRow( //
                AdvancedButton.primary().id("user_change_username_button").label("Change Username").emoji(EmojiEnum.EDIT).build(), //
                AdvancedButton.success().id("user_redeem_subscription_button").label("Redeem Subscription").emoji(EmojiEnum.KEY).build() //
        );

        callback.addActionRow( //
                AdvancedButton.secondary().id("user_dashboard_button").label("Dashboard").emoji(EmojiEnum.PANEL).disabled(subscription == null).build(), //
                AdvancedButton.secondary().id("user_library_button").label("Library").emoji(EmojiEnum.APPS).build() //
        );

        callback.queue();
    }

    private void changeUsernameButton(ButtonInteractionEvent event) {
        TextInput textInput = TextInput.create("user_change_username_input", "Username Input", TextInputStyle.SHORT) //
                .setRequiredRange(3, 20) //
                .setPlaceholder("Username") //
                .build();

        Modal modal = Modal.create("user_change_username_modal", "Edit Username Form") //
                .addActionRow(textInput) //
                .build();

        event.replyModal(modal).queue();
    }

    private void changeUsernameModal(ModalInteractionEvent event, Account account) {
        String username = event.getValue("user_change_username_input").getAsString();

        if (StringUtils.startWithNumber(username)) {
            event.replyEmbeds(error().setDescription("Username can not start with number.").build()).queue();
            return;
        }

        if (StringUtils.containsSpecialSymbols(username)) {
            event.replyEmbeds(error().setDescription("Username can not contain special symbols.").build()).queue();
            return;
        }

        if (username.equalsIgnoreCase("null")) {
            event.replyEmbeds(error().setDescription(String.format("Username can not be equal to `%s`.", username)).build()).queue();
            return;
        }

        if (accountRepository.findByUsername(username) != null) {
            event.replyEmbeds(error().setDescription("User with such name is already exists.").build()).queue();
            return;
        }

        account.setUsername(username);
        accountRepository.save(account);

        event.replyEmbeds(success().setDescription("Username has been changed.").build()).queue();
        userPanel(event, account);
    }

    private void redeemSubscriptionButton(ButtonInteractionEvent event) {
        TextInput textInput = TextInput.create("user_redeem_subscription_input", "Code Input", TextInputStyle.SHORT) //
                .setRequiredRange(17, 17) //
                .setPlaceholder("XXXXX-XXXXX-XXXXX") //
                .build();

        Modal modal = Modal.create("user_redeem_subscription_modal", "Redeem Subscription Form") //
                .addActionRow(textInput) //
                .build();

        event.replyModal(modal).queue();
    }

    private void redeemSubscriptionModal(ModalInteractionEvent event, Account account) {
        String code = event.getValue("user_redeem_subscription_input").getAsString();
        Subscription subscription = subscriptionRepository.findByCode(code);

        if (subscription == null) {
            event.replyEmbeds(error().setDescription("Invalid subscription code").build()).queue();
            return;
        }

        if (subscription.getAccountId() != null) {
            event.replyEmbeds(error().setDescription("Subscription code has already been used").build()).queue();
            return;
        }

        if (account.getSubscription() != null && !account.getSubscription().hasExpired()) {
            event.replyEmbeds(warning().setDescription("You already have an active subscription.").build()).queue();
            return;
        }

        String token;

        try {
            token = EncryptionUtils.encrypt(String.format("%s:%s", account.getDiscordId(), code));
        } catch (Exception exception) {
            event.replyEmbeds(ReportUtils.reportEmbed(exception)).queue();
            return;
        }

        String link = String.format("https://spezz.space/redeem/subscription?token=%s", token);

        MessageEmbed embed = info() //
                .setTitle("Redeem Subscription") //
                .setDescription(String.format("To activate subscription, follow this link %s and pass verification.", link)) //
                .build();

        event.replyEmbeds(embed).queue();
    }

    private void dashboardPanelButton(GenericEvent event, Account account) {
        int applicationsLimit = account.getAppLimit();
        String description;

        if (applicationsLimit == 0) {
            description = "You do not have slots for creating applications, you can purchase them on spezz website.";
        } else {
            description = String.format("You have %s slots left to create applications.", applicationsLimit);
        }

        EmbedBuilder embed = info() //
                .setTitle("Dashboard Panel") //
                .setDescription(description) //
                .setFooter(spezzFooter()[0], spezzFooter()[1]);

        List<Application> appList = applicationRepository.findAllByOwnerId(account.getId());

        for (Application application : appList) {
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append(String.format("* Version: %s", application.getVersion() == null ? "`unset`" : application.getVersion()));
            stringBuilder.append("\n");
            stringBuilder.append(String.format("* Creation Date: %s", StringUtils.timestamp(application.getCreationDate())));
            stringBuilder.append("\n");
            stringBuilder.append(String.format("* Status: %s", application.isEnabled() ? "Active" : "Disabled"));
            stringBuilder.append("\n");

            embed.addField(application.getName(), stringBuilder.toString(), false);
        }

        Callback callback = Callback.builder(event).addEmbeds(embed.build());

        callback.addActionRow( //
                AdvancedButton.success().id("user_new_app_button").label("New Application").emoji(EmojiEnum.ADD).disabled(applicationsLimit == 0).build(), //
                AdvancedButton.primary().id("user_manage_app_button").label("Manage Application").emoji(EmojiEnum.APP_GEAR).disabled(appList.isEmpty()).build() //
        );

        callback.addActionRow( //
                AdvancedButton.back().id("user_back_to_user_panel_button").build() //
        );

        callback.queue();
    }

    private void newApplicationButton(ButtonInteractionEvent event) {
        TextInput nameInput = TextInput.create("user_new_app_name_input", "Name Input", TextInputStyle.SHORT) //
                .setRequiredRange(3, 20) //
                .setPlaceholder("Name") //
                .build();
        TextInput versionInput = TextInput.create("user_new_app_version_input", "Version Input", TextInputStyle.SHORT) //
                .setRequiredRange(1, 20) //
                .setPlaceholder("Version") //
                .setRequired(false) //
                .build();
        TextInput logoInpit = TextInput.create("user_new_app_logo_input", "Logo Url Input", TextInputStyle.SHORT) //
                .setPlaceholder("Logo Url") //
                .setRequired(false) //
                .build();
        TextInput websiteInput = TextInput.create("user_new_app_website_input", "Website Url Input", TextInputStyle.SHORT) //
                .setPlaceholder("Website Url") //
                .setRequired(false) //
                .build();

        Modal modal = Modal.create("user_new_app_modal", "New Application Form") //
                .addActionRow(nameInput) //
                .addActionRow(versionInput) //
                .addActionRow(logoInpit) //
                .addActionRow(websiteInput) //
                .build();

        event.replyModal(modal).queue();
    }

    private void newApplicationModal(ModalInteractionEvent event, Account account) {
        String name = event.getValue("user_new_app_name_input").getAsString();
        String version = event.getValue("user_new_app_version_input").getAsString();
        String logoUrl = event.getValue("user_new_app_logo_input").getAsString();
        String websiteUrl = event.getValue("user_new_app_website_input").getAsString();

        if (StringUtils.startWithNumber(name)) {
            event.replyEmbeds(error().setDescription("Application name can not start with number.").build()).queue();
            return;
        }

        if (StringUtils.containsSpecialSymbols(name)) {
            event.replyEmbeds(error().setDescription("Application name can not contain special symbols.").build()).queue();
            return;
        }

        if (name.equalsIgnoreCase("null")) {
            event.replyEmbeds(error().setDescription(String.format("Application name can not be equal to %s.", name)).build()).queue();
            return;
        }

        if (applicationRepository.findByName(name) != null) {
            event.replyEmbeds(error().setDescription("Application with such name is already exists.").build()).queue();
            return;
        }

        if (!logoUrl.isEmpty() && StringUtils.invalidImageUrl(logoUrl)) {
            event.replyEmbeds(error().setDescription("Invalid logo url.").build()).queue();
            return;
        }

        if (!websiteUrl.isEmpty() && StringUtils.invalidUrl(websiteUrl)) {
            event.replyEmbeds(error().setDescription("Invalid website url.").build()).queue();
            return;
        }

        version = version.isEmpty() ? null : version;
        logoUrl = logoUrl.isEmpty() ? null : logoUrl;
        websiteUrl = websiteUrl.isEmpty() ? null : websiteUrl;

        Application application = Application.builder().name(name).version(version).logoUrl(logoUrl).websiteUrl(websiteUrl).ownerId(account.getId()).build();
        applicationRepository.save(application);

        account.decrementAppLimit(1);
        accountRepository.save(account);

        License license = License.builder().usageDate(System.currentTimeMillis()).expirationDate(-1).accountId(account.getId()).applicationId(application.getId()).build();
        licenseRepository.save(license);

        AppProfile appProfile = AppProfile.builder().accountId(account.getId()).applicationId(application.getId()).licenseId(license.getId()).role(AppProfile.Role.OWNER).build();
        appProfileRepository.save(appProfile);

        event.replyEmbeds(success().setDescription(String.format("**%s** application has been created.", name)).build()).queue();
        applicationPanel(event, application.getId().toString());
    }

    private void manageApplicationButton(ButtonInteractionEvent event, Account account) {
        StringSelectMenu.Builder selectMenu = StringSelectMenu //
                .create("user_manage_app_menu") //
                .setPlaceholder("Choose application to manage");

        for (Application application : applicationRepository.findAllByOwnerId(account.getId())) {
            String applicationId = application.getId().toString();
            selectMenu.addOption(application.getName(), applicationId, applicationId);
        }

        event.deferReply().addActionRow(selectMenu.build()).queue();
    }

    private void applicationPanel(GenericEvent event, String applicationId) {
        Application application = applicationRepository.findById(UUID.fromString(applicationId)).orElse(null);

        if (application == null) {
            Callback.builder(event).addEmbeds(error().setDescription("Application not found.").build()).queue();
            return;
        }

        MessageEmbed embed = info() //
                .setTitle("Application Panel") //
                .setAuthor(application.getName(), application.getWebsiteUrl(), application.getLogoUrl()) //
                .addField("Version", application.getVersion() == null ? "`unset`" : application.getVersion(), true) //
                .addField("Creation Date", StringUtils.timestamp(application.getCreationDate()), true) //
                .addBlankField(true) //
                .addField("Hardware Reset Period", application.getHardwareIdResetPeriod() == -1 ? "No Limit" : StringUtils.durationString(application.getHardwareIdResetPeriod()), false) //
                .addField("Status", application.isEnabled() ? "Active" : "Disabled", false) //
                .addField("UUID", application.getId().toString(), false) //
                .setThumbnail(application.getLogoUrl()) //
                .setFooter(spezzFooter()[0], spezzFooter()[1]) //
                .build();

        Callback callback = Callback.builder(event).addEmbeds(embed);

        callback.addActionRow( //
                AdvancedButton.primary() //
                        .id("user_edit_app_data_button") //
                        .label("Edit Application Data") //
                        .emoji(EmojiEnum.EDIT) //
                        .values(applicationId) //
                        .build(), //
                AdvancedButton.primary() //
                        .id("user_set_hardware_reset_period_button") //
                        .label("Set Hardware Reset Period") //
                        .emoji(EmojiEnum.HARDWARE_PERIOD) //
                        .values(applicationId) //
                        .build(), //
                AdvancedButton.builder() //
                        .style(application.isEnabled() ? ButtonStyle.DANGER : ButtonStyle.SUCCESS) //
                        .id("user_toggle_app_status_button") //
                        .label(application.isEnabled() ? "Disable" : "Enable") //
                        .emoji(application.isEnabled() ? EmojiEnum.TOGGLE_LEFT : EmojiEnum.TOGGLE_RIGHT) //
                        .values(applicationId) //
                        .build() //
        );

        callback.addActionRow( //
                AdvancedButton.secondary().id("user_app_keys_management_button").label("Keys Management").emoji(EmojiEnum.KEYS_MANAGEMENT).values(applicationId).build(), //
                AdvancedButton.secondary().id("user_app_users_management_button").label("Users Management").emoji(EmojiEnum.USERS_MANAGEMENT).values(applicationId).build(), //
                AdvancedButton.secondary().id("user_app_sales_statistic_button").label("Sales Statistic").emoji(EmojiEnum.STATISTIC).values(applicationId).build() //
        );

        callback.addActionRow( //
                AdvancedButton.secondary().id("user_setup_backup_server_button").label("Setup Backup Server").emoji(EmojiEnum.DISCORD).values(applicationId).build(), //
                AdvancedButton.success().id("user_developer_api_button").label("Developer API").emoji(EmojiEnum.CODE).values(applicationId).build() //
        );

        callback.addActionRow( //
                AdvancedButton.back().id("user_dashboard_button").build() //
        );

        callback.queue();
    }

    private void editApplicationDataButton(ButtonInteractionEvent event, String applicationId) {
        TextInput nameInput = TextInput.create("user_edit_app_data_name_input", "Name Input", TextInputStyle.SHORT) //
                .setRequiredRange(3, 20) //
                .setPlaceholder("Name") //
                .setRequired(false) //
                .build();
        TextInput versionInput = TextInput.create("user_edit_app_data_version_input", "Version Input", TextInputStyle.SHORT) //
                .setRequiredRange(1, 20) //
                .setPlaceholder("Version") //
                .setRequired(false) //
                .build();
        TextInput logoInpit = TextInput.create("user_edit_app_data_logo_input", "Logo Url Input", TextInputStyle.SHORT) //
                .setPlaceholder("Logo Url") //
                .setRequired(false) //
                .build();
        TextInput websiteInput = TextInput.create("user_edit_app_data_website_input", "Website Url Input", TextInputStyle.SHORT) //
                .setPlaceholder("Website Url") //
                .setRequired(false) //
                .build();

        Modal modal = ValueModal.create("user_edit_app_data_modal", "Edit Application Data Form", applicationId) //
                .addActionRow(nameInput) //
                .addActionRow(versionInput) //
                .addActionRow(logoInpit) //
                .addActionRow(websiteInput) //
                .build();

        event.replyModal(modal).queue();
    }

    private void editApplicationDataModal(ModalInteractionEvent event, String applicationId) {
        if (event.getValues().stream().allMatch(modalMapping -> modalMapping.getAsString().isEmpty())) {
            event.replyEmbeds(info().setDescription("Nothing was changed.").build()).queue();
            return;
        }

        String name = event.getValue("user_edit_app_data_name_input").getAsString();
        String version = event.getValue("user_edit_app_data_version_input").getAsString();
        String logoUrl = event.getValue("user_edit_app_data_logo_input").getAsString();
        String websiteUrl = event.getValue("user_edit_app_data_website_input").getAsString();

        if (!name.isEmpty() && StringUtils.startWithNumber(name)) {
            event.replyEmbeds(error().setDescription("Application name can not start with number.").build()).queue();
            return;
        }

        if (!name.isEmpty() && StringUtils.containsSpecialSymbols(name)) {
            event.replyEmbeds(error().setDescription("Application name can not contain special symbols.").build()).queue();
            return;
        }

        if (!name.isEmpty() && applicationRepository.findByName(name) != null) {
            event.replyEmbeds(error().setDescription("Application with such name is already exists.").build()).queue();
            return;
        }

        if (name.equalsIgnoreCase("null")) {
            event.replyEmbeds(error().setDescription(String.format("Application name can not be equal to %s.", name)).build()).queue();
            return;
        }

        if (!logoUrl.isEmpty() && StringUtils.invalidUrl(logoUrl)) {
            event.replyEmbeds(error().setDescription("Invalid logo url.").build()).queue();
            return;
        }

        if (!websiteUrl.isEmpty() && StringUtils.invalidUrl(websiteUrl)) {
            event.replyEmbeds(error().setDescription("Invalid website url.").build()).queue();
            return;
        }

        Application application = applicationRepository.findById(UUID.fromString(applicationId)).get();

        if (!name.isEmpty()) {
            application.setName(name);
        }

        if (!version.isEmpty()) {
            application.setVersion(version);
        }

        if (!logoUrl.isEmpty()) {
            application.setLogoUrl(logoUrl);
        }

        if (!websiteUrl.isEmpty()) {
            application.setWebsiteUrl(websiteUrl);
        }

        applicationRepository.save(application);

        event.replyEmbeds(info().setDescription("Application changes are saved.").build()).queue();
        applicationPanel(event, applicationId);
    }

    private void setHardwareIdResetPeriodButton(ButtonInteractionEvent event, String applicationId) {
        StringSelectMenu selectMenu = ValueSelectMenu.create("user_set_hardware_id_reset_period_menu", applicationId) //
                .setPlaceholder("Select a time period") //
                .addOption("No Limit", "0") //
                .addOption("Day", String.valueOf(TimeUnit.DAYS.toMillis(1))) //
                .addOption("Week", String.valueOf(TimeUnit.DAYS.toMillis(7))) //
                .addOption("Month", String.valueOf(TimeUnit.DAYS.toMillis(30))) //
                .addOption("Three Months", String.valueOf(TimeUnit.DAYS.toMillis(90))) //
                .addOption("Half a Year", String.valueOf(TimeUnit.DAYS.toMillis(180))) //
                .addOption("Year", String.valueOf(TimeUnit.DAYS.toMillis(365))) //
                .build();

        event.deferReply().addActionRow(selectMenu).queue();
    }

    private void setHardwareIdResetPeriodMenu(StringSelectInteractionEvent event, String applicationId) {
        Application application = applicationRepository.findById(UUID.fromString(applicationId)).get();

        application.setHardwareIdResetPeriod(Long.parseLong(event.getValues().get(0)));
        applicationRepository.save(application);

        event.replyEmbeds(info().setDescription("Application changes are saved.").build()).queue();
        applicationPanel(event, applicationId);
    }

    private void toggleApplicationButton(ButtonInteractionEvent event, String applicationId) {
        Application application = applicationRepository.findById(UUID.fromString(applicationId)).get();

        application.setEnabled(!application.isEnabled());
        applicationRepository.save(application);

        event.replyEmbeds(info().setDescription("Application changes are saved.").build()).queue();
        applicationPanel(event, applicationId);
    }

    private void keysManagementPanel(ButtonInteractionEvent event, String applicationId) {
        Application application = applicationRepository.findById(UUID.fromString(applicationId)).get();

        List<License> licenseList = licenseRepository.findAllByApplicationId(application.getId());
        List<License> unusedList = licenseList.stream().filter(license -> license.getAccountId() == null).toList();
        List<License> redeemedList = licenseList.stream().filter(license -> license.getAccountId() != null).toList();

        MessageEmbed embed = info() //
                .setTitle("Keys Management") //
                .setAuthor(application.getName(), application.getWebsiteUrl(), application.getLogoUrl()) //
                .addField("Total", String.valueOf(licenseList.size()), false) //
                .addField("Unused", String.valueOf(unusedList.size()), false) //
                .addField("Redeemed", String.valueOf(redeemedList.size()), false) //
                .setFooter(spezzFooter()[0], spezzFooter()[1]) //
                .build();

        Callback callback = Callback.builder(event).addEmbeds(embed);

        callback.addActionRow( //
                AdvancedButton.success().id("user_generate_app_keys_button").label("Generate Keys").values(applicationId).build(), //
                AdvancedButton.primary().id("user_show_app_keys_button").label("Show Keys").values(applicationId).disabled(licenseList.isEmpty()).build(), //
                AdvancedButton.primary().id("user_show_specific_key_button").label("Show Specific Key").values(applicationId).disabled(licenseList.isEmpty()).build(), //
                AdvancedButton.danger().id("user_delete_unused_app_keys_button").label("Delete Unused Keys").values(applicationId).disabled(unusedList.isEmpty()).build() //
        );

        callback.addActionRow( //
                AdvancedButton.back().id("user_back_to_app_panel_button").values(applicationId).build() //
        );

        callback.queue();
    }

    private void generateKeysButton(ButtonInteractionEvent event, String applicationId) {
        StringSelectMenu selectMenu = ValueSelectMenu //
                .create("user_select_keys_expiry_date_menu", applicationId) //
                .setPlaceholder("Select keys expiration date") //
                .addOption("Day", String.valueOf(TimeUnit.DAYS.toMillis(1))) //
                .addOption("Week", String.valueOf(TimeUnit.DAYS.toMillis(7))) //
                .addOption("Month", String.valueOf(TimeUnit.DAYS.toMillis(30))) //
                .addOption("Three Months", String.valueOf(TimeUnit.DAYS.toMillis(90))) //
                .addOption("Half a Year", String.valueOf(TimeUnit.DAYS.toMillis(180))) //
                .addOption("Year", String.valueOf(TimeUnit.DAYS.toMillis(365))) //
                .addOption("Lifetime", "-1") //
                .build();

        event.deferReply().addActionRow(selectMenu).queue();
    }

    private void selectKeysQuantityMenu(StringSelectInteractionEvent event, String applicationId) {
        StringSelectMenu selectMenu = ValueSelectMenu //
                .create("user_select_keys_qunatity_menu", applicationId, event.getValues().get(0)) //
                .setPlaceholder("Select quantity of keys to generate") //
                .addOption("1", "1") //
                .addOption("5", "5") //
                .addOption("10", "10") //
                .addOption("50", "50") //
                .addOption("100", "100") //
                .build();

        event.deferReply().addActionRow(selectMenu).queue();
    }

    private void generateKeysMenu(StringSelectInteractionEvent event, String applicationId, String expiryDateValue) {
        Application application = applicationRepository.findById(UUID.fromString(applicationId)).get();

        int quantity = Integer.parseInt(event.getValues().get(0));
        List<License> licenseList = new ArrayList<>();

        for (int i = 0; i < quantity; i++) {
            licenseList.add(License.builder().expirationDate(Long.parseLong(expiryDateValue)).applicationId(application.getId()).build());
        }

        StringBuilder description = new StringBuilder();

        for (License license : licenseList) {
            description.append(String.format("`%s`", license.getCode()));
            description.append("\n");
        }

        FileRequest fileRequest = FileRequest.builder() //
                .fileName("generated_keys.txt") //
                .content(description.toString().replace("`", "")) //
                .accountId(application.getOwnerId()) //
                .build();

        fileRequestRepository.save(fileRequest);
        licenseRepository.saveAll(licenseList);

        MessageEmbed embed = info() //
                .setTitle("Generated Keys") //
                .setDescription(description) //
                .setAuthor(application.getName(), application.getWebsiteUrl(), application.getLogoUrl()) //
                .build();

        ReplyCallbackAction callback = event.replyEmbeds(embed);

        callback.addActionRow(AdvancedButton.primary().id("user_download_file_button").label("Download").values(fileRequest.getId().toString()).build());
        callback.addActionRow(AdvancedButton.back().id("user_app_keys_management_button").values(applicationId).build());

        callback.queue();
    }

    private void downloadFileButton(ButtonInteractionEvent event, String fileRequestId) {
        FileRequest fileRequest = fileRequestRepository.findById(UUID.fromString(fileRequestId)).orElse(null);

        if (fileRequest == null) {
            event.replyEmbeds(warning().setDescription("File has expired.").build());
            return;
        }

        FileUpload fileUpload = FileUpload.fromData(fileRequest.getContent().getBytes(), fileRequest.getFileName());

        fileRequestRepository.findAll().stream().filter(FileRequest::hasExpired).forEach(fileRequestRepository::delete);
        event.replyFiles(fileUpload).queue();
    }

    private void showKeysButton(ButtonInteractionEvent event, String applicationId) {
        Application application = applicationRepository.findById(UUID.fromString(applicationId)).get();
        List<License> licenseList = licenseRepository.findAllByApplicationId(application.getId());

        MessageEmbed embed = info() //
                .setTitle("Show Keys") //
                .setDescription("Please select key type.") //
                .setAuthor(application.getName(), application.getWebsiteUrl(), application.getLogoUrl()) //
                .setFooter(spezzFooter()[0], spezzFooter()[1]) //
                .build();

        ReplyCallbackAction callback = event.replyEmbeds(embed);

        callback.addActionRow( //
                AdvancedButton.primary() //
                        .id("user_show_specific_app_keys_button") //
                        .label("All") //
                        .values(applicationId, "All", "1") //
                        .disabled(licenseList.isEmpty()) //
                        .build(), //
                AdvancedButton.primary() //
                        .id("user_show_specific_app_keys_button") //
                        .label("Unused") //
                        .values(applicationId, "Unused", "1") //
                        .disabled(licenseList.stream().noneMatch(license -> license.getAccountId() == null)) //
                        .build(), //
                AdvancedButton.primary() //
                        .id("user_show_specific_app_keys_button") //
                        .label("Redeemed") //
                        .values(applicationId, "Redeemed", "1") //
                        .disabled(licenseList.stream().noneMatch(license -> license.getAccountId() != null)) //
                        .build() //
        );

        callback.addActionRow( //
                AdvancedButton.back().id("user_app_keys_management_button").values(applicationId).build() //
        );

        callback.queue();
    }

    private void showSpecificKeysButton(ButtonInteractionEvent event, List<String> values) {
        String applicationId = values.get(0);
        String typeValue = values.get(1);
        String pageValue = values.get(2);

        Application application = applicationRepository.findById(UUID.fromString(applicationId)).get();
        Stream<License> licenseStream = licenseRepository.findAllByApplicationId(application.getId()).stream();

        Comparator<License> usageDateComparator = Comparator.comparingLong(License::getUsageDate);
        Comparator<License> expiryDateComparator = Comparator.comparingLong(License::getExpirationDate);

        Predicate<License> userIsNullFilter = license -> license.getAccountId() == null;
        Predicate<License> userNotNullFilter = license -> license.getAccountId() != null;

        switch (typeValue) {
            case "All" -> licenseStream = licenseStream.sorted(usageDateComparator).sorted(expiryDateComparator);

            case "Unused" -> licenseStream = licenseStream.filter(userIsNullFilter).sorted(expiryDateComparator);

            case "Redeemed" ->
                    licenseStream = licenseStream.filter(userNotNullFilter).sorted(usageDateComparator).sorted(expiryDateComparator);
        }

        StringBuilder code = new StringBuilder();
        StringBuilder duration = new StringBuilder();
        StringBuilder expiryDate = new StringBuilder();

        List<License> licenseList = licenseStream.toList();

        int page = Integer.parseInt(pageValue);
        int step = 25;

        int fromIndex = step * (page - 1);
        int toIndex = Math.min(step + fromIndex, licenseList.size());

        for (License license : licenseList.subList(fromIndex, toIndex)) {
            code.append(String.format("`%s`", license.getCode()));
            code.append("\n");

            duration.append(String.format("`%s`", StringUtils.durationString(license.getExpirationDate())));
            duration.append("\n");

            expiryDate.append(String.format("`%s`", license.getAccountId() == null ? "Unused" : StringUtils.timestamp(license.getUsageDate(), license.getExpirationDate())));
            expiryDate.append("\n");
        }

        StringBuilder keysContent = new StringBuilder();
        StringBuilder keysDetailedContent = new StringBuilder();

        for (int i = 0; i < licenseList.size(); i++) {
            License license = licenseList.get(i);

            keysContent.append(license.getCode());
            keysContent.append("\n");

            String durationString = StringUtils.durationString(license.getExpirationDate());
            String timestamp = license.getAccountId() == null ? "Unused" : StringUtils.timestamp(license.getUsageDate(), license.getExpirationDate());

            keysDetailedContent.append(String.format("%s. %s - %s - %s", i + 1, license.getCode(), durationString, timestamp));
            keysDetailedContent.append("\n");
        }

        FileRequest keysFile = FileRequest.builder().content(keysContent.toString()).fileName("keys.txt").accountId(application.getOwnerId()).build();
        FileRequest keysDetailedFile = FileRequest.builder().content(keysDetailedContent.toString()).fileName("keys_detailed.txt").accountId(application.getOwnerId()).build();

        fileRequestRepository.save(keysFile);
        fileRequestRepository.save(keysDetailedFile);

        MessageEmbed embed = info() //
                .setTitle(String.format("Key List - %s%nPage %s/%s", typeValue, pageValue, (int) Math.ceil((double) licenseList.size() / step))) //
                .setAuthor(application.getName(), application.getWebsiteUrl(), application.getLogoUrl()) //
                .addField("Code", code.toString(), true) //
                .addField("Duration", duration.toString(), true) //
                .addField("Expiration Date", expiryDate.toString(), true) //
                .setFooter(spezzFooter()[0], spezzFooter()[1]) //
                .build();

        ReplyCallbackAction callback = event.replyEmbeds(embed);

        callback.addActionRow( //
                AdvancedButton.primary().id("user_download_keys_button") //
                        .label("Download") //
                        .values(keysFile.getId().toString(), keysDetailedFile.getId().toString()) //
                        .build(), //
                AdvancedButton.primary() //
                        .id("user_show_specific_app_keys_button") //
                        .label("Prev Page") //
                        .values(applicationId, typeValue, String.valueOf(page - 1)).disabled(page == 1) //
                        .build(), //
                AdvancedButton.primary() //
                        .id("user_show_specific_app_keys_button") //
                        .label("Next Page") //
                        .values(applicationId, typeValue, String.valueOf(page + 1)) //
                        .disabled(page * step > licenseList.size()) //
                        .build() //
        );

        callback.addActionRow( //
                AdvancedButton.back().id("user_show_app_keys_button").values(applicationId).build() //
        );

        callback.queue();
    }

    private void downloadKeysButton(ButtonInteractionEvent event, String keysId, String detailedKeysId) {
        MessageEmbed embed = info().setTitle("Download Keys").setDescription("Please select info type.").build();
        ReplyCallbackAction callback = event.replyEmbeds(embed);

        callback.addActionRow( //
                AdvancedButton.primary().id("user_download_file_button").label("Code Only").values(keysId).build(), //
                AdvancedButton.primary().id("user_download_file_button").label("Detailed").values(detailedKeysId).build() //
        );

        callback.queue();
    }

    private void showSpecificKeyButton(ButtonInteractionEvent event, String applicationId) {
        TextInput textInput = TextInput.create("user_show_specific_key_input", "Code Input", TextInputStyle.SHORT) //
                .setPlaceholder("XXXXX-XXXXX-XXXXX") //
                .setRequiredRange(17, 17) //
                .build();

        Modal modal = ValueModal.create("user_show_specific_key_modal", "Show Specific Key Form", applicationId) //
                .addActionRow(textInput) //
                .build();

        event.replyModal(modal).queue();
    }

    private void showSpecificKeyModal(ModalInteractionEvent event, String applicationId) {
        Application application = applicationRepository.findById(UUID.fromString(applicationId)).get();

        String licenseCode = event.getValue("user_show_specific_key_input").getAsString();
        License license = licenseRepository.findByCode(licenseCode);

        if (license == null || !license.getApplicationId().equals(application.getId())) {
            event.replyEmbeds(error().setDescription("Key not found.").build());
            return;
        }

        AppProfile appProfile = appProfileRepository.findByLicenseId(license.getId());
        StringBuilder holderInfo = new StringBuilder();

        if (license.getAccountId() == null) {
            holderInfo.append("`Unused`");
        } else {
            holderInfo.append(String.format("* **User Id:** %04d", appProfile.getUid()));
            holderInfo.append("\n");
            holderInfo.append(String.format("* **Profile Name:** %s", appProfile.getProfileName()));
            holderInfo.append("\n");
            holderInfo.append(String.format("* **Since:** %s", StringUtils.timestamp(license.getUsageDate())));
            holderInfo.append("\n");
            holderInfo.append(String.format("* **Expiry:** %s", StringUtils.timestamp(license.getUsageDate(), license.getExpirationDate())));
            holderInfo.append("\n");
            holderInfo.append(String.format("* **Ip Address:** %s", license.getIpAddress() == null ? "`unknown`" : String.format("||%s||", license.getIpAddress())));
            holderInfo.append("\n");
        }

        MessageEmbed embed = info() //
                .setTitle("Detailed Key Info") //
                .setAuthor(application.getName(), application.getWebsiteUrl(), application.getLogoUrl()) //
                .addField("Code", licenseCode, true) //
                .addField("Issue Date", StringUtils.timestamp(license.getIssueDate()), true) //
                .addField("Duration", StringUtils.durationString(license.getExpirationDate()), true) //
                .addField("Holder", holderInfo.toString(), false) //
                .setFooter(spezzFooter()[0], spezzFooter()[1]) //
                .build();

        event.replyEmbeds(embed) //
                .addActionRow(AdvancedButton.primary().id("user_show_app_profile_button").label("Holder Info").values(appProfile.getId().toString()).build()) //
                .addActionRow(AdvancedButton.back().id("user_app_keys_management_button").values(applicationId).build()) //
                .queue();
    }

    private void deleteUnusedKeysButton(ButtonInteractionEvent event, String applicationId) {
        TextInput textInput = TextInput.create("user_delete_unused_app_keys_input", "Keys Input", TextInputStyle.PARAGRAPH) //
                .setPlaceholder("XXXXX-XXXXX-XXXXX\nXXXXX-XXXXX-XXXXX\nXXXXX-XXXXX-XXXXX...") //
                .setRequiredRange(17, 3600) //
                .build();

        Modal modal = ValueModal.create("user_delete_unused_app_keys_modal", "Delete Unused Keys Form", applicationId) //
                .addActionRow(textInput) //
                .build();

        event.replyModal(modal).queue();
    }

    private void deleteUnusedKeysModal(ModalInteractionEvent event, String applicationId) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String code : event.getValue("user_delete_unused_app_keys_input").getAsString().split("\n")) {
            License license = licenseRepository.findByCode(code);

            if (license.getApplicationId().equals(UUID.fromString(applicationId)) && license.getAccountId() == null) {
                stringBuilder.append(String.format("`%s`", license.getCode()));
                stringBuilder.append("\n");

                licenseRepository.delete(license);
            }
        }

        MessageEmbed embed = info().setTitle("Deleted Keys").setDescription(stringBuilder.toString()).build();
        ReplyCallbackAction callback = event.replyEmbeds(embed);

        callback.addActionRow(AdvancedButton.back().id("user_app_keys_management_button").values(applicationId).build());
        callback.queue();
    }

    private void salesStatisticButton(ButtonInteractionEvent event, String applicationId) {
        StringSelectMenu selectMenu = ValueSelectMenu.create("user_select_statistic_period_menu", applicationId) //
                .setPlaceholder("Please select period") //
                .addOption("Last 24 hours", "last24") //
                .addOption("Last week", "last7") //
                .addOption("Last month", "last30") //
                .addOption("Last six months", "last180") //
                .addOption("Last year", "last365") //
                .addOption("All time", "all") //
                .build();

        event.deferReply().addActionRow(selectMenu).queue();
    }

    private void salesStatisticMenu(StringSelectInteractionEvent event, String applicationId) {
        List<License> licenseList = licenseRepository.findAllByApplicationIdAndAccountIdNotNull(UUID.fromString(applicationId));
        String format = event.getValues().get(0);

        FileUpload fileUpload;

        try {
            fileUpload = ChartUtils.getStatisticChartFile(licenseList, format);
        } catch (IOException exception) {
            event.replyEmbeds(ReportUtils.reportEmbed(exception)).queue();
            return;
        }

        StringSelectMenu selectMenu = ValueSelectMenu.create("user_select_statistic_period_menu", applicationId) //
                .setPlaceholder("Please select period") //
                .addOption("Last 24 hours", "last24") //
                .addOption("Last week", "last7") //
                .addOption("Last month", "last30") //
                .addOption("Last six months", "last180") //
                .addOption("Last year", "last365") //
                .addOption("All time", "all") //
                .build();

        event.replyFiles(fileUpload) //
                .addActionRow(selectMenu) //
                .addActionRow(AdvancedButton.back().id("user_back_to_app_panel_button").values(applicationId).build()) //
                .queue();
    }

    private void usersManagementButton(ButtonInteractionEvent event, String applicationId) {
        Application application = applicationRepository.findById(UUID.fromString(applicationId)).get();

        List<AppProfile> allList = appProfileRepository.findAllByApplicationId(application.getId());
        List<AppProfile> bannedList = appProfileRepository.findAllByApplicationIdAndRole(application.getId(), AppProfile.Role.BANNED);
        List<AppProfile> adminList = appProfileRepository.findAllByApplicationIdAndRole(application.getId(), AppProfile.Role.ADMIN);

        MessageEmbed embed = info() //
                .setTitle("Users Management") //
                .setAuthor(application.getName(), application.getWebsiteUrl(), application.getLogoUrl()) //
                .addField("Total", String.valueOf(allList.size()), false) //
                .addField("Admins", String.valueOf(adminList.size()), false) //
                .addField("Banned", String.valueOf(bannedList.size()), false) //
                .setFooter(spezzFooter()[0], spezzFooter()[1]) //
                .build();

        Callback callback = Callback.builder(event).addEmbeds(embed);

        callback.addActionRow( //
                AdvancedButton.primary().id("user_show_app_users_button").label("Show Users").values(applicationId).disabled(allList.isEmpty()).build(), //
                AdvancedButton.primary().id("user_show_specific_app_user_button").label("Show Specific User").values(applicationId).disabled(allList.isEmpty()).build() //
        );

        callback.addActionRow( //
                AdvancedButton.back().id("user_back_to_app_panel_button").values(applicationId).build() //
        );

        callback.queue();
    }

    private void showUsersButton(ButtonInteractionEvent event, String applicationId) {
        Application application = applicationRepository.findById(UUID.fromString(applicationId)).get();

        MessageEmbed embed = info() //
                .setTitle("Show Users") //
                .setDescription("Please select status filter.") //
                .setAuthor(application.getName(), application.getWebsiteUrl(), application.getLogoUrl()) //
                .setFooter(spezzFooter()[0], spezzFooter()[1]) //
                .build();

        ReplyCallbackAction callback = event.replyEmbeds(embed);

        List<AppProfile> adminList = appProfileRepository.findAllByApplicationIdAndRole(application.getId(), AppProfile.Role.ADMIN);
        List<AppProfile> bannedList = appProfileRepository.findAllByApplicationIdAndRole(application.getId(), AppProfile.Role.BANNED);

        callback.addActionRow( //
                AdvancedButton.primary().id("user_show_specific_users_button").label("All").values(applicationId, "All", "1").build(), //
                AdvancedButton.primary().id("user_show_specific_users_button").label("Admin").values(applicationId, "Admin", "1").disabled(adminList.isEmpty()).build(), //
                AdvancedButton.primary().id("user_show_specific_users_button").label("Banned").values(applicationId, "Banned", "1").disabled(bannedList.isEmpty()).build() //
        );

        callback.addActionRow( //
                AdvancedButton.back().id("user_app_users_management_button").values(applicationId).build() //
        );

        callback.queue();
    }

    private void showSpecificUsersButton(ButtonInteractionEvent event, List<String> values) {
        String applicationId = values.get(0);
        String filerValue = values.get(1);
        String pageValue = values.get(1);

        Application application = applicationRepository.findById(UUID.fromString(applicationId)).get();
        List<AppProfile> appProfileList = null;

        switch (filerValue) {
            case "All" -> appProfileList = appProfileRepository.findAllByApplicationId(application.getId());

            case "Admin" ->
                    appProfileList = appProfileRepository.findAllByApplicationIdAndRole(application.getId(), AppProfile.Role.ADMIN);

            case "Banned" ->
                    appProfileList = appProfileRepository.findAllByApplicationIdAndRole(application.getId(), AppProfile.Role.BANNED);
        }

        appProfileList = appProfileList.stream().sorted(Comparator.comparingInt(AppProfile::getUid)).toList();

        StringBuilder userIdString = new StringBuilder();
        StringBuilder nameString = new StringBuilder();
        StringBuilder licenseString = new StringBuilder();
        StringBuilder fileContent = new StringBuilder();

        int page = Integer.parseInt(pageValue);
        int step = 25;

        int fromIndex = page * step - step;
        int toIndex = page * step;

        for (AppProfile appProfile : appProfileList.subList(fromIndex, Math.min(appProfileList.size(), toIndex))) {
            userIdString.append(String.format("`%04d`", appProfile.getUid()));
            userIdString.append("\n");

            nameString.append(String.format("`%s`", appProfile.getProfileName()));
            nameString.append("\n");

            licenseString.append(String.format("`%s`", appProfile.getLicense().getCode()));
            licenseString.append("\n");
        }

        for (AppProfile appProfile : appProfileList) {
            fileContent.append(String.format("%04d - %s - %s - %s", appProfile.getUid(), appProfile.getProfileName(), appProfile.getLicense().getCode(), appProfile.getHardwareId()));
            fileContent.append("\n");
        }

        FileRequest fileRequest = FileRequest.builder().accountId(application.getOwnerId()).content(fileContent.toString()).fileName("users.txt").build();
        fileRequestRepository.save(fileRequest);

        MessageEmbed embed = info() //
                .setTitle(String.format("User List - %s%nPage %s/%s", filerValue, page, (int) Math.ceil((double) appProfileList.size() / step))) //
                .setAuthor(application.getName(), application.getWebsiteUrl(), application.getLogoUrl()) //
                .addField("User Id", userIdString.toString(), true) //
                .addField("In App Name", nameString.toString(), true) //
                .addField("License", licenseString.toString(), true) //
                .setFooter(spezzFooter()[0], spezzFooter()[1]) //
                .build();

        ReplyCallbackAction callback = event.replyEmbeds(embed);

        callback.addActionRow( //
                AdvancedButton.primary() //
                        .id("user_download_file_button") //
                        .label("Download") //
                        .values(fileRequest.getId().toString()) //
                        .build(), //
                AdvancedButton.primary() //
                        .id("user_show_specific_users_button") //
                        .label("Prev Page") //
                        .values(applicationId, filerValue, String.valueOf(page - 1)) //
                        .disabled(page == 1) //
                        .build(), //
                AdvancedButton.primary() //
                        .id("user_show_specific_users_button") //
                        .label("Next Page") //
                        .values(applicationId, filerValue, String.valueOf(page + 1)) //
                        .disabled(page * step > appProfileList.size()) //
                        .build() //
        );

        callback.addActionRow( //
                AdvancedButton.back().id("user_show_app_users_button").values(applicationId).build() //
        );

        callback.queue();
    }

    private void showSpecificUserButton(ButtonInteractionEvent event, String applicationId) {
        TextInput textInput = TextInput.create("user_show_specific_app_user_input", "User Id Input", TextInputStyle.SHORT) //
                .setPlaceholder("User Id") //
                .setRequired(true) //
                .setMaxLength(6) //
                .build();

        Modal modal = ValueModal.create("user_show_specific_app_user_modal", "Show Specific User Form", applicationId) //
                .addActionRow(textInput) //
                .build();

        event.replyModal(modal).queue();
    }

    private void appProfilePanel(GenericEvent event, String appProfileId) {
        AppProfile appProfile = appProfileRepository.findById(UUID.fromString(appProfileId)).orElse(null);

        if (appProfile == null) {
            Callback.builder(event).addEmbeds(error().setDescription("User not found.").build()).queue();
            return;
        }

        Application application = applicationRepository.findById(appProfile.getApplicationId()).get();
        License license = licenseRepository.findById(appProfile.getLicenseId()).orElse(null);

        StringBuilder hardwareInfo = new StringBuilder();
        StringBuilder licenseInfo = new StringBuilder();

        hardwareInfo.append(String.format("* **Last Reset:** %s", appProfile.getLastHardwareIdReset() > 0 ? StringUtils.timestamp(appProfile.getLastHardwareIdReset()) : "`never`"));
        hardwareInfo.append("\n");
        hardwareInfo.append(String.format("* **Value:** %s", appProfile.getHardwareId() == null ? "`unset`" : appProfile.getHardwareId()));
        hardwareInfo.append("\n");

        licenseInfo.append(String.format("* **Seral:** %s", license.getCode()));
        licenseInfo.append("\n");
        licenseInfo.append(String.format("* **Usage Date:** %s", StringUtils.timestamp(license.getUsageDate())));
        licenseInfo.append("\n");
        licenseInfo.append(String.format("* **Expiration Date:** %s", StringUtils.timestamp(license.getUsageDate(), license.getExpirationDate())));
        licenseInfo.append("\n");

        MessageEmbed embed = info() //
                .setTitle("Detailed User Info") //
                .setAuthor(application.getName(), application.getWebsiteUrl(), application.getLogoUrl()) //
                .addField("User Id", String.format("%04d", appProfile.getUid()), true) //
                .addField("Profile Name", appProfile.getProfileName(), true) //
                .addField("Role", appProfile.getRole().getFuncyName(), true) //
                .addField("Hardware", hardwareInfo.toString(), false) //
                .addField("License", licenseInfo.toString(), false) //
                .setFooter(spezzFooter()[0], spezzFooter()[1]) //
                .build();

        Callback callback = Callback.builder(event).addEmbeds(embed);

        boolean owner = appProfile.getRole() == AppProfile.Role.OWNER;
        boolean banned = appProfile.getRole() == AppProfile.Role.BANNED;
        boolean admin = appProfile.getRole() == AppProfile.Role.ADMIN;

        callback.addActionRow( //
                AdvancedButton.builder() //
                        .style(admin ? ButtonStyle.DANGER : ButtonStyle.SUCCESS) //
                        .id("user_grant_app_user_admin_button") //
                        .label(String.format("%s Admin", admin ? "Discharge" : "Grant")) //
                        .values(appProfileId) //
                        .disabled(owner || banned) //
                        .build(), //
                AdvancedButton.builder() //
                        .style(banned ? ButtonStyle.SUCCESS : ButtonStyle.DANGER) //
                        .id("user_ban_app_user_button") //
                        .label(String.format("%s User", banned ? "Unban" : "Ban")) //
                        .values(appProfileId).disabled(owner) //
                        .build(), //
                AdvancedButton.danger() //
                        .id("user_delete_app_user_button") //
                        .label("Delete User") //
                        .values(appProfileId) //
                        .disabled(owner) //
                        .build() //
        );

        callback.addActionRow( //
                AdvancedButton.primary().id("user_reset_app_user_hardware_id_button").label("Reset Hardware Id").values(appProfileId).disabled(appProfile.getHardwareId() == null).build(), //
                AdvancedButton.primary().id("user_set_app_user_hardware_id_button").label("Set Hardware Id").values(appProfileId).build() //
        );

        callback.addActionRow( //
                AdvancedButton.back().id("user_app_users_management_button").values(application.getId().toString()).build() //
        );

        callback.queue();
    }

    private void showSpeicficUserModal(ModalInteractionEvent event, String applicationId) {
        String value = event.getValue("user_show_specific_app_user_input").getAsString();

        int userId = Integer.parseInt(value);
        UUID applicationUUID = UUID.fromString(applicationId);

        appProfilePanel(event, appProfileRepository.findByApplicationIdAndUid(applicationUUID, userId).getId().toString());
    }

    private void grantAdminButton(ButtonInteractionEvent event, String appProfileId) {
        AppProfile appProfile = appProfileRepository.findById(UUID.fromString(appProfileId)).get();
        String message;

        if (appProfile.getRole() == AppProfile.Role.ADMIN) {
            appProfile.setRole(AppProfile.Role.CUSTOMER);
            message = "was demoted";
        } else {
            appProfile.setRole(AppProfile.Role.ADMIN);
            message = "has been granted administrator rights";
        }

        appProfileRepository.save(appProfile);

        event.replyEmbeds(info().setDescription(String.format("User %s(%04d) %s.", appProfile.getProfileName(), appProfile.getUid(), message)).build()).queue();
        appProfilePanel(event, appProfileId);
    }

    private void banAppProfileButton(ButtonInteractionEvent event, String appProfileId) {
        AppProfile appProfile = appProfileRepository.findById(UUID.fromString(appProfileId)).get();
        String message;

        if (appProfile.getRole() == AppProfile.Role.BANNED) {
            appProfile.setRole(AppProfile.Role.CUSTOMER);
            message = "has been unbanned";
        } else {
            appProfile.setRole(AppProfile.Role.BANNED);
            message = "has been banned";
        }

        appProfileRepository.save(appProfile);

        event.replyEmbeds(info().setDescription(String.format("User %s(%04d) %s.", appProfile.getProfileName(), appProfile.getUid(), message)).build()).queue();
        appProfilePanel(event, appProfileId);
    }

    private void deleteAppProlileButton(ButtonInteractionEvent event, String appProfileId) {
        AppProfile appProfile = appProfileRepository.findById(UUID.fromString(appProfileId)).get();
        License license = licenseRepository.findById(appProfile.getLicenseId()).get();

        licenseRepository.delete(license);
        appProfileRepository.delete(appProfile);

        event.replyEmbeds(warning().setDescription(String.format("User %s(%04d) has been deleted!", appProfile.getProfileName(), appProfile.getUid())).build()).queue();
        usersManagementButton(event, appProfile.getApplicationId().toString());
    }

    private void resetUserHardwareIdButton(ButtonInteractionEvent event, String appProfileId) {
        AppProfile appProfile = appProfileRepository.findById(UUID.fromString(appProfileId)).get();

        appProfile.setLastHardwareIdReset(System.currentTimeMillis());
        appProfile.setHardwareId(null);
        appProfileRepository.save(appProfile);

        event.replyEmbeds(info().setDescription(String.format("Hardware id has been reset for user %s(%04d).", appProfile.getProfileName(), appProfile.getUid())).build()).queue();
        appProfilePanel(event, appProfileId);
    }

    private void setUserHardwareIdButton(ButtonInteractionEvent event, String appProfileId) {
        TextInput textInput = TextInput.create("user_set_app_user_hardware_id_input", "Hardware Id Input", TextInputStyle.PARAGRAPH) //
                .setPlaceholder("Hardware Id") //
                .setMaxLength(1000) //
                .build();

        Modal modal = ValueModal.create("user_set_app_user_hardware_id_modal", "Set Hardware Id Form", appProfileId) //
                .addActionRow(textInput) //
                .build();

        event.replyModal(modal).queue();
    }

    private void setUserHardwareIdModal(ModalInteractionEvent event, String appProfileId) {
        AppProfile appProfile = appProfileRepository.findById(UUID.fromString(appProfileId)).get();
        String hardwareId = event.getValue("user_set_app_user_hardware_id_input").getAsString();

        appProfile.setHardwareId(hardwareId);
        appProfileRepository.save(appProfile);

        event.replyEmbeds(info().setDescription(String.format("Hardware id has been set for user %s(%04d).", appProfile.getProfileName(), appProfile.getUid())).build()).queue();
        appProfilePanel(event, appProfileId);
    }

    private void libraryButton(ButtonInteractionEvent event, Account account) {
        List<AppProfile> appProfileList = appProfileRepository.findAllByAccountId(account.getId());
        EmbedBuilder embed = info().setTitle("Library").setFooter(spezzFooter()[0], spezzFooter()[1]);

        if (appProfileList.isEmpty()) {
            embed.setDescription("You don't have any activated apps yet.");
        } else for (AppProfile appProfile : appProfileList) {
            StringBuilder stringBuilder = new StringBuilder();
            License license = appProfile.getLicense();

            stringBuilder.append(String.format("* UID: %04d", appProfile.getUid()));
            stringBuilder.append("\n");
            stringBuilder.append(String.format("* Profile Name: %s", appProfile.getProfileName()));
            stringBuilder.append("\n");
            stringBuilder.append(String.format("* Hardware Id: %s", appProfile.getHardwareId() == null ? "`unset`" : "Set"));
            stringBuilder.append("\n");
            stringBuilder.append(String.format("* Expiration Date: %s", StringUtils.timestamp(license.getUsageDate(), license.getExpirationDate())));
            stringBuilder.append("\n");

            embed.addField(appProfile.getApplication().getName(), stringBuilder.toString(), false);
        }

        ReplyCallbackAction callback = event.replyEmbeds(embed.build());

        callback.addActionRow( //
                AdvancedButton.success().id("user_redeem_license_button").label("Redeem License").build(), //
                AdvancedButton.primary().id("user_manage_license_button").label("Manage License").disabled(appProfileList.isEmpty()).build() //
        );

        callback.addActionRow( //
                AdvancedButton.back().id("user_back_to_user_panel_button").build() //
        );

        callback.queue();
    }

    private void redeemLicenseButton(ButtonInteractionEvent event) {
        TextInput textInput = TextInput.create("user_redeem_license_input", "Code Input", TextInputStyle.SHORT) //
                .setRequiredRange(17, 17) //
                .setPlaceholder("XXXXX-XXXXX-XXXXX") //
                .build();

        Modal modal = Modal.create("user_redeem_license_modal", "Redeem License Form") //
                .addActionRow(textInput) //
                .build();

        event.replyModal(modal).queue();
    }

    private void redeemLicenseModal(ModalInteractionEvent event, Account account) {
        String code = event.getValue("user_redeem_license_input").getAsString();
        License license = licenseRepository.findByCode(code);

        if (license == null) {
            event.replyEmbeds(error().setDescription("Invalid license code").build()).queue();
            return;
        }

        if (license.getAccountId() != null) {
            event.replyEmbeds(error().setDescription("License code has already been used").build()).queue();
            return;
        }

        if (licenseRepository.findAllByAccountIdAndApplicationId(account.getId(), license.getApplicationId()).stream().anyMatch(licenseIn -> !licenseIn.hasExpired())) {
            event.replyEmbeds(warning().setDescription("You already have this application.").build()).queue();
            return;
        }

        String token;

        try {
            token = EncryptionUtils.encrypt(String.format("%s:%s", account.getDiscordId(), code));
        } catch (Exception exception) {
            event.replyEmbeds(ReportUtils.reportEmbed(exception)).queue();
            return;
        }

        String link = String.format("https://spezz.space/redeem/license?token=%s", token);
        MessageEmbed embed = info().setTitle("Redeem License").setDescription(String.format("To activate license, follow this link %s and pass verification.", link)).build();

        event.replyEmbeds(embed).queue();
    }

    private void manageLicenseButton(ButtonInteractionEvent event, Account account) {
        StringSelectMenu.Builder selectMenu = StringSelectMenu //
                .create("user_manage_license_menu") //
                .setPlaceholder("Choose license to manage");

        for (AppProfile appProfile : appProfileRepository.findAllByAccountId(account.getId())) {
            selectMenu.addOption(appProfile.getApplication().getName(), appProfile.getId().toString(), appProfile.getLicense().getCode());
        }

        event.deferReply().addActionRow(selectMenu.build()).queue();
    }

    private void licensePanel(GenericEvent event, String profileId) {
        AppProfile profile = appProfileRepository.findById(UUID.fromString(profileId)).get();
        Application application = applicationRepository.findById(profile.getApplicationId()).get();
        License license = licenseRepository.findById(profile.getLicenseId()).get();

        StringBuilder profileInfo = new StringBuilder();
        StringBuilder hardwareInfo = new StringBuilder();

        profileInfo.append(String.format("* **User Id:** %04d", profile.getUid()));
        profileInfo.append("\n");
        profileInfo.append(String.format("* **Name:** %s", profile.getProfileName()));
        profileInfo.append("\n");
        profileInfo.append(String.format("* **Role:** %s", profile.getRole().getFuncyName()));
        profileInfo.append("\n");

        hardwareInfo.append(String.format("* **Last Reset:** %s", profile.getLastHardwareIdReset() > 0 ? StringUtils.timestamp(profile.getLastHardwareIdReset()) : "`never`"));
        hardwareInfo.append("\n");
        hardwareInfo.append(String.format("* **Status:** %s", profile.getHardwareId() == null ? "`unset`" : "Set"));
        hardwareInfo.append("\n");

        MessageEmbed embed = info() //
                .setTitle("License Info") //
                .setAuthor(application.getName(), application.getWebsiteUrl(), application.getLogoUrl()) //
                .addField("Serial", license.getCode(), true) //
                .addField("Usage Date", StringUtils.timestamp(license.getUsageDate()), true) //
                .addField("Expiration Date", StringUtils.timestamp(license.getUsageDate(), license.getExpirationDate()), true) //
                .addField("Profile", profileInfo.toString(), false) //
                .addField("Hardware", hardwareInfo.toString(), false) //
                .setFooter(spezzFooter()[0], spezzFooter()[1]) //
                .build();

        Callback callback = Callback.builder(event).addEmbeds(embed);

        callback.addActionRow( //
                AdvancedButton.primary() //
                        .id("user_change_profile_name_button") //
                        .label("Change Profile Name") //
                        .values(profileId) //
                        .build(), //
                AdvancedButton.primary() //
                        .id("user_manage_profile_hardware_button") //
                        .label(String.format("%s Hardware Id", profile.getHardwareId() == null ? "Set" : "Reset")) //
                        .values(profileId) //
                        .build() //
        );

        callback.addActionRow( //
                AdvancedButton.back().id("user_library_button").build() //
        );

        callback.queue();
    }

    private void manageLicenseMenu(StringSelectInteractionEvent event) {
        licensePanel(event, event.getValues().get(0));
    }

    private void changeProfileNameButton(ButtonInteractionEvent event, String profileId) {
        TextInput textInput = TextInput.create("user_change_profile_name_input", "Name Input", TextInputStyle.SHORT) //
                .setPlaceholder("Profile Name") //
                .setRequiredRange(3, 20) //
                .build();

        Modal modal = ValueModal.create("user_change_profile_name_modal", "Change Profile Name Form", profileId) //
                .addActionRow(textInput) //
                .build();

        event.replyModal(modal).queue();
    }

    private void changeProfileNameModal(ModalInteractionEvent event, String profileId) {
        AppProfile profile = appProfileRepository.findById(UUID.fromString(profileId)).get();
        String profileName = event.getValue("user_change_profile_name_input").getAsString();

        if (StringUtils.startWithNumber(profileName)) {
            event.replyEmbeds(error().setDescription("Username can not start with number.").build()).queue();
            return;
        }

        if (StringUtils.containsSpecialSymbols(profileName)) {
            event.replyEmbeds(error().setDescription("Username can not contain special symbols.").build()).queue();
            return;
        }

        if (profileName.equalsIgnoreCase("null")) {
            event.replyEmbeds(error().setDescription(String.format("Username can not be equal to `%s`.", profileName)).build()).queue();
            return;
        }

        if (accountRepository.findByUsername(profileName) != null) {
            event.replyEmbeds(error().setDescription("User with such name is already exists.").build()).queue();
            return;
        }

        profile.setProfileName(profileName);
        appProfileRepository.save(profile);

        event.replyEmbeds(success().setDescription("Profile name has been changed.").build()).queue();
        licensePanel(event, profileId);
    }

    private void manageProfileHardwareButton(ButtonInteractionEvent event, String profileId) {
        AppProfile profile = appProfileRepository.findById(UUID.fromString(profileId)).get();

        if (profile.getHardwareId() == null) {
            TextInput textInput = TextInput.create("user_manage_profile_hardware_input", "Hardware Id Input", TextInputStyle.PARAGRAPH) //
                    .setPlaceholder("Hardware Id") //
                    .build();

            Modal modal = ValueModal.create("user_manage_profile_hardware_modal", "Set Hardware Id Form", profileId) //
                    .addActionRow(textInput) //
                    .build();

            event.replyModal(modal).queue();
            return;
        }

        long lastReset = profile.getLastHardwareIdReset();
        long resetPeriod = profile.getApplication().getHardwareIdResetPeriod();

        if (!TimerUtils.delay(lastReset, resetPeriod)) {
            event.replyEmbeds(error().setDescription(String.format("Next Hardware ID reset will be available on %s.", StringUtils.timestamp(lastReset, resetPeriod))).build()).queue();
            return;
        }

        profile.setLastHardwareIdReset(System.currentTimeMillis());
        profile.setHardwareId(null);
        appProfileRepository.save(profile);

        MessageEmbed embed = info() //
                .setDescription(String.format("Hardware Id has been reset for user %s(%04d).", profile.getProfileName(), profile.getUid())) //
                .build();

        event.replyEmbeds(embed).queue();
        licensePanel(event, profileId);
    }

    private void manageProfileHardwareModal(ModalInteractionEvent event, String profileId) {
        AppProfile profile = appProfileRepository.findById(UUID.fromString(profileId)).get();
        String hardwareId = event.getValue("user_manage_profile_hardware_input").getAsString();

        profile.setHardwareId(hardwareId);
        appProfileRepository.save(profile);

        MessageEmbed embed = info() //
                .setDescription(String.format("Hardware Id has been set for user %s(%04d).", profile.getProfileName(), profile.getUid())) //
                .build();

        event.replyEmbeds(embed).queue();
        licensePanel(event, profileId);
    }
}
