package ru.taste.utilities.discord;

import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;

import java.util.Objects;

import ru.taste.Instance;

public class ChannelUtils {
    public static PrivateChannel getChannelByUserId(String discordId) {
        for (PrivateChannel channel : Instance.get().getJda().getPrivateChannels()) {
            if (channel.getUser() != null && channel.getUser().getId().equals(discordId)) {
                return channel;
            }
        }

        return null;
    }
}
