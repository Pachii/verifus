package us.verif.bot.Stripe.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import us.verif.bot.Config;
import us.verif.bot.sql.ActivationDatabase;
import us.verif.bot.sql.StripeSql;

public class EmailSubject extends Command {
    private JDA jda;

    public EmailSubject(JDA jda) {
        this.jda = jda;
        super.name = "emailsubject";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (jda.getGuildById(Config.getGuildId()).getMemberById(event.getAuthor().getId()).hasPermission(Permission.ADMINISTRATOR) && ActivationDatabase.isActivated()) {
            StripeSql.setEmailSubject(event.getArgs());
            event.reply("Email subject set.");
        }
    }
}