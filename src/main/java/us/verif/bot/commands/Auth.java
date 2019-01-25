package us.verif.bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Role;
import us.verif.bot.Helpers;
import us.verif.bot.Sql;

import java.text.SimpleDateFormat;
import java.util.*;


public class Auth extends Command {

    private JDA jda;

    public Auth() {
        super.name = "auth";
    }

    @Override
    protected void execute(CommandEvent event) {

        final String inputtedKey = event.getArgs();

        if (Sql.activatedServersHas(event.getGuild().getId())) {
            if (Sql.containsServerKey(event.getGuild().getId()) && Sql.keyIsForGuild(inputtedKey, event.getGuild().getId())) {
                if (event.getArgs().isEmpty()) return;

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date today = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(today);
                String storedTime = Sql.getKeyTime(inputtedKey);
                int amount;
                try {
                    amount = Integer.parseInt(storedTime.split(" ")[0]);
                } catch (Exception e) {

                    return;
                }
                Date expireDate = null;
                String role = Sql.getKeyRole(inputtedKey);
                switch (storedTime.split(" ")[1]) {
                    case "SECOND":
                        if (Sql.userExistsInDatabaseWithGuildRole(event.getAuthor().getId(), event.getGuild().getId(), role)) {
                            cal.add(Calendar.SECOND, amount);
                            expireDate = cal.getTime();
                            Sql.execute("Update", "update `verifiedusers` set `expireDate` = '" + dateFormat.format(expireDate) + "' where `guildID` = '" + event.getGuild().getId() + "' and `userID` = '" + event.getAuthor().getId() + "' and `role` = '" + role + "';");

                        } else {
                            cal.add(Calendar.SECOND, amount);
                            expireDate = cal.getTime();
                            Sql.execute("Update", "insert into `verifiedusers` (`userID`,`guildID`,`role`,`expireDate`) values ('" + event.getAuthor().getId() + "','" + event.getGuild().getId() + "','" + Sql.getKeyRole(inputtedKey) + "','" + dateFormat.format(expireDate) + "');");

                        }
                        break;
                    case "MINUTE":
                        if (Sql.userExistsInDatabaseWithGuildRole(event.getAuthor().getId(), event.getGuild().getId(), role)) {
                            System.out.println("exists");
                            cal.setTime(Sql.getUserExpireDate(event.getAuthor().getId(), event.getGuild().getId()));
                            cal.add(Calendar.MINUTE, amount);
                            expireDate = cal.getTime();
                            Sql.execute("Update", "update `verifiedusers` set `expireDate` = '" + dateFormat.format(expireDate) + "' where `guildID` = '" + event.getGuild().getId() + "' and `userID` = '" + event.getAuthor().getId() + "' and `role` = '" + role + "';");

                        } else {
                            cal.add(Calendar.MINUTE, amount);
                            expireDate = cal.getTime();
                            Sql.execute("Update", "insert into `verifiedusers` (`userID`,`guildID`,`role`,`expireDate`) values ('" + event.getAuthor().getId() + "','" + event.getGuild().getId() + "','" + Sql.getKeyRole(inputtedKey) + "','" + dateFormat.format(expireDate) + "');");

                        }
                        break;
                    case "HOUR":
                        if (Sql.userExistsInDatabaseWithGuildRole(event.getAuthor().getId(), event.getGuild().getId(), role)) {
                            cal.setTime(Sql.getUserExpireDate(event.getAuthor().getId(), event.getGuild().getId()));
                            cal.add(Calendar.HOUR, amount);
                            expireDate = cal.getTime();
                            Sql.execute("Update", "update `verifiedusers` set `expireDate` = '" + dateFormat.format(expireDate) + "' where `guildID` = '" + event.getGuild().getId() + "' and `userID` = '" + event.getAuthor().getId() + "' and `role` = '" + role + "';");

                        } else {
                            cal.add(Calendar.HOUR, amount);
                            expireDate = cal.getTime();
                            Sql.execute("Update", "insert into `verifiedusers` (`userID`,`guildID`,`role`,`expireDate`) values ('" + event.getAuthor().getId() + "','" + event.getGuild().getId() + "','" + Sql.getKeyRole(inputtedKey) + "','" + dateFormat.format(expireDate) + "');");

                        }
                        break;
                    case "DAY":
                        if (Sql.userExistsInDatabaseWithGuildRole(event.getAuthor().getId(), event.getGuild().getId(), role)) {
                            cal.setTime(Sql.getUserExpireDate(event.getAuthor().getId(), event.getGuild().getId()));
                            cal.add(Calendar.DATE, amount);
                            expireDate = cal.getTime();
                            Sql.execute("Update", "update `verifiedusers` set `expireDate` = '" + dateFormat.format(expireDate) + "' where `guildID` = '" + event.getGuild().getId() + "' and `userID` = '" + event.getAuthor().getId() + "' and `role` = '" + role + "';");

                        } else {
                            cal.add(Calendar.DATE, amount);
                            expireDate = cal.getTime();
                            Sql.execute("Update", "insert into `verifiedusers` (`userID`,`guildID`,`role`,`expireDate`) values ('" + event.getAuthor().getId() + "','" + event.getGuild().getId() + "','" + Sql.getKeyRole(inputtedKey) + "','" + dateFormat.format(expireDate) + "');");

                        }
                        break;
                    case "MONTH":
                        if (Sql.userExistsInDatabaseWithGuildRole(event.getAuthor().getId(), event.getGuild().getId(), role)) {
                            cal.setTime(Sql.getUserExpireDate(event.getAuthor().getId(), event.getGuild().getId()));
                            cal.add(Calendar.MONTH, amount);
                            expireDate = cal.getTime();
                            Sql.execute("Update", "update `verifiedusers` set `expireDate` = '" + dateFormat.format(expireDate) + "' where `guildID` = '" + event.getGuild().getId() + "' and `userID` = '" + event.getAuthor().getId() + "' and `role` = '" + role + "';");

                        } else {
                            cal.add(Calendar.MONTH, amount);
                            expireDate = cal.getTime();
                            Sql.execute("Update", "insert into `verifiedusers` (`userID`,`guildID`,`role`,`expireDate`) values ('" + event.getAuthor().getId() + "','" + event.getGuild().getId() + "','" + Sql.getKeyRole(inputtedKey) + "','" + dateFormat.format(expireDate) + "');");

                        }
                        break;
                    case "YEAR":
                        if (Sql.userExistsInDatabaseWithGuildRole(event.getAuthor().getId(), event.getGuild().getId(), role)) {
                            cal.setTime(Sql.getUserExpireDate(event.getAuthor().getId(), event.getGuild().getId()));
                            cal.add(Calendar.YEAR, amount);
                            expireDate = cal.getTime();
                            Sql.execute("Update", "update `verifiedusers` set `expireDate` = '" + dateFormat.format(expireDate) + "' where `guildID` = '" + event.getGuild().getId() + "' and `userID` = '" + event.getAuthor().getId() + "' and `role` = '" + role + "';");

                        } else {
                            cal.add(Calendar.YEAR, amount);
                            expireDate = cal.getTime();
                            Sql.execute("Update", "insert into `verifiedusers` (`userID`,`guildID`,`role`,`expireDate`) values ('" + event.getAuthor().getId() + "','" + event.getGuild().getId() + "','" + Sql.getKeyRole(inputtedKey) + "','" + dateFormat.format(expireDate) + "');");

                        }
                        break;
                    case "LIFETIME":
                        cal.add(Calendar.YEAR, 200);
                        expireDate = cal.getTime();
                        Sql.execute("Update", "insert into `verifiedusers` (`userID`,`guildID`,`role`,`expireDate`) values ('" + event.getAuthor().getId() + "','" + event.getGuild().getId() + "','" + Sql.getKeyRole(inputtedKey) + "','" + dateFormat.format(expireDate) + "');");
                        break;
                }

                Role keyRole = event.getGuild().getRolesByName(Sql.getKeyRole(inputtedKey), true).get(0);
                event.getGuild().getController().addSingleRoleToMember(event.getMember(), keyRole).queue();

                Helpers.sendPrivateMessage(event.getAuthor(), "RECEIPT: You used the one-time activation key `" + inputtedKey +
                        "` to gain access to `" + event.getGuild().getName() + "` with the role `" + Sql.getKeyRole(inputtedKey) + "`. Your `" + storedTime + "` activation will expire on `" + dateFormat.format(expireDate) + "`.");

                Sql.execute("Update", "delete from `serverkeys` where `key` = '" + inputtedKey + "';");
            } else {
                event.reply("The entered key either does not exist or is already used.");
            }
        }
    }
}
