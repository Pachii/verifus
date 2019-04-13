package us.verif.bot.stripe.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import us.verif.bot.Config;
import us.verif.bot.sql.ActivationDatabase;
import us.verif.bot.sql.StripeSql;

public class StripeKey extends Command {

    private final EventWaiter waiter;
    private JDA jda;
    private final static Logger LOGGER = Logger.getLogger(StripeKey.class.getName());


    public StripeKey(JDA jda, EventWaiter waiter) {
        this.waiter = waiter;
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
        event.reply("stripe Secret API Key attachment process started. type `cancel` any time to cancel. Enter your stripe secret API key.");
        waiter.waitForEvent(MessageReceivedEvent.class,
                e -> e.getAuthor().equals(event.getAuthor()) && e.getChannel().equals(event.getChannel()),
                e -> {
                    if (e.getMessage().getContentRaw().equals("cancel")) {
                        event.reply("Process canceled.");
                        return;
                    }
                    String stripeKey = e.getMessage().getContentRaw();
                    StripeSql.setApiKey(stripeKey);
                    event.reply("Successfully attached stripe secret key to server. Securely stored key in database.");
                    LOGGER.log(Level.INFO, "stripe Secret API key " + stripeKey + " has been entered into the database.");
                });
    }
}
