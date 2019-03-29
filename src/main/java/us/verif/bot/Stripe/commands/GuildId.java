package us.verif.bot.Stripe.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import us.verif.bot.Config;

public class GuildId extends Command {

    public GuildId() {
        super.name = "serverid";
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reply(Config.getGuildId());
    }
}
