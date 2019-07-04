package us.verif.bot;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import us.verif.bot.sql.Sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PeriodicCheck implements Runnable {
    private JDA jda;
    private final static Logger LOGGER = Logger.getLogger(PeriodicCheck.class.getName());


    public PeriodicCheck(JDA jda) {
        this.jda = jda;
    }


    public void run() {
        try {
            Connection connection = DataSource.getDataSource().getConnection();
            connection.setCatalog("verifus");
            PreparedStatement preparedStatement = connection.prepareStatement("select * from `activatedservers` where `expireDate` <= NOW()");
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                String guildId = Config.getGuildId();
                User user = jda.getGuildById(guildId).getOwner().getUser();
                String server = jda.getGuildById(guildId).getName();
                user.openPrivateChannel().queue((channel) -> channel.sendMessage("Your Verifus activation for your server `" + server + "` has expired.").queue());
            }
            Sql.deleteExpiredGuilds();

            connection.setCatalog(Config.getGuildId());
            PreparedStatement preparedStatement1 = connection.prepareStatement("select * from `verifiedusers` where `expireDate` <= NOW()");
            ResultSet rs1 = preparedStatement1.executeQuery();
            while (rs1.next()) {
                User user = jda.getUserById(rs1.getString("discordId"));
                Guild guild = jda.getGuildById(Config.getGuildId());
                String role = rs1.getString("roleId");
                try {
                    user.openPrivateChannel().queue((channel) -> channel.sendMessage("Your role `" + jda.getRoleById(role).getName() + "` has expired.").queue());
                    guild.getController().removeSingleRoleFromMember(guild.getMemberById(rs1.getString("discordId")), jda.getRoleById(role)).queue();
                } catch (NullPointerException npe) {
                    LOGGER.log(Level.INFO, "Error occurred while removing classic role: " + user + " is not in the server anymore, or the role does not exist anymore.");
                }
            }
            Sql.deleteExpiredUsers();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
