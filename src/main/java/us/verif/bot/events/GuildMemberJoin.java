package us.verif.bot.events;

import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import us.verif.bot.sql.ActivationDatabase;
import us.verif.bot.sql.Sql;

public class GuildMemberJoin extends ListenerAdapter {
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {

        if (ActivationDatabase.isActivated()) {

            if (Sql.userExistsInDatabaseWithGuild(event.getUser().getId())) {
                for (String role : Sql.getUserRoles(event.getUser().getId())) {
                    Role finalRole = event.getGuild().getRoleById(role);
                    event.getGuild().getController().addSingleRoleToMember(event.getMember(), finalRole).queue();
                }
            }
        }
    }
}
