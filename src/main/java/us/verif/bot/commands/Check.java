package us.verif.bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import us.verif.bot.Sql;

import java.util.Date;

public class Check extends Command {
    public Check() {
        super.name = "check";
    }

    @Override
    protected void execute(CommandEvent event) {
        String guildID = event.getGuild().getId();
        String userID = event.getAuthor().getId();
        if (Sql.activatedServersHas(guildID)) {
            Date expireDate = Sql.getUserExpireDate(userID, guildID);
            event.replyInDm("Your verification for the server `" + event.getGuild().getName() + "` will expire on `" + expireDate + "`.");
        }
    }
}
