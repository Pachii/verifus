package us.verif.bot.Stripe.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.stripe.Stripe;
import com.stripe.model.Subscription;
import us.verif.bot.sql.StripeSql;

import java.util.HashMap;
import java.util.Map;

public class Cancel extends Command {
    private final EventWaiter waiter;

    public Cancel(EventWaiter waiter) {
        this.waiter = waiter;
        super.name = "cancel";
        super.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {

        Stripe.apiKey = StripeSql.getStripeApiKey();
        String key = event.getArgs();

        try {
            String subscriptionId = StripeSql.getSubscriptionByStripeKey(key);
            Subscription subscription = Subscription.retrieve(subscriptionId);
            if (subscription == null) {
                event.reply("Error: Key not found.");
                return;
            }
            Map<String, Object> params = new HashMap<>();
            params.put("cancel_at_period_end", true);
            subscription.update(params);
            event.replyInDm("Your subscription has been canceled. Your role will be removed at the end of the current period.");
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
}


