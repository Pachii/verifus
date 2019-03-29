package us.verif.bot.Stripe.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import us.verif.bot.Config;
import us.verif.bot.sql.ActivationDatabase;
import us.verif.bot.sql.StripeSql;

import java.sql.SQLException;

public class Redeem extends Command {

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
            if (StripeSql.containsStripeKey(key)) {
                if (!StripeSql.keyRegistered(key)) {
                    if (ActivationDatabase.isActivated()) {
                        Guild guild = jda.getGuildById(Config.getGuildId());
                        Member member = guild.getMemberById(event.getAuthor().getId());
                        Role role = guild.getRoleById(StripeSql.getRoleByStripeKey(key));
                        try {
                            guild.getController().addSingleRoleToMember(member, role).queue();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                        StripeSql.registerStripeUser(event.getAuthor().getId(), key);
                        event.reply("Your key for the role `" + role.getName() + "` in `" + guild.getName() + "` has been tied to this Discord account.");
                    }
                } else if (StripeSql.getUserByStripeKey(key).equals(event.getAuthor().getId())) {
                    if (ActivationDatabase.isActivated()) {
                        Guild guild = jda.getGuildById(Config.getGuildId());
                        Member member = guild.getMemberById(event.getAuthor().getId());
                        Role role = guild.getRoleById(StripeSql.getRoleByStripeKey(key));
                        try {
                            guild.getController().addSingleRoleToMember(member, role).queue();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                        event.reply("Your key for the role `" + role.getName() + "` in `" + guild.getName() + "` has already been tied to this Discord account. If the role was manually deleted for some reason," +
                                " it was added back.");
                    }
                }
                else {
                    event.reply("This key is tied to another Discord account.");
                }
            } else {
                event.reply("This key does not exist.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
