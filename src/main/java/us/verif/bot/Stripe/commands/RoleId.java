package us.verif.bot.Stripe.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class RoleId extends Command {

    public RoleId() {
        super.name = "roleid";
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            String roleId = event.getGuild().getRolesByName(event.getArgs(), true).get(0).getId();
            event.reply("The ID for the role " + event.getArgs() + " is: " + roleId);
        } catch (Throwable e) {
            event.reply("The role does not exist.");
        }
    }
}
