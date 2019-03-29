package us.verif.bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import us.verif.bot.Config;
import us.verif.bot.sql.ActivationDatabase;
import us.verif.bot.sql.Sql;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Add extends Command {
    private final EventWaiter waiter;
    private JDA jda;

    public Add(EventWaiter waiter, JDA jda) {
        this.jda = jda;
        this.waiter = waiter;
        super.name = "add";
    }

    @Override
    protected void execute(CommandEvent event) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (ActivationDatabase.isActivated()) {
            if (jda.getGuildById(Config.getGuildId()).getMemberById(event.getAuthor().getId()).hasPermission(Permission.ADMINISTRATOR)) {

                event.reply("@ the members that will be affected. Type `all` to select everyone in the server. Type `cancel` anytime to cancel.");
                waiter.waitForEvent(MessageReceivedEvent.class,
                        e -> e.getAuthor().equals(event.getAuthor()) && e.getChannel().equals(event.getChannel()),
                        e -> {
                            if (e.getMessage().getContentRaw().equals("cancel")) {
                                event.reply("Process canceled.");
                                return;
                            }
                            if (e.getMessage().getContentRaw().equalsIgnoreCase("all")) {
                                event.reply("Enter the time interval that the roles will last. Accepted intervals: `second` `minute` `hour` `day` `month` `year` `lifetime`");
                                waiter.waitForEvent(MessageReceivedEvent.class,
                                        e1 -> e1.getAuthor().equals(event.getAuthor()) && e1.getChannel().equals(event.getChannel()),
                                        e1 -> {
                                            if (e1.getMessage().getContentRaw().equals("cancel")) {
                                                event.reply("Process canceled.");
                                                return;
                                            }
                                            ArrayList<String> acceptedValues = new ArrayList<>();
                                            acceptedValues.add("second");
                                            acceptedValues.add("minute");
                                            acceptedValues.add("hour");
                                            acceptedValues.add("day");
                                            acceptedValues.add("month");
                                            acceptedValues.add("year");
                                            acceptedValues.add("lifetime");
                                            String interval = e1.getMessage().getContentRaw().toUpperCase();
                                            if (!acceptedValues.contains(interval.toLowerCase())) {
                                                event.reply("Error: Invalid interval. Process canceled.");
                                                return;
                                            }

                                            event.reply("Enter the number of `" + interval.toLowerCase() + "s` that the role will last for everyone.");
                                            waiter.waitForEvent(MessageReceivedEvent.class,
                                                    e2 -> e2.getAuthor().equals(event.getAuthor()) && e2.getChannel().equals(event.getChannel()),
                                                    e2 -> {
                                                        if (e2.getMessage().getContentRaw().equals("cancel")) {
                                                            event.reply("Process canceled.");
                                                            return;
                                                        }
                                                        int amount = Integer.parseInt(e2.getMessage().getContentRaw());

                                                        event.reply("Enter the role name you would like to add for everyone. Type `none` for no time.");
                                                        waiter.waitForEvent(MessageReceivedEvent.class,
                                                                e3 -> e3.getAuthor().equals(event.getAuthor()) && e3.getChannel().equals(event.getChannel()),
                                                                e3 -> {
                                                                    if (e3.getMessage().getContentRaw().equals("cancel")) {
                                                                        event.reply("Process canceled.");
                                                                        return;
                                                                    }
                                                                    String roleName = e3.getMessage().getContentRaw();
                                                                    Role inputRole = event.getGuild().getRolesByName(roleName, true).get(0);
                                                                    String role = inputRole.getId();
                                                                    event.reply("Working...");
                                                                    for (Member member : event.getGuild().getMembers()) {
                                                                        switchCheck(event, dateFormat, interval, amount, inputRole, role, member);
                                                                    }
                                                                    event.reply("Done.");
                                                                });
                                                    });
                                        });
                            }

                            if (!e.getMessage().getContentRaw().equalsIgnoreCase("all")) {
                                event.reply("Enter the time interval that the roles will last. Accepted intervals: `second` `minute` `hour` `day` `month` `year` `lifetime`");
                                waiter.waitForEvent(MessageReceivedEvent.class,
                                        e1 -> e1.getAuthor().equals(event.getAuthor()) && e1.getChannel().equals(event.getChannel()),
                                        e1 -> {
                                            if (e1.getMessage().getContentRaw().equals("cancel")) {
                                                event.reply("Process canceled.");
                                                return;
                                            }
                                            ArrayList<String> acceptedValues = new ArrayList<>();
                                            acceptedValues.add("second");
                                            acceptedValues.add("minute");
                                            acceptedValues.add("hour");
                                            acceptedValues.add("day");
                                            acceptedValues.add("month");
                                            acceptedValues.add("year");
                                            acceptedValues.add("lifetime");
                                            String interval = e1.getMessage().getContentRaw().toUpperCase();
                                            if (!acceptedValues.contains(interval.toLowerCase())) {
                                                event.reply("Error: Invalid interval. Process canceled.");
                                                return;
                                            }

                                            event.reply("Enter the number of `" + interval.toLowerCase() + "s` that the role will last for the selected members.");
                                            waiter.waitForEvent(MessageReceivedEvent.class,
                                                    e2 -> e2.getAuthor().equals(event.getAuthor()) && e2.getChannel().equals(event.getChannel()),
                                                    e2 -> {
                                                        if (e2.getMessage().getContentRaw().equals("cancel")) {
                                                            event.reply("Process canceled.");
                                                            return;
                                                        }
                                                        int amount = Integer.parseInt(e2.getMessage().getContentRaw());

                                                        event.reply("Enter the role name you would like to add for the selected members.");
                                                        waiter.waitForEvent(MessageReceivedEvent.class,
                                                                e3 -> e3.getAuthor().equals(event.getAuthor()) && e3.getChannel().equals(event.getChannel()),
                                                                e3 -> {
                                                                    if (e3.getMessage().getContentRaw().equals("cancel")) {
                                                                        event.reply("Process canceled.");
                                                                        return;
                                                                    }
                                                                    String roleName = e3.getMessage().getContentRaw();
                                                                    Role inputRole = event.getGuild().getRolesByName(roleName, true).get(0);
                                                                    String role = inputRole.getId();
                                                                    event.reply("Working...");
                                                                    for (Member member : e.getMessage().getMentionedMembers()) {
                                                                        switchCheck(event, dateFormat, interval, amount, inputRole, role, member);
                                                                    }
                                                                    event.reply("Done.");
                                                                });
                                                    });
                                        });

                            }
                        });
            }
        }
    }

    private void switchCheck(CommandEvent event, SimpleDateFormat dateFormat, String interval, int amount, Role inputRole, String role, Member member) {
        event.getGuild().getController().addSingleRoleToMember(member, inputRole).queue();
        Date expireDate;
        String guildID = event.getGuild().getId();
        Calendar cal = Calendar.getInstance();
        switch (interval) {
            case "SECOND":
                if (Sql.userExistsInDatabaseWithGuildRole(member.getUser().getId(), role)) {
                    cal.setTime(Sql.getUserExpireDate(member.getUser().getId()));
                    cal.add(Calendar.SECOND, amount);
                    expireDate = cal.getTime();
                    Sql.updateVerifiedUser(dateFormat.format(expireDate), event.getAuthor().getId(), role);

                } else {
                    cal.add(Calendar.SECOND, amount);
                    expireDate = cal.getTime();
                    Sql.updateVerifiedUser(dateFormat.format(expireDate), event.getAuthor().getId(), role);

                }
                break;
            case "MINUTE":
                if (Sql.userExistsInDatabaseWithGuildRole(member.getUser().getId(), role)) {
                    cal.setTime(Sql.getUserExpireDate(member.getUser().getId()));
                    cal.add(Calendar.MINUTE, amount);
                    expireDate = cal.getTime();
                    Sql.updateVerifiedUser(dateFormat.format(expireDate), event.getAuthor().getId(), role);

                } else {
                    cal.add(Calendar.MINUTE, amount);
                    expireDate = cal.getTime();
                    Sql.updateVerifiedUser(dateFormat.format(expireDate), event.getAuthor().getId(), role);

                }
                break;
            case "HOUR":
                if (Sql.userExistsInDatabaseWithGuildRole(member.getUser().getId(), role)) {
                    cal.setTime(Sql.getUserExpireDate(member.getUser().getId()));
                    cal.add(Calendar.HOUR, amount);
                    expireDate = cal.getTime();
                    Sql.updateVerifiedUser(dateFormat.format(expireDate), event.getAuthor().getId(), role);

                } else {
                    cal.add(Calendar.HOUR, amount);
                    expireDate = cal.getTime();
                    Sql.updateVerifiedUser(dateFormat.format(expireDate), event.getAuthor().getId(), role);

                }
                break;
            case "DAY":
                if (Sql.userExistsInDatabaseWithGuildRole(member.getUser().getId(), role)) {
                    cal.setTime(Sql.getUserExpireDate(member.getUser().getId()));
                    cal.add(Calendar.DATE, amount);
                    expireDate = cal.getTime();
                    Sql.updateVerifiedUser(dateFormat.format(expireDate), event.getAuthor().getId(), role);

                } else {
                    cal.add(Calendar.DATE, amount);
                    expireDate = cal.getTime();
                    Sql.updateVerifiedUser(dateFormat.format(expireDate), event.getAuthor().getId(), role);

                }
                break;
            case "MONTH":
                if (Sql.userExistsInDatabaseWithGuildRole(member.getUser().getId(), role)) {
                    cal.setTime(Sql.getUserExpireDate(member.getUser().getId()));
                    cal.add(Calendar.MONTH, amount);
                    expireDate = cal.getTime();
                    Sql.updateVerifiedUser(dateFormat.format(expireDate), event.getAuthor().getId(), role);

                } else {
                    cal.add(Calendar.MONTH, amount);
                    expireDate = cal.getTime();
                    Sql.updateVerifiedUser(dateFormat.format(expireDate), event.getAuthor().getId(), role);

                }
                break;
            case "YEAR":
                if (Sql.userExistsInDatabaseWithGuildRole(member.getUser().getId(), role)) {
                    cal.setTime(Sql.getUserExpireDate(member.getUser().getId()));
                    cal.add(Calendar.YEAR, amount);
                    expireDate = cal.getTime();
                    Sql.updateVerifiedUser(dateFormat.format(expireDate), event.getAuthor().getId(), role);

                } else {
                    cal.add(Calendar.YEAR, amount);
                    expireDate = cal.getTime();
                    Sql.updateVerifiedUser(dateFormat.format(expireDate), event.getAuthor().getId(), role);

                }
                break;
            case "LIFETIME":
                cal.add(Calendar.YEAR, 200);
                expireDate = cal.getTime();
                Sql.updateVerifiedUser(dateFormat.format(expireDate), event.getAuthor().getId(), role);
                break;
        }
    }
}

