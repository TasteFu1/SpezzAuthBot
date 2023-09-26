package ru.taste.utilities.discord;

import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;

import java.util.Objects;

import ru.taste.Instance;

public class ChannelUtils {
    public static PrivateChannel getChannelByUserId(String discordId) {
        for (PrivateChannel privateChannel : Instance.get().getJda().getPrivateChannels()) {
            if (Objects.requireNonNull(privateChannel.getUser()).getId().equals(discordId)) {
                return privateChannel;
            }
        }

        return null;
    }
}
