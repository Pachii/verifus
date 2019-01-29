package us.verif.bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.JDA;
import us.verif.bot.Sql;

public class Revoke extends Command {
    private JDA jda;

    public Revoke() {
        super.name = "revoke";
        super.ownerCommand = true;
        super.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        String guildID = event.getArgs();
        Sql.execute("Update", "remove `guildID` from `activatedservers` where `guildID` = '" + guildID + "`;");
        event.replyInDm("Server `" + jda.getGuildById(guildID) + "`'s activation has been revoked.");
    }
}
