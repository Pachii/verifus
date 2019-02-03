package us.verif.bot;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

import java.sql.*;
import java.util.Collection;
import java.util.TimerTask;

public class PeriodicCheck extends TimerTask {
    private JDA jda;

    public PeriodicCheck(JDA jda) {
        this.jda = jda;
    }

    public void run() {

        try (Connection connection = DriverManager.getConnection(Bot.url, Bot.user, Bot.password); Statement statement = connection.createStatement()) {
            statement.executeUpdate("use verifus;");
            ResultSet rs = statement.executeQuery("select * from `activatedservers` where `expireDate` <= NOW();");
            while (rs.next()) {
                try {
                    String guildID = rs.getString("guildID");
                    User user = jda.getGuildById(guildID).getOwner().getUser();
                    String server = jda.getGuildById(guildID).getName();
                    user.openPrivateChannel().queue((channel) -> channel.sendMessage("Your Verifus activation for your server `" + server + "` has expired. \nVisit https://verif.us/ to purchase.").queue());
                } catch (NullPointerException e) {
                }
            }
            statement.executeUpdate("delete from `activatedservers` where `expireDate` <= NOW();");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (Connection connection = DriverManager.getConnection(Bot.url, Bot.user, Bot.password); Statement statement = connection.createStatement()) {
            statement.executeUpdate("use verifus;");
            ResultSet rs = statement.executeQuery("select * from `verifiedusers` where `expireDate` <= NOW();");
            while (rs.next()) {
                try {
                    User user = jda.getUserById(rs.getString("userID"));
                    String server = jda.getGuildById(rs.getString("guildID")).getName();
                    String role = rs.getString("role");
                    user.openPrivateChannel().queue((channel) -> channel.sendMessage("Your access for the server `" + server + "` has expired.").queue());
                    jda.getGuildById(rs.getString("guildID")).getController().removeSingleRoleFromMember(jda.getGuildById(rs.getString("guildID")).getMemberById(rs.getString("userID")), jda.getRolesByName(role, true).get(0)).queue();
                } catch (NullPointerException e) {
                }
            }
            statement.executeUpdate("delete from `verifiedusers` where `expireDate` <= NOW();");
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String key : Sql.stripeKeyList()) {
            try {
                Stripe.apiKey = key;
                Collection<String> subscriptions = Sql.getSubscriptions();

                for (String activeSubscription : subscriptions) {
                    Subscription subscription = Subscription.retrieve(activeSubscription);

                    if (subscription == null) continue;

                    if (subscription.getEndedAt() != null && subscription.getEndedAt() < System.currentTimeMillis() && !subscription.getStatus().equalsIgnoreCase("active")) {

                        String expiredUser = Sql.getDiscordIdByCustomer(subscription.getCustomer());
                        Helpers.sendPrivateMessage(jda.getUserById(expiredUser), "");
                        String guildId = Sql.getGuildFromStripeKey(key);
                        String role = subscription.getPlan().getNickname();
                        Role removeRole = jda.getGuildById(guildId).getRolesByName(role, true).get(0);
                        jda.getGuildById(guildId).getController().removeSingleRoleFromMember(jda.getGuildById(guildId).getMemberById(expiredUser), removeRole).queue();


                        Sql.setSubscriptionActive(subscription.getId(), false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


