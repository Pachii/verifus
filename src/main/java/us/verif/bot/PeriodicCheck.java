package us.verif.bot;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import us.verif.bot.sql.Sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PeriodicCheck implements Runnable {
    private JDA jda;

    public PeriodicCheck(JDA jda) {
        this.jda = jda;
    }


    public void run() {
        try {
            Connection connection = DataSource.getConnection();
            connection.setCatalog("verifus");
            PreparedStatement preparedStatement = connection.prepareStatement("select * from `activatedservers` where `expireDate` <= NOW()");
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                String guildId = Config.getGuildId();
                User user = jda.getGuildById(guildId).getOwner().getUser();
                String server = jda.getGuildById(guildId).getName();
                user.openPrivateChannel().queue((channel) -> channel.sendMessage("Your Verifus activation for your server `" + server + "` has expired. \nVisit https://verif.us/ to purchase.").queue());
            }
            connection.close();
            Sql.deleteExpiredGuilds();

            Connection connection1 = DataSource.getConnection();
            connection1.setCatalog(Config.getGuildId());
            PreparedStatement preparedStatement1 = connection1.prepareStatement("select * from `verifiedusers` where `expireDate` <= NOW()");
            ResultSet rs1 = preparedStatement1.executeQuery();
            while (rs1.next()) {
                User user = jda.getUserById(rs1.getString("discordId"));
                String serverName = jda.getGuildById(Config.getGuildId()).getName();
                Guild guild = jda.getGuildById(Config.getGuildId());
                String role = rs1.getString("roleId");
                user.openPrivateChannel().queue((channel) -> channel.sendMessage("Your role `" + jda.getRoleById(role).getName() + "` in the server `" + serverName + "` has expired.").queue());
                guild.getController().removeSingleRoleFromMember(guild.getMemberById(rs1.getString("discordId")), jda.getRoleById(role)).queue();
            }
            connection1.close();
            Sql.deleteExpiredUsers();

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
