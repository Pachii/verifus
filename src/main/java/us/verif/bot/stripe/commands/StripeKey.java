package us.verif.bot.stripe.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import us.verif.bot.Config;
import us.verif.bot.sql.ActivationDatabase;
import us.verif.bot.sql.Sql;

public class StripeKey extends Command {

    private final static Logger LOGGER = Logger.getLogger(StripeKey.class.getName());
    private JDA jda;

    public StripeKey(JDA jda) {
        this.jda = jda;
        super.name = "stripekey";
        super.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {

        LOGGER.log(Level.INFO, event.getAuthor() + " executed the command '" + event.getMessage().getContentRaw() + "'");

        if (!jda.getGuildById(Config.getGuildId()).getMemberById(event.getAuthor().getId()).hasPermission(Permission.ADMINISTRATOR)) {
            event.reply("You cannot use this command.");
            return;
        }
        if (!ActivationDatabase.isActivated()) return;
        Sql.setStripeApiKey(event.getArgs());
        event.reply("Successfully attached stripe secret key to server. Securely stored key in database.");
        LOGGER.log(Level.INFO, "stripe Secret API key `" + event.getArgs() + "` has been entered into the database.");
    }
}
