package us.verif.bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.JDA;
import us.verif.bot.sql.ActivationDatabase;
import us.verif.bot.sql.Sql;

public class Revoke extends Command {
    private JDA jda;

    public Revoke(JDA jda) {
        this.jda = jda;
        super.name = "revoke";
        super.ownerCommand = true;
        super.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        String guildId = event.getArgs();
        ActivationDatabase.deleteActivatedGuild(guildId);
        event.replyInDm("Server `" + jda.getGuildById(guildId) + "`'s activation has been revoked.");
    }
}
