package us.verif.bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import us.verif.bot.Helpers;
import us.verif.bot.Sql;

import java.text.SimpleDateFormat;
import java.util.*;

import static net.dv8tion.jda.core.Permission.*;

public class BotActivation extends Command {
    static Role everyone;
    private JDA jda;

    public BotActivation() {
        super.name = "activate";
    }

    @Override
    protected void execute(CommandEvent event) {
        String serial = event.getArgs();
        if (Sql.activationSerialsHas(serial)) {
            if (event.getGuild().getOwnerId().equals(event.getAuthor().getId()) || event.getGuild().getMember(event.getAuthor()).hasPermission(ADMINISTRATOR)) {
                if(event.getArgs().isEmpty()) return;
                Date today = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(today);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String serialTime = Sql.getActivationSerialTime(serial);
                int amount;
                amount = Integer.parseInt(serialTime.split(" ")[0]);
                String time = serialTime.split(" ")[1];
                Date expireDate = null;
                switch (time) {
                    case "MINUTE":
                        if (Sql.activatedServersHas(event.getGuild().getId())) {
                            cal.setTime(Sql.activationExpireDate(event.getGuild().getId()));
                            cal.add(Calendar.MINUTE, amount);
                            expireDate = cal.getTime();
                            Sql.execute("Update", "update `activatedservers` set `expireDate` = '" + dateFormat.format(expireDate) + "' where `guildID` = '" + event.getGuild().getId() + "';");

                        } else {
                            cal.add(Calendar.MINUTE, amount);
                            expireDate = cal.getTime();
                            Sql.execute("Update", "insert into `activatedservers` (`guildID`,`expireDate`) values ('" + event.getGuild().getId() + "','" + dateFormat.format(expireDate) + "');");

                        }
                        break;
                    case "HOUR":
                        if (Sql.activatedServersHas(event.getGuild().getId())) {
                            cal.setTime(Sql.activationExpireDate(event.getGuild().getId()));
                            cal.add(Calendar.HOUR, amount);
                            expireDate = cal.getTime();
                            Sql.execute("Update", "update `activatedservers` set `expireDate` = '" + dateFormat.format(expireDate) + "' where `guildID` = '" + event.getGuild().getId() + "';");

                        } else {
                            cal.add(Calendar.HOUR, amount);
                            expireDate = cal.getTime();
                            Sql.execute("Update", "insert into `activatedservers` (`guildID`,`expireDate`) values ('" + event.getGuild().getId() + "','" + dateFormat.format(expireDate) + "');");

                        }
                        break;
                    case "DAY":
                        if (Sql.activatedServersHas(event.getGuild().getId())) {
                            cal.setTime(Sql.activationExpireDate(event.getGuild().getId()));
                            cal.add(Calendar.DATE, amount);
                            expireDate = cal.getTime();
                            Sql.execute("Update", "update `activatedservers` set `expireDate` = '" + dateFormat.format(expireDate) + "' where `guildID` = '" + event.getGuild().getId() + "';");

                        } else {
                            cal.add(Calendar.DATE, amount);
                            expireDate = cal.getTime();
                            Sql.execute("Update", "insert into `activatedservers` (`guildID`,`expireDate`) values ('" + event.getGuild().getId() + "','" + dateFormat.format(expireDate) + "');");

                        }
                        break;
                    case "MONTH":
                        if (Sql.activatedServersHas(event.getGuild().getId())) {
                            cal.setTime(Sql.activationExpireDate(event.getGuild().getId()));
                            cal.add(Calendar.MONTH, amount);
                            expireDate = cal.getTime();
                            Sql.execute("Update", "update `activatedservers` set `expireDate` = '" + dateFormat.format(expireDate) + "' where `guildID` = '" + event.getGuild().getId() + "';");

                        } else {
                            cal.add(Calendar.MONTH, amount);
                            expireDate = cal.getTime();
                            Sql.execute("Update", "insert into `activatedservers` (`guildID`,`expireDate`) values ('" + event.getGuild().getId() + "','" + dateFormat.format(expireDate) + "');");

                        }
                        break;
                    case "YEAR":
                        if (Sql.activatedServersHas(event.getGuild().getId())) {
                            cal.setTime(Sql.activationExpireDate(event.getGuild().getId()));
                            cal.add(Calendar.YEAR, amount);
                            expireDate = cal.getTime();
                            Sql.execute("Update", "update `activatedservers` set `expireDate` = '" + dateFormat.format(expireDate) + "' where `guildID` = '" + event.getGuild().getId() + "';");

                        } else {
                            cal.add(Calendar.YEAR, amount);
                            expireDate = cal.getTime();
                            Sql.execute("Update", "insert into `activatedservers` (`guildID`,`expireDate`) values ('" + event.getGuild().getId() + "','" + dateFormat.format(expireDate) + "');");

                        }
                        break;
                    case "LIFETIME":
                        cal.add(Calendar.YEAR, 200);
                        expireDate = cal.getTime();
                        Sql.execute("Update", "insert into `activatedservers` (`guildID`,`expireDate`) values ('" + event.getGuild().getId() + "','" + dateFormat.format(expireDate) + "');");

                        break;
                }

                event.reply("Bot activation successful. Your `" + amount + " " + time + "` activation will expire on `" + dateFormat.format(expireDate) + "`.");
                event.replyInDm("RECEIPT: You used the one-time activation serial `" + serial + "` to activate your server `" + event.getGuild().getName() + "`. Your `" + amount + " " + time + "` activation will expire on `" + dateFormat.format(expireDate) + "`.");
                Sql.execute("Update", "delete from `activationserials` where `serial` = '" + serial + "';");

                //first time setup
                if (event.getGuild().getTextChannelsByName("verification", true).isEmpty()) {
                    Helpers.sleep(1000);
                    event.getChannel().sendMessage("Performing setup.").queue();
                    Helpers.sleep(1000);
                    everyone = event.getGuild().getRolesByName("@everyone", true).get(0);
                    everyone.getManager().setPermissions(CREATE_INSTANT_INVITE).queue();
                    Set<Permission> addPerms = new HashSet<>(Arrays.asList(VIEW_CHANNEL, MESSAGE_WRITE));
                    Set<Permission> removePerms = new HashSet<>(Arrays.asList(MESSAGE_HISTORY));
                    event.getGuild().getController().createTextChannel("verification").setTopic("/auth XXXXXX-XXXXXX-XXXXXX-XXXXXX-XXXXXX").complete();
                    event.getGuild().getTextChannelsByName("verification", true).get(0).getManager().putPermissionOverride(everyone, addPerms, removePerms).queue();
                    TextChannel verification = event.getGuild().getTextChannelsByName("verification", true).get(0);
                    event.getGuild().getManager().setSystemChannel(verification).queue();
                    event.getChannel().sendMessage("Done.").queue();
                    event.getChannel().sendMessage("Everything is setup and automated at this point. Please use the command `/help` for information for more commands." +
                            "You need to create the roles that you would like yourself and change the permissions of the roles in the text channels to suit your needs." +
                            "The default role is `@ everyone` which can only see the `#verification` channel. For server admin commands, type `/help 2`.").queue();
                }
            }
        }
    }
}
