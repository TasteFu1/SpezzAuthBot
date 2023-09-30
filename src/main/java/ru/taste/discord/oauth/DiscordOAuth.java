package ru.taste.discord.oauth;


import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Connection;
import org.jsoup.Jsoup;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import ru.taste.discord.oauth.model.TokensResponse;

@Slf4j
public class DiscordOAuth {
    private static final Gson gson = new GsonBuilder().serializeNulls().enableComplexMapKeySerialization().create();
    private static final String GRANT_TYPE_AUTHORIZATION = "authorization_code";
    private static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";
    private final String clientID;
    private final String clientSecret;
    private final String redirectUri;
    private final String[] scopes;

    private DiscordOAuth(String clientID, String clientSecret, String redirectUri, String[] scopes) {
        this.clientID = clientID;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.scopes = scopes;
    }

    public static DiscordOAuthBuilder builder() {
        return new DiscordOAuthBuilder();
    }

    private static TokensResponse toObject(String str) {
        return gson.fromJson(str, TokensResponse.class);
    }

    public String getAuthorizationURL(String state) {
        URIBuilder builder;

        try {
            builder = new URIBuilder(DiscordAPI.BASE_URI + "/oauth2/authorize");
        } catch (URISyntaxException e) {
            log.error("Failed to initialize URIBuilder", e);
            return null;
        }

        builder.addParameter("response_type", "code");
        builder.addParameter("client_id", clientID);
        builder.addParameter("redirect_uri", redirectUri);

        if (state != null && !state.isEmpty()) {
            builder.addParameter("state", state);
        }

        return builder + "&scope=" + String.join("%20", scopes);
    }

    public String getAuthorizationURL() {
        return getAuthorizationURL(null);
    }

    public TokensResponse getTokens(String code) throws IOException {
        Connection request = Jsoup.connect(DiscordAPI.BASE_URI + "/oauth2/token") //
                .data("client_id", clientID) //
                .data("client_secret", clientSecret) //
                .data("grant_type", GRANT_TYPE_AUTHORIZATION) //
                .data("code", code) //
                .data("redirect_uri", redirectUri) //
                .data("scope", String.join(" ", scopes)) //
                .ignoreContentType(true);

        return toObject(request.post().body().text());
    }

    public TokensResponse refreshTokens(String refresh_token) throws IOException {
        Connection request = Jsoup.connect(DiscordAPI.BASE_URI + "/oauth2/token") //
                .data("client_id", clientID) //
                .data("client_secret", clientSecret) //
                .data("grant_type", GRANT_TYPE_REFRESH_TOKEN) //
                .data("refresh_token", refresh_token) //
                .ignoreContentType(true);

        return toObject(request.post().body().text());
    }

    public static class DiscordOAuthBuilder {
        private String clientID;
        private String clientSecret;
        private String redirectUri;
        private String[] scopes;

        public DiscordOAuthBuilder clientID(String clientID) {
            this.clientID = clientID;
            return this;
        }

        public DiscordOAuthBuilder clientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public DiscordOAuthBuilder redirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
            return this;
        }

        public DiscordOAuthBuilder scopes(String... scopes) {
            this.scopes = scopes;
            return this;
        }

        public DiscordOAuth build() {
            return new DiscordOAuth(clientID, clientSecret, redirectUri, scopes);
        }
    }
}
