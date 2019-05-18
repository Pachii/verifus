/*
 * Copyright (c) 2018-2019 Verifus and/or its affiliates. All rights reserved.
 * VERIFUS PROPRIETARY/CONFIDENTIAL. Do not distribute or decompile.
 */
package us.verif.bot;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import us.verif.bot.stripe.StripeWebhook;
import us.verif.bot.stripe.commands.*;
import us.verif.bot.commands.*;
import us.verif.bot.events.GuildMemberJoin;
import us.verif.bot.events.KeyCheck;
import us.verif.bot.sql.Setup;
import us.verif.bot.sql.Sql;

import javax.xml.crypto.Data;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Bot {

    public static final String version = "0.2.5";
    private final static Logger LOGGER = Logger.getLogger(Bot.class.getName());

    public static void main(String[] args) throws Exception {
        // Initiate DataSource
        new DataSource();

        Setup.start();

        EventWaiter waiter = new EventWaiter();

        Config.createConfigFile();

        JDA api = new JDABuilder(AccountType.BOT)
                .setToken(Config.getToken())
                .addEventListener(waiter)
                .setGame(Game.of(Game.GameType.STREAMING, Sql.getBotStatus()))
                .build().awaitReady();

        StripeWebhook webhook = new StripeWebhook(api);
        webhook.startListener();

        CommandClientBuilder builder = new CommandClientBuilder();
        builder.setPrefix("/");
        builder.addCommands(new SerialCreation(), new GenerateKeys(waiter, api), new BotActivation(api), new Remove(waiter), new Add(waiter, api), new Help(api), new Redeem(api), new StripeKey(api)
                , new GuildId(), new Cancel(waiter), new RoleId(), new Unbind(api), new SetStatus(api));
        builder.setOwnerId("426839909421154314");
        builder.setHelpWord("unusedhelp");
        CommandClient commands = builder.build();

        api.addEventListener(commands, new GuildMemberJoin(), new KeyCheck(api));
        new StripeWebhook(api);

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                new PeriodicCheck(api).run();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.MINUTES);
        LOGGER.log(Level.INFO, "\n      ___  __     ___       __  \n" +
                "\\  / |__  |__) | |__  |  | /__` \n" +
                " \\/  |___ |  \\ | |    \\__/ .__/ \n" +
                "Version: " + version + "\nGuild: " + api.getGuildById(Config.getGuildId()) + "\nWebhook URL: /" + Config.getStripeWebhookUrl() + "\nWebhook Port: " + Config.getStripeWebhookPort());
    }
}
