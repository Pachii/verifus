package us.verif.bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.JDA;

public class Revoke extends Command {
    private JDA jda;

    public Revoke() {
        super.name = "revoke";
        super.ownerCommand = true;
        super.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        String guildID = event.getArgs();
        event.replyInDm("Server `" + jda.getGuildById(guildID) + "`'s activation has been revoked.");
    }
}
