package us.verif.bot.Stripe.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import us.verif.bot.Config;
import us.verif.bot.sql.ActivationDatabase;
import us.verif.bot.sql.Sql;
import us.verif.bot.sql.StripeSql;

public class EmailHtml extends Command {
    private JDA jda;

    public EmailHtml(JDA jda) {
        this.jda = jda;
        super.name = "emailhtml";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (jda.getGuildById(Config.getGuildId()).getMemberById(event.getAuthor().getId()).hasPermission(Permission.ADMINISTRATOR) && ActivationDatabase.isActivated()) {
            StripeSql.setEmailHtml(event.getArgs());
            event.reply("Email HTML set.");
        }
    }
}