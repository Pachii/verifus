package us.verif.bot.events;

import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class WhenJoinGuild extends ListenerAdapter {
    public void onGuildJoin(GuildJoinEvent event) {
        event.getGuild().getSystemChannel().sendMessage("Thank you for using Verifus. To activate, please type in any server text channel `/activate `" +
                "\nfollowed by your one-time use activation serial key. For more information, type `/help`.").queue();
    }
}
