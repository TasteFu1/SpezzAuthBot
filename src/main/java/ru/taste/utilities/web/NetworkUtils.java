package ru.taste.utilities.web;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.thymeleaf.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

public class NetworkUtils {
    private static final String H_CAPTCHA_SECRET = "0x6c89A5bBce4d6871116c286bBEE0A176b9A924b6";
    private static final String H_CAPTCHA_VERIFY_URL = "https://hcaptcha.com/siteverify";
    private static final String VPN_CHECK_URL = "https://beta.iprisk.info/v1";

    public static boolean isCaptchaInvalid(String captchaResponse) throws IOException {
        if (StringUtils.isEmpty(captchaResponse)) {
            return true;
        }

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(H_CAPTCHA_VERIFY_URL);

        List<NameValuePair> pairList = new ArrayList<>();

        pairList.add(new BasicNameValuePair("response", captchaResponse));
        pairList.add(new BasicNameValuePair("secret", H_CAPTCHA_SECRET));

        httpPost.setEntity(new UrlEncodedFormEntity(pairList));

        HttpResponse response = httpClient.execute(httpPost);
        String responseString = EntityUtils.toString(response.getEntity());

        if (StringUtils.isEmpty(responseString)) {
            return true;
        }

        JsonObject responseJson = JsonParser.parseString(responseString).getAsJsonObject();

        if (responseJson == null) {
            return true;
        }

        if (!responseJson.has("success")) {
            return true;
        }

        return !responseJson.get("success").getAsBoolean();
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");

        if (ipAddress == null || ipAddress.isEmpty() || ipAddress.equalsIgnoreCase("unknown")) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }

        if (ipAddress == null || ipAddress.isEmpty() || ipAddress.equalsIgnoreCase("unknown")) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ipAddress == null || ipAddress.isEmpty() || ipAddress.equalsIgnoreCase("unknown")) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }

        if (ipAddress == null || ipAddress.isEmpty() || ipAddress.equalsIgnoreCase("unknown")) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }

        if (ipAddress == null || ipAddress.isEmpty() || ipAddress.equalsIgnoreCase("unknown")) {
            ipAddress = request.getRemoteAddr();
        }

        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }

        return ipAddress;
    }

    public static boolean isUsingVPN(String ipAddress) throws IOException {
        if (StringUtils.isEmpty(ipAddress)) {
            return false;
        }

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(String.format("%s/%s", VPN_CHECK_URL, ipAddress));

        HttpResponse response = httpClient.execute(httpGet);
        String responseString = EntityUtils.toString(response.getEntity());

        if (StringUtils.isEmpty(responseString)) {
            return false;
        }

        JsonObject responseJson = JsonParser.parseString(responseString).getAsJsonObject();

        if (responseJson == null) {
            return false;
        }

        if (!responseJson.has("data_center") && !responseJson.has("public_proxy") && !responseJson.has("tor_exit_relay")) {
            return false;
        }

        return responseJson.get("data_center").getAsBoolean() || responseJson.get("public_proxy").getAsBoolean() || responseJson.get("tor_exit_relay").getAsBoolean();
    }

    public static boolean isUsingVPN(HttpServletRequest request) throws IOException {
        return isUsingVPN(getIpAddress(request));
    }
}
