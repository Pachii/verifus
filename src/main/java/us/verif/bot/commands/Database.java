package us.verif.bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import us.verif.bot.Sql;

public class Database extends Command {
    public Database() {
        super.name = "database";
    }

    @Override
    protected void execute(CommandEvent event) {
        String guildID = event.getGuild().getId();
        if (Sql.activatedServersHas(guildID) && event.getGuild().getOwnerId().equals(event.getAuthor().getId())) {
            event.replyInDm("https://verif.us/database?guildID=" + guildID);
        }
    }
}
