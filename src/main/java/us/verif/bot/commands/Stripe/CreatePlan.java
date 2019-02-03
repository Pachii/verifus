package us.verif.bot.commands.Stripe;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Plan;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import us.verif.bot.Sql;

import java.util.HashMap;
import java.util.Map;

public class CreatePlan extends Command {
    private final EventWaiter waiter;

    public CreatePlan(EventWaiter waiter) {
        this.waiter = waiter;
        super.name = "createplan";
    }

    @Override
    protected void execute(CommandEvent event) {

        if (event.getAuthor().equals(event.getGuild().getOwner().getUser()) && Sql.activatedServersHas(event.getGuild().getId())) {

            Stripe.apiKey = Sql.getStripeKeyFromGuild(event.getGuild().getId());

            Map<String, Object> productParams = new HashMap<>();

            productParams.put("type", "service");

            Map<String, Object> planParams = new HashMap<>();

            event.reply("You are now creating a product. type `cancel` any time to cancel creation.\n\nEnter the name of the role that subscribers will go in.");
            waiter.waitForEvent(MessageReceivedEvent.class,
                    e -> e.getAuthor().equals(event.getAuthor()) && e.getChannel().equals(event.getChannel()),
                    e -> {
                        if (e.getMessage().getContentRaw().equals("cancel")) {
                            event.reply("Product creation canceled.");
                            return;
                        }

                        planParams.put("nickname", e.getMessage().getContentRaw());

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
                                                                            planParams.put("interval", e4.getMessage().getContentRaw());
                                                                            Plan.create(planParams);
                                                                        } catch (StripeException ex) {
                                                                            ex.printStackTrace();
                                                                            event.getChannel().sendMessage("Error: Invalid Stripe key or product does not exist.. refer to `/help stripe`.").queue();
                                                                            return;
                                                                        }
                                                                        event.getChannel().sendMessage("Successfully created plan `" + e.getMessage().getContentRaw() + "`.").queue();
                                                                    });
                                                        });
                                            });
                                });
                    });
        }
    }
}
