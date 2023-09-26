package ru.taste.utilities.xchart;

import net.dv8tion.jda.api.utils.FileUpload;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.XYStyler;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import ru.taste.database.entities.License;

public class ChartUtils {
    private static long daysToMillis(int days) {
        return TimeUnit.DAYS.toMillis(days);
    }

    public static XYChart getSalesStatisticChart(List<License> licenseList, String format) {
        List<Date> dates = new ArrayList<>();
        List<Integer> sales = new ArrayList<>();

        long currentMillis = System.currentTimeMillis();

        switch (format) {
            case "last7" -> {
                for (long l = currentMillis - daysToMillis(7); l < currentMillis; l += daysToMillis(1)) {
                    int saleCounter = 0;

                    for (License license : licenseList) {
                        if (license.getUsageDate() >= l && license.getUsageDate() <= l + daysToMillis(1)) {
                            saleCounter++;
                        }
                    }

                    dates.add(new Date(l));
                    sales.add(saleCounter);
                }
            }

            case "last24" -> {
                for (long l = currentMillis - daysToMillis(1); l <= currentMillis; l += daysToMillis(1) / 12) {
                    int saleCounter = 0;

                    for (License license : licenseList) {
                        if (license.getUsageDate() >= l && license.getUsageDate() <= l + daysToMillis(1) / 12) {
                            saleCounter++;
                        }
                    }

                    dates.add(new Date(l));
                    sales.add(saleCounter);
                }
            }

            case "last30" -> {
                for (long l = currentMillis - daysToMillis(30); l <= currentMillis; l += daysToMillis(1)) {
                    int saleCounter = 0;

                    for (License license : licenseList) {
                        if (license.getUsageDate() >= l && license.getUsageDate() <= l + daysToMillis(1)) {
                            saleCounter++;
                        }
                    }

                    dates.add(new Date(l));
                    sales.add(saleCounter);
                }
            }

            case "last180" -> {
                for (long l = currentMillis - daysToMillis(180); l <= currentMillis; l += daysToMillis(30)) {
                    int saleCounter = 0;

                    for (License license : licenseList) {
                        if (license.getUsageDate() >= l && license.getUsageDate() <= l + daysToMillis(30)) {
                            saleCounter++;
                        }
                    }

                    dates.add(new Date(l));
                    sales.add(saleCounter);
                }
            }

            case "last365" -> {
                for (long l = currentMillis - daysToMillis(365); l <= currentMillis; l += daysToMillis(30)) {
                    int saleCounter = 0;

                    for (License license : licenseList) {
                        if (license.getUsageDate() >= l && license.getUsageDate() <= l + daysToMillis(30)) {
                            saleCounter++;
                        }
                    }

                    dates.add(new Date(l));
                    sales.add(saleCounter);
                }
            }

            case "all" -> {
                for (long l = 50 * daysToMillis(365); l <= (int) Math.ceil((double) currentMillis / daysToMillis(365)) * daysToMillis(365); l += daysToMillis(365)) {
                    int saleCounter = 0;

                    for (License license : licenseList) {
                        if (license.getUsageDate() >= l && license.getUsageDate() <= l + daysToMillis(365)) {
                            saleCounter++;
                        }
                    }

                    dates.add(new Date(l));
                    sales.add(saleCounter);
                }
            }
        }

        XYChart chart = new XYChartBuilder().width(1600).height(800).title("Sales Statistic").xAxisTitle("Date").yAxisTitle("Orders").build();
        XYStyler styler = chart.getStyler();

        XYSeries series = chart.addSeries("_", dates, sales);

        series.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        series.setSmooth(true);

        switch (format) {
            case "last7" -> styler.setDatePattern("dd-MMM");
            case "last30" -> styler.setDatePattern("dd-MM");
            case "last24" -> styler.setDatePattern("HH:mm");
            case "last365", "last180" -> styler.setDatePattern("yyyy-MMM");
            case "all" -> styler.setDatePattern("yyyy");
        }

        styler.setYAxisDecimalPattern("0");
        styler.setLocale(Locale.ENGLISH);

        styler.setChartFontColor(Color.WHITE);
        styler.setAxisTickLabelsColor(Color.WHITE);
        styler.setSeriesColors(new Color[]{new Color(80, 100, 240)});

        styler.setPlotBorderVisible(false);
        styler.setAxisTicksLineVisible(false);
        styler.setAxisTicksMarksVisible(false);

        styler.setChartBackgroundColor(new Color(34, 36, 41));
        styler.setPlotBackgroundColor(new Color(34, 36, 41));

        styler.setPlotGridLinesColor(Color.LIGHT_GRAY);
        styler.setPlotGridLinesStroke(new BasicStroke(1));

        return chart;
    }

    public static FileUpload getStatisticChartFile(List<License> licenseList, String format) throws IOException {
        byte[] bytes = BitmapEncoder.getBitmapBytes(getSalesStatisticChart(licenseList, format), BitmapEncoder.BitmapFormat.PNG);
        String fileName = String.format("sales_statistic_%s_days.png", format);

        return FileUpload.fromData(bytes, fileName);
    }
}
