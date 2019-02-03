package us.verif.bot;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import us.verif.bot.commands.*;
import us.verif.bot.commands.Stripe.Bind;
import us.verif.bot.commands.Stripe.CreatePlan;
import us.verif.bot.commands.Stripe.CreateProduct;
import us.verif.bot.commands.Stripe.StripeKey;
import us.verif.bot.events.GuildMemberJoin;
import us.verif.bot.events.WhenJoinGuild;

import java.util.Timer;

public class Bot {



    public static final String version = "0.8.0 β";
    static final String url = "jdbc:mysql://localhost:3306/verifus?useSSL=false&allowPublicKeyRetrieval=true";
    static final String user = "root";
    static final String password = "Verifus168";

    public static void main(String[] args) throws Exception {
        EventWaiter waiter = new EventWaiter();

        Config.createConfigFile();

        JDA api = new JDABuilder(AccountType.BOT)
                .setToken(Config.getToken())
                .addEventListener(waiter)
                .build();



        Sql.connect();

        CommandClientBuilder builder = new CommandClientBuilder();
        builder.setPrefix("/");
        builder.addCommands(new SerialCreation(), new GenerateKeys(), new BotActivation(), new Auth(api), new Revoke(), new Remove(), new Add(), new Help(), new Check(), new Bind(api), new CreatePlan(waiter), new CreateProduct(waiter), new StripeKey(api, waiter));
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
