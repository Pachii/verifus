package us.verif.bot.commands.Stripe;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import us.verif.bot.Sql;

public class StripeKey extends Command {

    private JDA jda;
    private final EventWaiter waiter;

    public StripeKey(JDA jda, EventWaiter waiter) {
        this.waiter = waiter;
        this.jda = jda;
        super.name = "stripekey";
        super.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reply("Stripe Secret API Key attachment process started. type `cancel` any time to cancel.\n\nEnter the name of your server.");
        waiter.waitForEvent(MessageReceivedEvent.class,
                e -> e.getAuthor().equals(event.getAuthor()) && e.getChannel().equals(event.getChannel()),
                e -> {
                    if (e.getMessage().getContentRaw().equals("cancel")) {
                        event.reply("Process canceled.");
                        return;
                    }
                    String guildID = jda.getGuildsByName(e.getMessage().getContentRaw(), true).get(0).getId();

                    if (!event.getAuthor().getId().equals(jda.getGuildById(guildID).getOwnerId())) {
                        event.reply("You are not the owner of the specified server.");
                        return;
                    }
                    if (!Sql.activatedServersHas(guildID)) return;

                    event.reply("Enter your Stripe secret API key.");
                    waiter.waitForEvent(MessageReceivedEvent.class,
                            e1 -> e1.getAuthor().equals(event.getAuthor()) && e1.getChannel().equals(event.getChannel()),
                            e1 -> {
                                if (e1.getMessage().getContentRaw().equals("cancel")) {
                                    event.reply("Process canceled.");
                                    return;
                                }
                                String stripeKey = e1.getMessage().getContentRaw();
                                Sql.execute("Update", "replace into `serverInfo` (`guildID`,`stripeKey`) values ('" + guildID + "','" + stripeKey + "');");
                                event.reply("Successfully attached Stripe secret key to server. Securely stored key in database.");
                            });
                });
    }
}
