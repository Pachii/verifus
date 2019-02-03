package us.verif.bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import us.verif.bot.Sql;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static net.dv8tion.jda.core.Permission.ADMINISTRATOR;
import static net.dv8tion.jda.core.Permission.MANAGE_SERVER;

public class Add extends Command {

    public Add() {
        super.name = "add";
    }

    @Override
    protected void execute(CommandEvent event) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();

        if (Sql.activatedServersHas(event.getGuild().getId())) {
            if (event.getGuild().getOwnerId().equals(event.getAuthor().getId()) || event.getGuild().getMember(event.getAuthor()).hasPermission(MANAGE_SERVER)) {
                if (event.getArgs().isEmpty()) return;

                if (event.getArgs().split(":")[0].equalsIgnoreCase("all")) {
                    String role = event.getArgs().split(":")[1];
                    Role inputRole = event.getGuild().getRolesByName(role, true).get(0);
                    event.reply("Working...");
                    for (Member member : event.getGuild().getMembers()) {
                        if (!member.hasPermission(Permission.ADMINISTRATOR)) {
                            event.getGuild().getController().addSingleRoleToMember(member, inputRole).queue();
                            if (!event.getArgs().split(":")[2].isEmpty()) {
                                /*String time = event.getArgs().split(":")[2];
                                String timeNum = time.split(" ")[0];
                                String timeString = time.split(" ")[1];
                                cal.setTime(new Date());
                                if (Sql.userExistsInDatabaseWithGuildRole(member.getUser().getId(), event.getGuild().getId(), role)) {
                                }
                                Sql.execute("Update", "update `verifiedusers` set `expireDate` = '" + dateFormat.format(expireDate) + "' where `guildID` = '" + guildID + "' and `userID` = '" + event.getAuthor().getId() + "' and `role` = '" + role + "';");
                                */
                            }
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
                            if (!event.getArgs().split(":")[2].isEmpty()) {
                                //todo split time and sql update
                            }
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
                    if (!event.getArgs().split(":")[2].isEmpty()) {
                        //todo split time and sql update
                    }
                    event.reply("Done.");
                }
            }
        }
    }
}

