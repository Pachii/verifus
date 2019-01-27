package us.verif.bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import us.verif.bot.Bot;

import java.awt.*;

public class Help extends Command {
    public Help() {
        super.name = "help";
        super.guildOnly = false;
        super.aliases = new String[]{"verifus", "commands"};
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getArgs().isEmpty()) {
            EmbedBuilder helpMenu = new EmbedBuilder();
            helpMenu.setTitle("Help Categories - /help `#`");
            helpMenu.setColor(Color.blue);
            helpMenu.addBlankField(true);
            helpMenu.addField("1. Client Commands", "Commands for regular server members.", false);
            helpMenu.addField("2. Server Admin Commands", "Commands used by server owners to manage Verifus.", false);
            helpMenu.addField("3. Bot Owner Commands", "Commands that can only be used by Pach#6408. ", false);
            helpMenu.addField("4. Verifus Information", "Information about this bot.", false);
            helpMenu.addField("setup. Setup Help", "Bot setup instructions for new server owners.", false);
            helpMenu.setFooter("Verifus v. " + Bot.version, "https://cdn.discordapp.com/attachments/523261515231395840/534487432939307024/checkmark.png");
            event.replyInDm(helpMenu.build());
        } else if (event.getArgs().equals("1")) {
            EmbedBuilder clientCommandsHelpMenu = new EmbedBuilder();
            clientCommandsHelpMenu.setTitle("Client Commands");
            clientCommandsHelpMenu.setColor(Color.blue);
            clientCommandsHelpMenu.addBlankField(true);
            clientCommandsHelpMenu.addField("/auth XXXXX-XXXXX-XXXXX-XXXXX-XXXXX-XXXXX", "To gain access to the #verification channel with a one-time use auth-key.", false);
            clientCommandsHelpMenu.addField("/check", "Check when your verification for a server will expire. (Type this in the server you want to check)", false);
            event.replyInDm(clientCommandsHelpMenu.build());
        } else if (event.getArgs().equals("2")) {
            EmbedBuilder serverOwnerHelpMenu = new EmbedBuilder();
            serverOwnerHelpMenu.setTitle("Server Owner Commands");
            serverOwnerHelpMenu.setColor(Color.blue);
            serverOwnerHelpMenu.addBlankField(true);
            serverOwnerHelpMenu.addField("Important", "for command inputs with brackets, you need to put a custom value (without brackets).", false);
            serverOwnerHelpMenu.addField("/activate XXXXXX-XXXXXX-XXXXXX-XXXXXX", "Activate a one-time use serial. Type this in any channel in a server that you own that you want Verifus activated for.", false);
            serverOwnerHelpMenu.addField("/database", "(Coming Soon) Generates a link for a database with every verified member, their verified role, and their expire date.", false);
            serverOwnerHelpMenu.addField("/add all:[role]", "Adds a role to everyone in the server.", false);
            serverOwnerHelpMenu.addField("/add role [existing role]:[role]", "Adds a role to everyone with an existing role.", false);
            serverOwnerHelpMenu.addField("/add member [@member]:[role]", "Adds a role to a member.", false);
            serverOwnerHelpMenu.addField("/remove all:[role]", "Removes a role from everyone in the server.", false);
            serverOwnerHelpMenu.addField("/remove role [existing role]:[role]", "Removes a role to everyone with an existing role.", false);
            serverOwnerHelpMenu.addField("/remove member [@member]:[role]", "Removes a role from a member.", false);
            serverOwnerHelpMenu.addField("/generate [amount]:[time]:[role]", "Generate server auth-keys for your customers. Type this in any channel in the chosen server and they will be PMed to you. " +
                    "\n Example 1: `/generate 5:30 day:example role` will generate 5 auth-keys that puts the user in 'example role' for 30 days.", false);
            serverOwnerHelpMenu.addField("Accepted Time Values", "`second` `minute` `hour` `day` `month` `year` `lifetime`", false);
            event.replyInDm(serverOwnerHelpMenu.build());
        } else if (event.getArgs().equals("3")) {
            EmbedBuilder botOwnerHelpMenu = new EmbedBuilder();
            botOwnerHelpMenu.setTitle("Bot Owner Commands");
            botOwnerHelpMenu.setColor(Color.blue);
            botOwnerHelpMenu.addBlankField(true);
            botOwnerHelpMenu.addField("/genserial [time]", "Generates server activation serials for Verifus with a custom time.", false);
            botOwnerHelpMenu.addField("/revoke [guildID]", "Revokes a server's activation.", false);
            event.replyInDm(botOwnerHelpMenu.build());
        } else if (event.getArgs().equals("4")) {
            EmbedBuilder aboutHelpMenu = new EmbedBuilder();
            aboutHelpMenu.setTitle("About");
            aboutHelpMenu.setColor(Color.blue);
            aboutHelpMenu.addBlankField(true);
            aboutHelpMenu.addField("Website", "https://verif.us/", false);
            aboutHelpMenu.addField("Developer", "Pach#6408", false);
            aboutHelpMenu.addField("Lines of Code", "984", false);
            aboutHelpMenu.addField("Database", "MySQL 8.0", false);
            aboutHelpMenu.addField("Version", Bot.version, false);
            event.replyInDm(aboutHelpMenu.build());
        } else if (event.getArgs().equals("setup")) {
            EmbedBuilder setupHelp = new EmbedBuilder();
            setupHelp.setTitle("Setup");
            setupHelp.setColor(Color.blue);
            setupHelp.addBlankField(true);
            setupHelp.addField("First Time Setup", "You've just activated the bot on your server with your activation serial. " +
                    "Next, go to roles and (it's recommended to) turn off every permission for `@everyone` because when people join, you don't" +
                    " want them to be able to do anything except read specified channels like maybe, a channel on how to activate new members' keys" +
                    " (very recommended), or a channel of rules/about your server.", false);
            setupHelp.addField("Creating Roles", "This next step should be easy. If you already have roles that you want to use as " +
                    "roles people would get when they activate a key, then don't worry about this. Otherwise, make roles that you want and " +
                    "turn on some permissions, like send messages. If you turn on see channels, then they can see every public channel, " +
                    "but it's recommended that you see the next step.", false);
            setupHelp.addField("Channel Permission Overrides", "This is the good stuff. If you go to your text channel settings, you " +
                    "can set permission overrides. For example, if there was a role called `monitors` and you want that role to only see " +
                    "channels with monitors, you would disable the view channels permission in the role, but override it with allow in the " +
                    "channels of your choice.", false);
            setupHelp.addField("Generating Keys", "The command to generate access keys for your members is defined in `/help 2` is " +
                    "`/generate [amount]:[time]:[role]`. So if you wanted to generate 30 keys that had a length of 1 month that gives the role `test role`, " +
                    "you would type in the server server: `/generate 30:1 month:test role`. Make sure there are no excess spaces. Accepted time " +
                    "values are: `minute`, `hour`, `day`, `month`, `year`, `lifetime`.", false);
            setupHelp.addField("More Help", "If you are still confused, please take time to go through the `/help` command, then " +
                    "contact the dev `Pach#6408` if you still need assistance.", false);
            event.replyInDm(setupHelp.build());
        }
        }
    }

