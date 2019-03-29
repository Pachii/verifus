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
import us.verif.bot.Stripe.StripeWebhook;
import us.verif.bot.Stripe.commands.*;
import us.verif.bot.commands.*;
import us.verif.bot.events.GuildMemberJoin;
import us.verif.bot.events.KeyCheck;
import us.verif.bot.sql.Setup;
import us.verif.bot.sql.Sql;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Bot {

    public static final String version = "0.1.0";
    //NTM0OTI0NTgxNTg5MjIxMzg3.Dzz09w._8m0vdTwqAs5xsgHzeNwfoBzcmU

    public static void main(String[] args) throws Exception {

        Setup.start();

        EventWaiter waiter = new EventWaiter();

        Config.createConfigFile();

        JDA api = new JDABuilder(AccountType.BOT)
                .setToken(Config.getToken())
                .addEventListener(waiter)
                .setGame(Game.of(Game.GameType.STREAMING, Sql.getBotStatus()))
                .build();

        StripeWebhook webhook = new StripeWebhook(api);
        webhook.startListener();

        CommandClientBuilder builder = new CommandClientBuilder();
        builder.setPrefix("/");
        builder.addCommands(new SerialCreation(), new GenerateKeys(waiter, api), new BotActivation(api), new Revoke(api), new Remove(waiter), new Add(waiter, api), new Help(), new Check(), new Redeem(api),
                new CreatePlan(waiter, api), new CreateProduct(waiter, api), new StripeKey(api, waiter), new GuildId(), new Cancel(waiter), new RoleId(), new EmailHtml(api), new EmailSubject(api), new Unbind(api),
                new SetStatus(api));
        builder.setOwnerId("426839909421154314");
        builder.setHelpWord("unusedhelp");
        CommandClient commands = builder.build();

        api.awaitReady();

        api.addEventListener(commands, new GuildMemberJoin(), new KeyCheck(api));
        new StripeWebhook(api);

        PeriodicCheck periodicCheck = new PeriodicCheck(api);
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                periodicCheck.run();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }
}
