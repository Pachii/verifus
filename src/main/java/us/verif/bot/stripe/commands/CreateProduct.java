package us.verif.bot.stripe.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Product;
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

public class CreateProduct extends Command {

    private final EventWaiter waiter;
    private final static Logger LOGGER = Logger.getLogger(CreateProduct.class.getName());
    private JDA jda;

    public CreateProduct(EventWaiter waiter, JDA jda) {
        this.jda = jda;
        this.waiter = waiter;
        super.name = "createproduct";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {

        LOGGER.log(Level.INFO, event.getAuthor() + " executed the command /createproduct '" + event.getMessage().getContentRaw() + "'");

        if (jda.getGuildById(Config.getGuildId()).getMemberById(event.getAuthor().getId()).hasPermission(Permission.ADMINISTRATOR) && ActivationDatabase.isActivated()) {

            Stripe.apiKey = StripeSql.getStripeApiKey();

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
                            productParams.put("type", "service");
                            Product product = Product.create(productParams);

                            event.getChannel().sendMessage("Product Successfully Created: `" + product.getName() + "`").queue();

                        } catch (StripeException ex) {
                            System.out.println(ex.toString());
                            event.getChannel().sendMessage("Error: Invalid StripeSql Key. refer to `/help stripe`.").queue();
                        }
                    });
        }
    }
}
