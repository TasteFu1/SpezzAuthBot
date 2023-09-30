package ru.taste.discord.oauth;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.taste.discord.oauth.model.Connection;
import ru.taste.discord.oauth.model.Guild;
import ru.taste.discord.oauth.model.User;

@Slf4j
@RequiredArgsConstructor
public class DiscordAPI {
    public static final String BASE_URI = "https://discord.com/api";
    private static final Gson gson = new GsonBuilder().serializeNulls().enableComplexMapKeySerialization().create();
    private final String accessToken;

    private static <T> T toObject(String str, Class<T> clazz) {
        return gson.fromJson(str, clazz);
    }

    private String getVersion() throws IOException {
        InputStream resourceAsStream = this.getClass().getResourceAsStream("/version.properties");
        java.util.Properties prop = new java.util.Properties();
        prop.load(resourceAsStream);
        return prop.getProperty("version");
    }

    private void setHeaders(org.jsoup.Connection request) throws IOException {
        request.header("Authorization", "Bearer " + accessToken);
        request.header("User-Agent", String.format("Mokulu-Discord-OAuth2-Java, version %s, platform %s %s", getVersion(), System.getProperty("os.name"), System.getProperty("os.version")));
    }

    private String handleGet(String path) throws IOException {
        org.jsoup.Connection request = Jsoup.connect(BASE_URI + path).ignoreContentType(true);
        setHeaders(request);

        return request.get().body().text();
    }

    public User fetchUser() throws IOException {
        return toObject(handleGet("/users/@me"), User.class);
    }

    public List<Guild> fetchGuilds() throws IOException {
        return Arrays.asList(toObject(handleGet("/users/@me/guilds"), Guild[].class));
    }

    public List<Connection> fetchConnections() throws IOException {
        return Arrays.asList(toObject(handleGet("/users/@me/connections"), Connection[].class));
    }
}
