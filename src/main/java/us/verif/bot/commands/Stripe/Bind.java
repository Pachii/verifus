package us.verif.bot.commands.Stripe;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Subscription;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import us.verif.bot.Sql;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Bind extends Command {

    private JDA jda;

    public Bind(JDA jda) {
        this.jda = jda;
        super.name = "bind";
        super.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {

        String[] args = event.getMessage().getContentRaw().split(" ");
        User author = event.getAuthor();

        if (args.length < 3) {
            event.getChannel().sendMessage("The proper command usage is `/bind <email> <serverID>`").queue();
            return;
        }

        String email = args[1];
        String guildID = args[2];
        Stripe.apiKey = Sql.getStripeKeyFromGuild(guildID);

        Map<String, Object> subscriptionParams = new HashMap<>();
        subscriptionParams.put("limit", 100);

        if (Sql.activatedServersHas(guildID)) {
            try {
                for (Subscription subscription : Subscription.list(subscriptionParams).autoPagingIterable()) {
                    Customer customer = Customer.retrieve(subscription.getCustomer());
                    String role = subscription.getPlan().getNickname();
                    String receiptEmail = customer.getEmail();

                    if (!receiptEmail.equalsIgnoreCase(email)) continue;
                    String customerId = Sql.getCustomerByEmail(email);

                    if (customerId == null) {
                        Sql.addCustomer(event.getAuthor(), customer.getId(), receiptEmail, guildID);
                    } else {
                        String discordId = Sql.getDiscordIdByCustomer(customer.getId());

                        if (!discordId.equalsIgnoreCase(author.getId())) {
                            event.getChannel().sendMessage("Your Discord account is not tied to this email.").queue();
                            return;
                        }
                    }

                    Role addRole = jda.getGuildById(guildID).getRolesByName(role, true).get(0);

                    if (Sql.subscriptionRegistered(subscription)) return;
                    jda.getGuildById(guildID).getController().addSingleRoleToMember(jda.getGuildById(guildID).getMemberById(author.getId()), addRole).queue();
                    event.reply("Successfully tied email" + receiptEmail + "to your Discord account and activated your subscription role " + subscription.getPlan().getNickname() + " on " + jda.getGuildById(guildID).getName() + ".");
                    Sql.registerSubscription(subscription, customer);
                }
            } catch (StripeException ex) {
                System.out.println(ex.toString());
                event.getChannel().sendMessage("Stripe exception has occurred.").queue();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
