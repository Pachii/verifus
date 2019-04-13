package us.verif.bot.stripe.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import us.verif.bot.Config;
import us.verif.bot.sql.ActivationDatabase;
import us.verif.bot.sql.StripeSql;

import java.sql.SQLException;

public class Unbind extends Command {

    private JDA jda;
    private final static Logger LOGGER = Logger.getLogger(Unbind.class.getName());

    public Unbind(JDA jda) {
        this.jda = jda;
        super.name = "unbind";
        super.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {

        String key = event.getArgs();

        LOGGER.log(Level.INFO, event.getAuthor() + " executed the command '" + event.getMessage().getContentRaw() + "'");

        try {
            if (StripeSql.containsStripeKey(key)) {
                if (StripeSql.keyRegistered(key)) {
                    if (ActivationDatabase.isActivated() && StripeSql.getUserByStripeKey(key).equals(event.getAuthor().getId())) {
                        Guild guild = jda.getGuildById(Config.getGuildId());
                        Member member = guild.getMemberById(event.getAuthor().getId());
                        Role role = guild.getRoleById(StripeSql.getRoleByStripeKey(key));
                        try {
                            guild.getController().removeSingleRoleFromMember(member, role).queue();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                        StripeSql.unbindKey(event.getAuthor().getId(), event.getArgs());
                        event.reply("Your key `" + event.getArgs() +"` for the role `" + role.getName() + "` in `" + guild.getName() + "` has been unbound from this Discord account. You can now tie it to another account.");
                        LOGGER.log(Level.INFO, "The key " + key + " was unbound from " + member.getUser());
                    }
                } else {
                    event.reply("This key is not registered to an account yet.");
                }
            } else {
                event.reply("This key does not exist.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
