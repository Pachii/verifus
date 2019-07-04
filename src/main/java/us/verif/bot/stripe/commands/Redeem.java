package us.verif.bot.stripe.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import us.verif.bot.Bot;
import us.verif.bot.Config;
import us.verif.bot.sql.ActivationDatabase;
import us.verif.bot.sql.StripeSql;

import java.sql.SQLException;

public class Redeem extends Command {

    private final static Logger LOGGER = Logger.getLogger(Bot.class.getName());
    private JDA jda;


    public Redeem(JDA jda) {
        this.jda = jda;
        super.name = "redeem";
        super.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {

        String key = event.getArgs();

        try {
            if (ActivationDatabase.isActivated()) {
                if (StripeSql.containsStripeKey(key)) {
                    if (!StripeSql.keyRegistered(key)) {
                        Guild guild = jda.getGuildById(Config.getGuildId());
                        Member member = guild.getMemberById(event.getAuthor().getId());
                        Role role;
                        try {
                            role = guild.getRoleById(StripeSql.getRoleByStripeKey(key));
                        } catch (NullPointerException npe) {
                            LOGGER.log(Level.WARN, event.getAuthor() + " tried to tie the key " + key + " but the role ID of the key does not exist (" + StripeSql.getRoleByStripeKey(key) + "). The key has been removed from the database.");
                            StripeSql.removeStripeKey(key);
                            return;
                        } catch (NumberFormatException nfe) {
                            LOGGER.log(Level.WARN, event.getAuthor() + " tried to tie the key " + key + " but the role of the key is in an invalid format (" + StripeSql.getRoleByStripeKey(key) + "). The key has been removed from the database.");
                            StripeSql.removeStripeKey(key);
                            return;
                        }
                        try {
                            guild.getController().addSingleRoleToMember(member, role).queue();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                        StripeSql.registerStripeUser(event.getAuthor().getId(), key);
                        event.reply("Your key for the role `" + role.getName() + "` in `" + guild.getName() + "` has been tied to this Discord account.");
                        LOGGER.log(Level.INFO, event.getAuthor() + " tied the key " + key + " for the role " + role);
                    } else if (StripeSql.getUserByStripeKey(key).equals(event.getAuthor().getId())) {
                        Guild guild = jda.getGuildById(Config.getGuildId());
                        Member member = guild.getMemberById(event.getAuthor().getId());
                        Role role;
                        try {
                            role = guild.getRoleById(StripeSql.getRoleByStripeKey(key));
                        } catch (NullPointerException npe) {
                            LOGGER.log(Level.WARN, event.getAuthor() + " tried to tie the key " + key + " but the role ID of the key does not exist (" + StripeSql.getRoleByStripeKey(key) + "). The key has been removed from the database.");
                            StripeSql.removeStripeKey(key);
                            return;
                        } catch (NumberFormatException nfe) {
                            LOGGER.log(Level.WARN, event.getAuthor() + " tried to tie the key " + key + " but the role of the key is in an invalid format (" + StripeSql.getRoleByStripeKey(key) + "). The key has been removed from the database.");
                            StripeSql.removeStripeKey(key);
                            return;
                        }
                        try {
                            guild.getController().addSingleRoleToMember(member, role).queue();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                        event.reply("Your key for the role `" + role.getName() + "` in `" + guild.getName() + "` has already been tied to this Discord account. If the role was manually deleted for some reason, it was added back.");
                        LOGGER.log(Level.INFO, event.getAuthor() + " tied the key " + key + " that was already tied to his account.");
                    } else {
                        event.reply("This key is tied to another Discord account.");
                        LOGGER.log(Level.INFO, event.getAuthor() + " failed to tie the key " + key + " because it is already tied to " + jda.getUserById(StripeSql.getUserByStripeKey(key)));
                    }
                } else {
                    event.reply("This key does not exist.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
