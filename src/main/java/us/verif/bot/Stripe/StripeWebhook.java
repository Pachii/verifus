package us.verif.bot.Stripe;

import com.stripe.Stripe;
import com.stripe.model.*;
import com.stripe.net.ApiResource;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Role;
import org.apache.commons.httpclient.HttpStatus;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import us.verif.bot.Config;
import us.verif.bot.sql.StripeSql;

import java.security.SecureRandom;
import java.util.EventListener;
import java.util.Random;

import static spark.Spark.port;
import static spark.Spark.post;

public class StripeWebhook implements EventListener {

    static final private String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    final private Random rng = new SecureRandom();
    private JDA jda;


    public StripeWebhook(JDA jda) {
        this.jda = jda;
    }

    public void startListener() {

        port(Integer.parseInt(Config.getStripeWebhookPort()));


        post("/webhook", (request, response) -> {
            Event event = ApiResource.GSON.fromJson(request.body(), Event.class);

            if (event.getType().equals("customer.subscription.deleted")) {
                Subscription subscriptionObject = (Subscription) event.getData().getObject();
                String subscriptionId = subscriptionObject.getId();
                try {
                    Stripe.apiKey = StripeSql.getStripeApiKey();
                    if (StripeSql.subscriptionExists(subscriptionId)) {
                        Subscription subscription = Subscription.retrieve(subscriptionId);
                        if (subscription == null) return "";
                        String key = StripeSql.getKeyBySubscription(subscriptionId);
                        String userId = StripeSql.getUserByStripeKey(key);
                        String roleName = jda.getGuildById(Config.getGuildId()).getRoleById(StripeSql.getRoleByKey(key)).getName();
                        Role removeRole = jda.getGuildById(Config.getGuildId()).getRoleById(StripeSql.getRoleByKey(key));
                        jda.getGuildById(Config.getGuildId()).getController().removeSingleRoleFromMember(jda.getGuildById(Config.getGuildId()).getMemberById(userId), removeRole).queue();
                        StripeSql.removeKeyAndStripeUser(key);
                        jda.getUserById(userId).openPrivateChannel().queue((channel) -> channel.sendMessage("Your subscription for the role `" + roleName + "` has been canceled.").queue());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (event.getType().equals("invoice.payment_failed")) {
                Invoice invoice = (Invoice) event.getData().getObject();
                String subscriptionId = invoice.getSubscription();

                try {
                    Stripe.apiKey = StripeSql.getStripeApiKey();
                    if (StripeSql.subscriptionExists(subscriptionId)) {
                        Subscription subscription = Subscription.retrieve(subscriptionId);
                        if (subscription == null) return "";
                        String key = StripeSql.getKeyBySubscription(subscriptionId);
                        String userId = StripeSql.getUserByStripeKey(key);
                        String roleName = jda.getGuildById(Config.getGuildId()).getRoleById(StripeSql.getRoleByKey(key)).getName();
                        Role removeRole = jda.getGuildById(Config.getGuildId()).getRoleById(StripeSql.getRoleByKey(key));
                        jda.getGuildById(Config.getGuildId()).getController().removeSingleRoleFromMember(jda.getGuildById(Config.getGuildId()).getMemberById(userId), removeRole).queue();
                        String stripeKey = StripeSql.getKeyBySubscription(subscriptionId);
                        StripeSql.removeKeyAndStripeUser(stripeKey);
                        jda.getUserById(userId).openPrivateChannel().queue((channel) -> channel.sendMessage("Your payment for the role `" + roleName + "` has failed. Your role was removed.").queue());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            if (event.getType().equals("invoice.payment_succeeded")) {
                Invoice invoice = (Invoice) event.getData().getObject();
                String subscriptionId = invoice.getSubscription();

                try {
                    Stripe.apiKey = StripeSql.getStripeApiKey();
                    Subscription subscription = Subscription.retrieve(subscriptionId);
                    if (subscription == null) return "";

                    if (StripeSql.userHasStripeKey(StripeSql.getKeyBySubscription(subscriptionId))) {
                        String key = StripeSql.getKeyBySubscription(subscriptionId);
                        String userId = StripeSql.getUserByStripeKey(key);
                        String roleName = jda.getGuildById(Config.getGuildId()).getRoleById(StripeSql.getRoleByKey(key)).getName();
                        jda.getUserById(userId).openPrivateChannel().queue((channel) -> channel.sendMessage("Your role `" + roleName + "` has been automatically renewed.").queue());
                        return "";
                    }

                    Plan plan = subscription.getPlan();
                    Customer customer = Customer.retrieve(invoice.getCustomer());
                    String customerEmail = customer.getEmail();
                    String key = randomUUID(12, 4, '-');
                    String roleId = plan.getNickname();
                    StripeSql.registerKey(key, subscription.getId(), roleId);

                    Email email = EmailBuilder.startingBlank().from("Auth", "verifus.auth@gmail.com").to("Member", customerEmail).withSubject(StripeSql.getEmailSubject()).withHTMLText(StripeSql.getEmailHtml() + key).buildEmail();
                    MailerBuilder.withSMTPServer("smtp.gmail.com", 587, "verifus.auth@gmail.com", "Verifus168").buildMailer().sendMail(email);
                    return "";
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

            response.status(HttpStatus.SC_OK);
            return "";
        });
    }

    private char randomChar() {
        return ALPHABET.charAt(rng.nextInt(ALPHABET.length()));
    }

    private String randomUUID(int length, int spacing, char spacerChar) {
        StringBuilder sb = new StringBuilder();
        int spacer = 0;
        while (length > 0) {
            if (spacer == spacing) {
                sb.append(spacerChar);
                spacer = 0;
            }
            length--;
            spacer++;
            sb.append(randomChar());
        }
        return sb.toString();
    }
}
