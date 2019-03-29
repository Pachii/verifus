package us.verif.bot;

import net.dv8tion.jda.core.entities.User;

public class Helpers {

    public static void sendPrivateMessage(User user, String content) {
        user.openPrivateChannel().queue((channel) -> channel.sendMessage(content).queue());
    }
}
