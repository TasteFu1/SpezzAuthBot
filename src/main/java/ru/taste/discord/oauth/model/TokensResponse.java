package ru.taste.discord.oauth.model;

import com.google.gson.annotations.SerializedName;

import java.io.IOException;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import ru.taste.discord.oauth.DiscordAPI;

@Data
@Setter(AccessLevel.NONE)
public class TokensResponse {
    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("token_type")
    private String tokenType;
    @SerializedName("expires_in")
    private int expiresIn;
    @SerializedName("refresh_token")
    private String refreshToken;
    private String scope;

    public DiscordAPI getDiscordAPI() {
        return new DiscordAPI(accessToken);
    }

    public User getUser() throws IOException {
        return getDiscordAPI().fetchUser();
    }

    public String getDiscordId() throws IOException {
        return getDiscordAPI().fetchUser().getId();
    }
}
