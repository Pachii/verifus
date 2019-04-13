package us.verif.bot.stripe.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Plan;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import us.verif.bot.Config;
import us.verif.bot.sql.ActivationDatabase;
import us.verif.bot.sql.StripeSql;

import java.util.HashMap;
import java.util.Map;

public class CreatePlan extends Command {

    private final EventWaiter waiter;
    private JDA jda;
    private final static Logger LOGGER = Logger.getLogger(CreatePlan.class.getName());


    public CreatePlan(EventWaiter waiter, JDA jda) {
        this.jda = jda;
        this.waiter = waiter;
        super.name = "createplan";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {

        LOGGER.log(Level.INFO, event.getAuthor() + " executed the command '" + event.getMessage().getContentRaw() + "'");

        if (jda.getGuildById(Config.getGuildId()).getMemberById(event.getAuthor().getId()).hasPermission(Permission.ADMINISTRATOR) && ActivationDatabase.isActivated()) {

            Stripe.apiKey = StripeSql.getStripeApiKey();

            Map<String, Object> productParams = new HashMap<>();

            productParams.put("type", "service");

            Map<String, Object> planParams = new HashMap<>();

            event.reply("You are now creating a plan. type `cancel` any time to cancel creation.\n\nEnter the name of the role that subscribers will go in.");
            waiter.waitForEvent(MessageReceivedEvent.class,
                    e -> e.getAuthor().equals(event.getAuthor()) && e.getChannel().equals(event.getChannel()),
                    e -> {
                        if (e.getMessage().getContentRaw().equals("cancel")) {
                            event.reply("Product creation canceled.");
                            return;
                        }

                        planParams.put("nickname", jda.getGuildById(Config.getGuildId()).getRolesByName(e.getMessage().getContentRaw(), true).get(0).getId());

                        event.reply("Enter the product ID that this plan will go in (Billing > Products > Your Product). Make sure the product already exists.");
                        waiter.waitForEvent(MessageReceivedEvent.class,
                                e1 -> e1.getAuthor().equals(event.getAuthor()) && e1.getChannel().equals(event.getChannel()),
                                e1 -> {
                                    if (e1.getMessage().getContentRaw().equals("cancel")) {
                                        event.reply("Product creation canceled.");
                                        return;
                                    }
                                    planParams.put("product", e1.getMessage().getContentRaw());

                                    event.reply("Enter the currency. Ex. `usd`, `eur`, `gbp`");
                                    waiter.waitForEvent(MessageReceivedEvent.class,
                                            e2 -> e2.getAuthor().equals(event.getAuthor()) && e2.getChannel().equals(event.getChannel()),
                                            e2 -> {
                                                if (e2.getMessage().getContentRaw().equals("cancel")) {
                                                    event.reply("Product creation canceled.");
                                                    return;
                                                }

                                                planParams.put("currency", e2.getMessage().getContentRaw());

                                                event.reply("Enter the amount in `" + e2.getMessage().getContentRaw() + "`.");
                                                waiter.waitForEvent(MessageReceivedEvent.class,
                                                        e3 -> e3.getAuthor().equals(event.getAuthor()) && e3.getChannel().equals(event.getChannel()),
                                                        e3 -> {
                                                            if (e3.getMessage().getContentRaw().equals("cancel")) {
                                                                event.reply("Product creation canceled.");
                                                                return;
                                                            }
                                                            int amountInt = (int) Math.round(Double.parseDouble(e3.getMessage().getContentRaw()) * 100);
                                                            planParams.put("amount", Integer.toString(amountInt));

                                                            event.reply("Enter the subscription interval. Ex. `month`");
                                                            waiter.waitForEvent(MessageReceivedEvent.class,
                                                                    e4 -> e4.getAuthor().equals(event.getAuthor()) && e4.getChannel().equals(event.getChannel()),
                                                                    e4 -> {
                                                                        if (e4.getMessage().getContentRaw().equals("cancel")) {
                                                                            event.reply("Product creation canceled.");
                                                                            return;
                                                                        }
                                                                        try {
                                                                            planParams.put("interval", e4.getMessage().getContentRaw().toLowerCase());
                                                                            Plan.create(planParams);
                                                                        } catch (StripeException ex) {
                                                                            ex.printStackTrace();
                                                                            event.getChannel().sendMessage("Error: Invalid Stripe key or product does not exist. refer to `/help stripe`.").queue();
                                                                            return;
                                                                        }
                                                                        event.getChannel().sendMessage("Successfully created plan `" + e.getMessage().getContentRaw() + "`. If you ever change the name of the role, please update " +
                                                                                "this plan to the name of the new role for it to continue working.").queue();
                                                                    });
                                                        });
                                            });
                                });
                    });
        }
    }
}
