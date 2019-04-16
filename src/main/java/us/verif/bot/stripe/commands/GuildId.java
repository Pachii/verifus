package us.verif.bot.stripe.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import us.verif.bot.Config;

public class GuildId extends Command {

    private final static Logger LOGGER = Logger.getLogger(GuildId.class.getName());

    public GuildId() {
        super.name = "guildid";
    }

    @Override
    protected void execute(CommandEvent event) {
        LOGGER.log(Level.INFO, event.getAuthor() + " executed the command '" + event.getMessage().getContentRaw() + "'");
        event.reply(Config.getGuildId());
    }
}
