package us.verif.bot.commands.Stripe;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Product;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import us.verif.bot.Sql;

import java.util.HashMap;
import java.util.Map;

public class CreateProduct extends Command {

    private final EventWaiter waiter;

    public CreateProduct(EventWaiter waiter) {
        this.waiter = waiter;
        super.name = "createproduct";
    }

    @Override
    protected void execute(CommandEvent event) {

        if (event.getAuthor().equals(event.getGuild().getOwner().getUser()) && Sql.activatedServersHas(event.getGuild().getId())) {

            Stripe.apiKey = Sql.getStripeKeyFromGuild(event.getGuild().getId());

            event.reply("Type the name of the product. type `cancel` to cancel.");

            Map<String, Object> productParams = new HashMap<>();

            waiter.waitForEvent(MessageReceivedEvent.class,
                    e -> e.getAuthor().equals(event.getAuthor()) && e.getChannel().equals(event.getChannel()),
                    e -> {
                        if (e.getMessage().getContentRaw().equals("cancel")) {
                            event.reply("Product creation canceled.");
                            return;
                        }
                        productParams.put("name", e.getMessage().getContentRaw());
                        try {
                            Product product = Product.create(productParams);

                            event.getChannel().sendMessage("Product Successfully Created: `" + product.getName() + "`").queue();
                        } catch (StripeException ex) {
                            System.out.println(ex.toString());
                            event.getChannel().sendMessage("Error: Invalid Stripe Key. refer to `/help stripe`.").queue();
                        }
                    });
        }
    }
}
