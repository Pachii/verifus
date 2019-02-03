package us.verif.bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import us.verif.bot.Sql;

import java.util.ArrayList;
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
            try {
                ArrayList<Date> expireDates = Sql.getUserExpireDateList(userID, guildID);
                for (Date expireDate : expireDates) {
                    event.replyInDm("Your role for the server `" + event.getGuild().getName() + "` will expire on `" + expireDate + "`.");
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}
