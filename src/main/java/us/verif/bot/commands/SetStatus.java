package us.verif.bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Game;
import us.verif.bot.Config;
import us.verif.bot.sql.ActivationDatabase;
import us.verif.bot.sql.Sql;

public class SetStatus extends Command {
    private JDA jda;

    public SetStatus(JDA jda) {
        this.jda = jda;
        super.name = "setstatus";
        super.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (ActivationDatabase.isActivated() && jda.getGuildById(Config.getGuildId()).getMemberById(event.getAuthor().getId()).hasPermission(Permission.ADMINISTRATOR)) {
            Sql.setBotStatus(event.getArgs());
            jda.getPresence().setGame(Game.of(Game.GameType.STREAMING, event.getArgs()));
            event.reply("Status changed successfully.");
        }
    }
}
