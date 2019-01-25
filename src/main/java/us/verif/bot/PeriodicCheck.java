package us.verif.bot;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
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
    }
}


