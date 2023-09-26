package ru.taste.utilities.throwable;

import net.dv8tion.jda.api.entities.MessageEmbed;

import org.springframework.web.servlet.ModelAndView;

import ru.taste.Instance;
import ru.taste.database.entities.Report;
import ru.taste.utilities.discord.EmbedUtils;
import ru.taste.utilities.web.ResponseUtils;

public class ReportUtils {
    private static String getStackTraceAsString(Throwable throwable) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(throwable.getMessage());
        stringBuilder.append("\n");

        for (StackTraceElement stackTraceElement : throwable.getStackTrace()) {
            stringBuilder.append(stackTraceElement.toString());
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }

    public static ModelAndView reportModel(Throwable throwable) {
        Report report = Report.builder().stackTrace(getStackTraceAsString(throwable)).build();

        Instance.get().getReportRepository().save(report);

        return ResponseUtils.error(String.format("An unexpected error has occurred, please report this error ID %s to admin.", report.getId()));
    }

    public static MessageEmbed reportEmbed(Throwable throwable) {
        Report report = Report.builder().stackTrace(getStackTraceAsString(throwable)).build();

        Instance.get().getReportRepository().save(report);

        return EmbedUtils.error() //
                .setDescription(String.format("An unexpected error has occurred, please report this error ID %s to admin.", report.getId())) //
                .setFooter(EmbedUtils.spezzFooter()[0], EmbedUtils.spezzFooter()[1]) //
                .build();
    }
}
