package us.verif.bot;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import us.verif.bot.commands.*;
import us.verif.bot.events.GuildMemberJoin;
import us.verif.bot.events.WhenJoinGuild;

import java.util.Timer;

public class Bot {

    static final String url = "jdbc:mysql://localhost:3306/verifus?useSSL=false&allowPublicKeyRetrieval=true";
    static final String user = "root";
    static final String password = "Verifus168";

    public static final String version = "0.6.6 β";

    public static void main(String[] args) throws Exception {

        Config.createConfigFile();

        JDA api = new JDABuilder(AccountType.BOT)
                .setToken(Config.getToken())
                .build();

        Sql.connect();

        CommandClientBuilder builder = new CommandClientBuilder();
        builder.setPrefix("/");
        builder.addCommand(new SerialCreation()).addCommand(new GenerateKeys()).addCommand(new BotActivation()).addCommand(new Auth()).addCommand(new Revoke()).addCommand(new Remove()).addCommand(new Add()).addCommand(new Help()).addCommand(new Check()).addCommand(new Database());
        builder.setOwnerId("426839909421154314");
        builder.setGame(Game.of(Game.GameType.LISTENING, "/help"));
        builder.setHelpWord("unusedhelp");
        CommandClient commands = builder.build();

        api.addEventListener(commands, new WhenJoinGuild(), new GuildMemberJoin());

        Helpers.sleep(1000);

        Timer timer = new Timer();
        timer.schedule(new PeriodicCheck(api), 0, 5000);
    }
}

