package us.verif.bot.stripe;

import com.stripe.Stripe;
import com.stripe.model.*;
import com.stripe.net.ApiResource;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Invite;
import net.dv8tion.jda.core.entities.Role;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import us.verif.bot.Config;
import us.verif.bot.sql.StripeSql;

import java.security.SecureRandom;
import java.util.EventListener;
import java.util.List;
import java.util.Random;

import static spark.Spark.port;
import static spark.Spark.post;

public class StripeWebhook implements EventListener {

    static final private String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final static Logger LOGGER = Logger.getLogger(StripeWebhook.class.getName());
    final private Random rng = new SecureRandom();
    private JDA jda;

    public StripeWebhook(JDA jda) {
        this.jda = jda;
    }

    public void startListener() {

        port(Integer.parseInt(Config.getStripeWebhookPort()));

        post("/" + Config.getStripeWebhookUrl(), (request, response) -> {
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
                        LOGGER.log(Level.INFO, "customer.subscription.deleted received. Key " + key + " deleted for " + jda.getUserById(userId));
                        return "";
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
                        LOGGER.log(Level.INFO, "invoice.payment_failed received. Key " + key + " deleted for " + jda.getUserById(userId));
                        return "";
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
                        LOGGER.log(Level.INFO, "invoice.payment_succeeded received. Key " + key + " renewed for " + jda.getUserById(userId));
                        return "";
                    }

                    List<Invite> i = jda.getGuildById(Config.getGuildId()).getInvites().complete();
                    Invite link;

                    if (i.size() > 1) {
                        if (i.get(0).isTemporary()) {
                            link = jda.getGuildById(Config.getGuildId()).getSystemChannel().createInvite().setTemporary(false).complete();
                        } else {
                            link = i.get(0);
                        }
                    } else {
                        link = jda.getGuildById(Config.getGuildId()).getSystemChannel().createInvite().setTemporary(false).complete();
                    }
                    Plan plan = subscription.getPlan();
                    Customer customer = Customer.retrieve(invoice.getCustomer());
                    String customerEmail = customer.getEmail();
                    String key = randomUUID(12, 4, '-');
                    String roleId = plan.getNickname();
                    StripeSql.registerKey(key, subscription.getId(), roleId);
                    Email email = EmailBuilder.startingBlank().from("Auth", "verifus.auth@gmail.com").to("Member", customerEmail).withSubject(jda.getGuildById(Config.getGuildId()).getName() + " Membership Key").withHTMLText(
                            "<body class=\"\" style=\"background-color: #fff;font-family: sans-serif;-webkit-font-smoothing: antialiased;font-size: 14px;line-height: 1.4;margin: 0;padding: 0;-ms-text-size-adjust: 100%;-webkit-text-size-adjust: 100%;\"> <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"body\" style=\"border-collapse: separate;mso-table-lspace: 0pt;mso-table-rspace: 0pt;width: 100%;background-color: #fff;color: #787878;\"> <tr> <td style=\"font-family: sans-serif;font-size: 14px;vertical-align: top;\">&nbsp;</td> <td class=\"container\" style=\"font-family: sans-serif;font-size: 14px;vertical-align: top;display: block;max-width: 580px;padding: 10px;width: 580px;margin: 0 auto !important;\"> <div class=\"content\" style=\"box-sizing: border-box;display: block;margin: 0 auto;max-width: 580px;padding: 10px;\"> <div style=\"display: none; max-height: 0px; overflow: hidden;\"> &nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp; &nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp; &nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp; &nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp; &nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp; &nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp; &nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp; &nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp; &nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp; &nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp; &nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp; &nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp; &nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp; &nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp; &nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp; &nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp; &nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp; </div> <table role=\"presentation\" class=\"main\" style=\"border-collapse: separate;mso-table-lspace: 0pt;mso-table-rspace: 0pt;width: 100%;background: #ffffff;border-radius: 3px;\"> <tr> <td class=\"wrapper\" style=\"font-family: sans-serif;font-size: 14px;vertical-align: top;box-sizing: border-box;padding: 20px;\"> <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse: separate;mso-table-lspace: 0pt;mso-table-rspace: 0pt;width: 100%;\"> <img style=\"display: block; margin: 0 auto; border-radius: 50%; max-width: 25%; height: auto; margin-bottom: 2em;\" src=\"" + jda.getGuildById(Config.getGuildId()).getIconUrl() + "\" alt=\"Icon\" style=\"border: none;-ms-interpolation-mode: bicubic;max-width: 25%;display: block;margin: 0 auto;height: auto;margin-bottom: 1.5em;\"> <tr> <td style=\"font-family: sans-serif;font-size: 14px;vertical-align: top;\"> <h1 style=\"color: #000;font-family: sans-serif;font-weight: 600;line-height: 1.4;margin: 0;margin-bottom: 30px;font-size: 25px;text-align: center;text-transform: capitalize;color: #000;\">Welcome to " + jda.getGuildById(Config.getGuildId()).getName() + "!</h1> <center><p style=\"font-family: sans-serif;font-size: 14px;font-weight: normal;margin: 0;margin-bottom: 15px;\">You have purchased a one " + plan.getInterval().toLowerCase() + " membership key to " + jda.getGuildById(Config.getGuildId()).getName() + ".</p> <p style=\"font-family: sans-serif;font-size: 14px;font-weight: normal;margin: 0;margin-bottom: 15px;\">To activate your membership and gain access to the channels, DM <strong>" + jda.getSelfUser().getName() + "#" + jda.getSelfUser().getDiscriminator() + "</strong> the message below.</p> <p style=\"font-family: sans-serif;font-size: 14px;font-weight: normal;margin: 0;margin-bottom: 15px;\">This payment is auto-recurring. The serial is valid up until this subscription is cancelled.</p> <div style=\"text-align: center; border-radius: 4px; padding: 5px; background-color: #f8f8f8; margin-bottom: 2em; text-decoration:none !important;font-family: Courier New;\"> /redeem <a style=\"color: #787878;font-family: Courier New;\">" + key + "</a></div><h6>The transferable serial key above grants ONE Discord account access to the " + jda.getGuildById(Config.getGuildId()).getName() + " server. For more information about recurring payments, transferring, or cancelling, please DM the bot /help or DM a staff member.</h6></center> </div> <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"btn btn-primary\" style=\"border-collapse: separate;mso-table-lspace: 0pt;mso-table-rspace: 0pt;width: 100%;box-sizing: border-box;margin: 0 auto;\"> <tbody> <tr> <td align=\"left\" style=\"font-family: sans-serif;font-size: 14px;vertical-align: top;padding-bottom: 15px;\"> <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse: separate;mso-table-lspace: 0pt;mso-table-rspace: 0pt; margin: 0 auto;\"> <tbody> <tr><td style=\"font-family: Trebuchet MS;font-size: 14px;vertical-align: top;border-radius: 5px;text-align: center;\"> <a href=\"" + link.getURL() + "\" target=\"_blank\" style=\"color: #ffffff;text-decoration: none;background-color: #7289da;border: none;border-radius: 5px;box-sizing: border-box;cursor: pointer;display: inline-block;font-size: 14px;font-weight: bold;margin: 0 auto;padding: 12px 25px;text-transform: capitalize;\">Join " + jda.getGuildById(Config.getGuildId()).getName() + "</a> </td> </tr> </tbody> </table> </td> </tr> </tbody> </table> </td> </tr> </table> </td> </tr> </table> </div> </td> <td style=\"font-family: sans-serif;font-size: 14px;vertical-align: top;\">&nbsp;</td> </tr> </table></body>"
                    ).buildEmail();
                    MailerBuilder.withSMTPServer("smtp.gmail.com", 587, "verifus.auth@gmail.com", "Verifus168").buildMailer().sendMail(email);
                    LOGGER.log(Level.INFO, "invoice.payment_succeeded received. Key " + key + " generated. Email sent to " + customerEmail);
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
