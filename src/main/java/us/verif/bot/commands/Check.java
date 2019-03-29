package us.verif.bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import us.verif.bot.sql.ActivationDatabase;
import us.verif.bot.sql.Sql;

import java.util.ArrayList;
import java.util.Date;

public class Check extends Command {
    public Check() {
        super.name = "check";
    }

    @Override
    protected void execute(CommandEvent event) {
        String userID = event.getAuthor().getId();
        if (ActivationDatabase.isActivated()) {
            try {
                ArrayList<Date> expireDates = Sql.getUserExpireDateList(userID);
                for (Date expireDate : expireDates) {
                    event.replyInDm("Your role will expire on `" + expireDate + "`.");
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}
