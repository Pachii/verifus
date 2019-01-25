package us.verif.bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import us.verif.bot.Sql;

import static net.dv8tion.jda.core.Permission.ADMINISTRATOR;

public class Add extends Command {

    public Add() {
        super.name = "add";
    }

    @Override
    protected void execute(CommandEvent event) {
        if (Sql.activatedServersHas(event.getGuild().getId())) {
            if (event.getGuild().getOwnerId().equals(event.getAuthor().getId()) || event.getGuild().getMember(event.getAuthor()).hasPermission(ADMINISTRATOR)) {
                if(event.getArgs().isEmpty()) return;

                if (event.getArgs().split(":")[0].equalsIgnoreCase("all")) {
                    String role = event.getArgs().split(":")[1];
                    Role inputRole = event.getGuild().getRolesByName(role, true).get(0);
                    event.reply("Working...");
                    for (Member member : event.getGuild().getMembers()) {
                        if (!member.hasPermission(Permission.ADMINISTRATOR)) {
                            event.getGuild().getController().addSingleRoleToMember(member, inputRole).queue();
                        }
                    }
                    event.reply("Done.");
                }

                if (event.getArgs().split(" ")[0].equalsIgnoreCase("role")) {
                    String role = event.getArgs().split(":")[1];
                    Role inputRole = event.getGuild().getRolesByName(role, true).get(0);
                    Role roleToWork = event.getGuild().getRolesByName(event.getArgs().split(":")[1], true).get(0);
                    event.reply("Working...");
                    for (Member member : event.getGuild().getMembers()) {
                        if (member.getRoles().contains(inputRole) && !member.hasPermission(Permission.ADMINISTRATOR)) {
                            event.getGuild().getController().addSingleRoleToMember(member, roleToWork).queue();
                        }
                        event.reply("Done.");
                    }
                }

                if (event.getArgs().split(" ")[0].equalsIgnoreCase("member")) {
                    String role = event.getArgs().split(":")[1];
                    Role inputRole = event.getGuild().getRolesByName(role, true).get(0);
                    event.reply("Working...");
                    Member member = event.getMessage().getMentionedMembers().get(0);
                    event.getGuild().getController().addSingleRoleToMember(member, inputRole).queue();
                    event.reply("Done.");
                }
            }
        }
    }
}

