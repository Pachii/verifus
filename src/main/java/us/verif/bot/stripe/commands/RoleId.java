package us.verif.bot.stripe.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class RoleId extends Command {

    private final static Logger LOGGER = Logger.getLogger(RoleId.class.getName());

    public RoleId() {
        super.name = "roleid";
    }

    @Override
    protected void execute(CommandEvent event) {
        LOGGER.log(Level.INFO, event.getAuthor() + " executed the command '" + event.getMessage().getContentRaw() + "'");
        try {
            String roleId = event.getGuild().getRolesByName(event.getArgs(), true).get(0).getId();
            event.reply("The ID for the role " + event.getArgs() + " is: " + roleId);
        } catch (Throwable e) {
            event.reply("The role does not exist.");
        }
    }
}
