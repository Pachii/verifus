package us.verif.bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import us.verif.bot.sql.ActivationDatabase;
import us.verif.bot.sql.Sql;

public class Remove extends Command {
    private final EventWaiter waiter;
    private final static Logger LOGGER = Logger.getLogger(Remove.class.getName());

    public Remove(EventWaiter waiter) {
        this.waiter = waiter;
        super.name = "remove";
    }

    @Override
    protected void execute(CommandEvent event) {
        LOGGER.log(Level.INFO, event.getAuthor() + " executed the command '" + event.getMessage().getContentRaw() + "'");
        if (ActivationDatabase.isActivated()) {
            if (event.getGuild().getMemberById(event.getAuthor().getId()).hasPermission(Permission.ADMINISTRATOR)) {

                event.reply("@ the members that will be affected. Type `all` to select everyone in the server. This command will also remove any authenticated members. Type `cancel` anytime to cancel.");
                waiter.waitForEvent(MessageReceivedEvent.class,
                        e -> e.getAuthor().equals(event.getAuthor()) && e.getChannel().equals(event.getChannel()),
                        e -> {
                            if (e.getMessage().getContentRaw().equals("cancel")) {
                                event.reply("process canceled.");
                                return;
                            }
                            if (e.getMessage().getContentRaw().equalsIgnoreCase("all")) {
                                event.reply("Enter the role name you would like to remove for everyone.");
                                waiter.waitForEvent(MessageReceivedEvent.class,
                                        e1 -> e1.getAuthor().equals(event.getAuthor()) && e1.getChannel().equals(event.getChannel()),
                                        e1 -> {
                                            if (e1.getMessage().getContentRaw().equals("cancel")) {
                                                event.reply("process canceled.");
                                                return;
                                            }
                                            String role = e1.getMessage().getContentRaw();
                                            Role inputRole = event.getGuild().getRolesByName(role, true).get(0);
                                            event.reply("Working...");
                                            for (Member member : event.getGuild().getMembers()) {
                                                if (member.getRoles().contains(inputRole)) {
                                                    event.getGuild().getController().removeSingleRoleFromMember(member, inputRole).queue();
                                                }
                                                if (Sql.userExistsInDatabaseWithGuildRole(member.getUser().getId(), inputRole.getId())) {
                                                    Sql.removeVerifiedUser(event.getAuthor().getId(), inputRole.getId());
                                                }
                                            }
                                            event.reply("Done.");
                                            LOGGER.log(Level.INFO, event.getAuthor() + " removed the role " + event.getGuild().getRoleById(role) + " to ALL.");

                                        });
                            }
                            if (!e.getMessage().getContentRaw().equalsIgnoreCase("all")) {
                                event.reply("Enter the role name you would like to remove for the mentioned members.");
                                waiter.waitForEvent(MessageReceivedEvent.class,
                                        e2 -> e2.getAuthor().equals(event.getAuthor()) && e2.getChannel().equals(event.getChannel()),
                                        e2 -> {
                                            if (e2.getMessage().getContentRaw().equals("cancel")) {
                                                event.reply("process canceled.");
                                                return;
                                            }
                                            String role = e2.getMessage().getContentRaw();
                                            Role inputRole = event.getGuild().getRolesByName(role, true).get(0);
                                            event.reply("Working...");
                                            for (Member member : e.getMessage().getMentionedMembers()) {
                                                if (member.getRoles().contains(inputRole)) {
                                                    event.getGuild().getController().removeSingleRoleFromMember(member, inputRole).queue();
                                                }
                                                if (Sql.userExistsInDatabaseWithGuildRole(member.getUser().getId(), inputRole.getId())) {
                                                    Sql.removeVerifiedUser(event.getAuthor().getId(), inputRole.getId());
                                                }
                                            }
                                            event.reply("Done.");
                                            LOGGER.log(Level.INFO, event.getAuthor() + " removed the role " + event.getGuild().getRoleById(role) + " to mentioned members.");
                                        });
                            }
                        });

            }
        }
    }
}
