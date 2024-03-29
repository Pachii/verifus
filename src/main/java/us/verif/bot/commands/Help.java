package us.verif.bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import us.verif.bot.Bot;
import us.verif.bot.Config;

import java.awt.*;

public class Help extends Command {

    private final static Logger LOGGER = Logger.getLogger(Help.class.getName());
    private JDA jda;

    public Help(JDA jda) {
        super.name = "help";
        super.guildOnly = false;
        super.aliases = new String[]{"verifus", "commands"};
        this.jda = jda;
    }

    @Override
    protected void execute(CommandEvent event) {
        LOGGER.log(Level.INFO, event.getAuthor() + " executed the command '" + event.getMessage().getContentRaw() + "'");
        try {
            if (jda.getGuildById(Config.getGuildId()).getMemberById(event.getAuthor().getId()).hasPermission(Permission.ADMINISTRATOR)) {
                if (event.getArgs().isEmpty()) {
                    EmbedBuilder helpMenu = new EmbedBuilder();
                    helpMenu.setTitle("Help Categories - /help `<category>`");
                    helpMenu.setColor(Color.blue);
                    helpMenu.addBlankField(true);
                    helpMenu.addField("`client` - Client Commands", "Commands for regular server members.", false);
                    helpMenu.addField("`admin` - Server Admin Commands", "Commands used by server owners to manage Verifus.", false);
                    helpMenu.addField("`bot` - Bot Owner Commands", "Commands that can only be used by Pach#6408. ", false);
                    helpMenu.addField("`classic` - Classic Setup Help", "Bot setup instructions for the classic mode of keys.", false);
                    helpMenu.addField("`stripe` - Stripe Setup Help", "Bot setup instructions for stripe.", false);
                    helpMenu.addField("`paypal` - PayPal Setup Help", "Bot setup instructions for PayPal (WIP).", false);
                    helpMenu.addField("`info` - Bot Information", "Information about the bot.", false);
                    helpMenu.setFooter("Verifus v. " + Bot.version, "https://cdn.discordapp.com/attachments/534926501053726751/541664248376459264/kisspng-facebook-social-media-verified-badge-logo-vanity-u-blue-checkmark-5aab5ca4825a51.44338811152.png");
                    event.replyInDm(helpMenu.build());
                } else if (event.getArgs().equals("client")) {
                    EmbedBuilder clientCommandsHelpMenu = new EmbedBuilder();
                    clientCommandsHelpMenu.setTitle("Client Commands");
                    clientCommandsHelpMenu.setColor(Color.blue);
                    clientCommandsHelpMenu.addBlankField(true);
                    clientCommandsHelpMenu.addField("Classic Commands:", "", false);
                    clientCommandsHelpMenu.addField("<key>", "Gives a role for a one-time use classic key. Just message the bot the key.", false);
                    clientCommandsHelpMenu.addField("Stripe Commands:", "", false);
                    clientCommandsHelpMenu.addField("/redeem <key>", "Binds a key to your account with a subscription for a role tied to the key.", false);
                    clientCommandsHelpMenu.addField("/cancel <key>", "Cancels a subscription.", false);
                    event.replyInDm(clientCommandsHelpMenu.build());
                } else if (event.getArgs().equals("admin")) {
                    EmbedBuilder serverOwnerHelpMenu = new EmbedBuilder();
                    serverOwnerHelpMenu.setTitle("Server Owner/Admin Commands");
                    serverOwnerHelpMenu.setColor(Color.blue);
                    serverOwnerHelpMenu.addBlankField(true);
                    serverOwnerHelpMenu.addField("Classic Commands:", "", false);
                    serverOwnerHelpMenu.addField("/activate <serial>", "Activate a one-time use serial. Type this in any channel in a server that you own that you want Verifus activated for.", false);
                    serverOwnerHelpMenu.addField("/add", "Add roles to members, including specific times.", false);
                    serverOwnerHelpMenu.addField("/remove", "Remove roles from members.", false);
                    serverOwnerHelpMenu.addField("/generate", "Generate server auth-keys for your customers. Type this in any channel in the chosen server and they will be DMed to you.", false);
                    serverOwnerHelpMenu.addField("Stripe Commands:", "", false);
                    serverOwnerHelpMenu.addField("/stripekey <key>", "Ties a Stripe API secret key to the bot for use with Stripe.", false);
                    serverOwnerHelpMenu.addField("Other Commands:", "", false);
                    serverOwnerHelpMenu.addField("/roleid <rolename>", "Shows the role ID for a role.", false);
                    serverOwnerHelpMenu.addField("/setstatus <status>", "Sets the bot's status.", false);
                    event.replyInDm(serverOwnerHelpMenu.build());
                } else if (event.getArgs().equals("bot")) {
                    EmbedBuilder botOwnerHelpMenu = new EmbedBuilder();
                    botOwnerHelpMenu.setTitle("Bot Owner Commands");
                    botOwnerHelpMenu.setColor(Color.blue);
                    botOwnerHelpMenu.addBlankField(true);
                    botOwnerHelpMenu.addField("/genserial <time>", "Generates server activation serials for Verifus with a custom time.", false);
                    event.replyInDm(botOwnerHelpMenu.build());
                } else if (event.getArgs().equals("setup")) {
                    EmbedBuilder setupHelp = new EmbedBuilder();
                    setupHelp.setTitle("Setup");
                    setupHelp.setColor(Color.blue);
                    setupHelp.addBlankField(true);
                    setupHelp.addField("About Classic Mode", "The classic mode is a simple way to use this bot. You generate the keys yourself, and it is up " +
                            "to you to how you distribute it. The bot will not distribute the keys itself. This is good if you plan to use a site that can grab keys from " +
                            "a key stock that you paste in there.", false);
                    setupHelp.addField("Generating Keys", "The command to generate access keys is `/generate`. just type that in the server and " +
                            "Verifus will guide you through the key creation process. NON STRIPE only! If you're using Stripe don't use this. Do `/help stripe`.", false);
                    setupHelp.addField("Redeeming Keys", "To redeem keys, members just have to DM the bot the key, nothing else, and they will get " +
                            "the role on the server. It's recommended that there's a channel to teach new members how to redeem their activation key.", false);
                    setupHelp.addField("More Help", "If you are still confused, please take time to go through the `/help` command, then " +
                            "contact the dev `Pach#6408` if you still need assistance.", false);
                    event.replyInDm(setupHelp.build());
                } else if (event.getArgs().equals("stripe")) {
                    EmbedBuilder setupHelp = new EmbedBuilder();
                    setupHelp.setTitle("Stripe Setup");
                    setupHelp.setColor(Color.blue);
                    setupHelp.addBlankField(true);
                    setupHelp.addField("About Stripe Mode", "Stripe is an easy way to manage subscriptions and payments. The bot will take care of pretty much everything. " +
                            "As long as it is set up correctly, it will automatically generate and email keys to the purchasers' emails for them to redeem with their Discord. They are " +
                            "given many options, like cancelling their payment and key, or moving the key to another account.", false);
                    setupHelp.addField("Saving your Stripe API Key", "First, you need to tell the bot your Stripe secret API key. On the Stripe dashboard, go to Developers > API keys" +
                            "and copy your secret key. Then, DM the bot `/stripekey` and it will tell you what to do.", false);
                    setupHelp.addField("Create a product and plan", "In the Stripe dashboard sidebar, go to Billing > Products and click new and make a product. Then, it will prompt you " +
                            "to make a plan. For the plan nickname, only put the ID of the role that you want to give. To get the role ID, type `/roleid <rolename>` in your server, and then back in the" +
                            " dashboard you can finalize the plan by setting the price, etc. If you are using an external source that links to your Stripe account, it may create a product and a plan " +
                            "for you, and in that case just rename the plan to the role ID.", false);
                    setupHelp.addField("Webhook Setup (IMPORTANT)", "The last step is to set the webhook URL in your Stripe account. Go to your dashboard, Developers > Webhooks and click " +
                            "add endpoint. The URL will be `https://verifus.ddns.net/" + Config.getStripeWebhookUrl() + "`, all events, and click add endpoint. Now the Stripe setup is finished.", false);
                    setupHelp.addField("How it works", "When a user receives their key in their email, they can DM the bot `/redeem <key>` and the key will be attached to their account " +
                            "and they will get the role. If they somehow lose the role, they can do the same command and they will get their role back if it was removed. When a key is tied to a " +
                            "Discord account, it cannot be redeemed on another account, unless that person does `/unbind <key>` (which will also remove their role if they had it before), then it is free to use for " +
                            "anyone to redeem. After their subscription is canceled or failed to pay, their role will be removed and the key will be deleted from the database.", false);
                    setupHelp.addField("Handling Errors", "One of the errors that can occur with this bot and Stripe is that the correct redeem key sent to the correct email will not work when trying " +
                            "to redeem it. This is most likely because the role ID is entered wrong as the product nickname. Go back into Stripe and make sure the correct ID is entered, NOT the role name.", false);
                    setupHelp.addField("More Help", "If you are still confused, please take time to go through the `/help` command, then " +
                            "contact the dev `Pach#6408` if you still need assistance.", false);
                    event.replyInDm(setupHelp.build());
                } else if (event.getArgs().equals("info")) {
                    EmbedBuilder aboutHelpMenu = new EmbedBuilder();
                    aboutHelpMenu.setTitle("About");
                    aboutHelpMenu.setColor(Color.blue);
                    aboutHelpMenu.addBlankField(true);
                    aboutHelpMenu.addField("Website", "https://verif.us/", false);
                    aboutHelpMenu.addField("Developer", "Pach#6408", false);
                    aboutHelpMenu.addField("Lines of Code", "2731", false);
                    aboutHelpMenu.addField("Database", "MySQL 8.0", false);
                    aboutHelpMenu.addField("Version", Bot.version, false);
                    event.replyInDm(aboutHelpMenu.build());
                }
            }
        } catch (NullPointerException e) {

        }
    }
}

