package us.verif.bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import us.verif.bot.Config;
import us.verif.bot.sql.ActivationDatabase;
import us.verif.bot.sql.Sql;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class GenerateKeys extends Command {
    private JDA jda;

    static final private String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    final private Random rng = new SecureRandom();

    private final EventWaiter waiter;

    public GenerateKeys(EventWaiter waiter, JDA jda) {
        this.jda = jda;
        this.waiter = waiter;
        super.name = "generate";
    }

    @Override
    protected void execute(CommandEvent event) {
        if (ActivationDatabase.isActivated()) {
            if (jda.getGuildById(Config.getGuildId()).getMemberById(event.getAuthor().getId()).hasPermission(Permission.ADMINISTRATOR)) {

                event.reply("Enter the number of keys you would like to generate. Type `cancel` anytime to cancel.");
                waiter.waitForEvent(MessageReceivedEvent.class,
                        e -> e.getAuthor().equals(event.getAuthor()) && e.getChannel().equals(event.getChannel()),
                        e -> {
                            if (e.getMessage().getContentRaw().equals("cancel")) {
                                event.reply("Key generation canceled.");
                                return;
                            }
                            int batch = Integer.parseInt(e.getMessage().getContentRaw());

                            event.reply("Enter the time interval of the keys. Accepted intervals: `second` `minute` `hour` `day` `month` `year` `lifetime`");
                            waiter.waitForEvent(MessageReceivedEvent.class,
                                    e1 -> e1.getAuthor().equals(event.getAuthor()) && e1.getChannel().equals(event.getChannel()),
                                    e1 -> {
                                        if (e1.getMessage().getContentRaw().equals("cancel")) {
                                            event.reply("Key generation canceled.");
                                            return;
                                        }
                                        ArrayList<String> acceptedValues = new ArrayList<>();
                                        acceptedValues.add("second");
                                        acceptedValues.add("minute");
                                        acceptedValues.add("hour");
                                        acceptedValues.add("day");
                                        acceptedValues.add("month");
                                        acceptedValues.add("year");
                                        acceptedValues.add("lifetime");
                                        String interval = e1.getMessage().getContentRaw();
                                        if (!acceptedValues.contains(interval.toLowerCase())) {
                                            event.reply("Error: Invalid interval. Creation canceled.");
                                            return;
                                        }

                                        event.reply("Enter the number of `" + interval.toLowerCase() + "s` that the activation will last.");
                                        waiter.waitForEvent(MessageReceivedEvent.class,
                                                e2 -> e2.getAuthor().equals(event.getAuthor()) && e2.getChannel().equals(event.getChannel()),
                                                e2 -> {
                                                    if (e2.getMessage().getContentRaw().equals("cancel")) {
                                                        event.reply("Key generation canceled.");
                                                        return;
                                                    }
                                                    int number = Integer.parseInt(e2.getMessage().getContentRaw());

                                                    event.reply("Enter the role name for the keys.");
                                                    waiter.waitForEvent(MessageReceivedEvent.class,
                                                            e3 -> e3.getAuthor().equals(event.getAuthor()) && e3.getChannel().equals(event.getChannel()),
                                                            e3 -> {
                                                                if (e3.getMessage().getContentRaw().equals("cancel")) {
                                                                    event.reply("Key generation canceled.");
                                                                    return;
                                                                }
                                                                String roleId = jda.getGuildById(Config.getGuildId()).getRolesByName(e3.getMessage().getContentRaw(), true).get(0).getId();
                                                                try {
                                                                    jda.getGuildById(Config.getGuildId()).getRoleById(roleId);
                                                                } catch (Throwable ex) {
                                                                    event.reply("Error: Role does not exist. Creation canceled.");
                                                                    return;
                                                                }
                                                                try {
                                                                    List<String> keysList = new ArrayList<>();

                                                                    for (int i = 0; i < batch; i++) {
                                                                        String serial = randomUUID(30, 5, '-');
                                                                        keysList.add(serial);
                                                                        Sql.registerKey(serial, number + " " + interval.toUpperCase(), roleId);
                                                                    }
                                                                    Writer writer;
                                                                    File tempFile = File.createTempFile("keys-", ".txt");
                                                                    writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8));
                                                                    for (String s : keysList) {
                                                                        writer.append(s);
                                                                        writer.append("\n");
                                                                    }
                                                                    writer.close();
                                                                    Consumer<Message> callback = (response) -> tempFile.delete();
                                                                    User user = event.getAuthor();
                                                                    user.openPrivateChannel().queue((channel) -> channel.sendMessage("Generated Keys: `" + number + " " + interval + "` for role `" + e.getGuild().getRoleById(roleId).getName() + "`.").queue());
                                                                    user.openPrivateChannel().queue((channel) -> channel.sendFile(tempFile).queue(callback));
                                                                } catch (Throwable ex) {
                                                                    ex.printStackTrace();
                                                                }
                                                            });
                                                });
                                    });
                        });
            }
        }
    }


    private char randomChar() {
        return ALPHABET.charAt(rng.nextInt(ALPHABET.length()));
    }

    private String randomUUID(int length, int spacing, char spacerChar) {
        StringBuilder sb = new StringBuilder();
        int spacer = 0;
        while (length > 0) {
            if (spacer == spacing) {
                sb.append(spacerChar);
                spacer = 0;
            }
            length--;
            spacer++;
            sb.append(randomChar());
        }
        return sb.toString();
    }
}

