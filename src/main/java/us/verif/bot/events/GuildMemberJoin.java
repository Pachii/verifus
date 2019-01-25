package us.verif.bot.events;

import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import us.verif.bot.Sql;

public class GuildMemberJoin extends ListenerAdapter {
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {

        if (Sql.activatedServersHas(event.getGuild().getId())) {
            
            if (Sql.userExistsInDatabaseWithGuild(event.getUser().getId(), event.getGuild().getId())) {
                for (String role : Sql.getUserRoles(event.getGuild().getId(), event.getUser().getId())) {
                    Role finalRole = event.getGuild().getRolesByName(role, false).get(0);
                    event.getGuild().getController().addRolesToMember(event.getMember(), finalRole).queue();
                }
            }
        }
    }
}
