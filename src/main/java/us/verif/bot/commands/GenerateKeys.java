package us.verif.bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import us.verif.bot.Sql;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static net.dv8tion.jda.core.Permission.ADMINISTRATOR;

public class GenerateKeys extends Command {

    static final private String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    final private Random rng = new SecureRandom();

    public GenerateKeys() {
        super.name = "generate";
    }

    @Override
    protected void execute(CommandEvent event) {
        if (Sql.activatedServersHas(event.getGuild().getId())) {
            if (event.getGuild().getOwnerId().equals(event.getAuthor().getId()) || event.getGuild().getMember(event.getAuthor()).hasPermission(ADMINISTRATOR)) {
                if (event.getArgs().isEmpty()) return;
                List<String> keysList = new ArrayList<>();
                keysList.clear();
                try {
                    int batch = Integer.parseInt(event.getArgs().split(":")[0]);
                    String time = event.getArgs().split(":")[1].toUpperCase();
                    String role = event.getArgs().split(":")[2];
                    if (event.getGuild().getRolesByName(role, true).isEmpty()) return;
                    if (batch > 28) {
                        for (int i = 0; i < batch; i++) {
                            String serial = randomUUID(30, 5, '-');
                            keysList.add(serial);
                            Sql.execute("", "insert into `serverkeys` (`key`,`guildID`,`length`,`role`) values ('" + serial + "','" + event.getGuild().getId() + "','" + time + "','" + role + "');");
                        }
                        Writer writer;
                        try {
                            File tempFile = File.createTempFile("keys-", ".txt");
                            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8));
                            for (String s : keysList) {
                                writer.append(s);
                                writer.append("\n");
                            }
                            writer.close();
                            event.replyInDm("Generated Keys: `" + time + "` for role `" + role + "`.", tempFile, "generatedkeys.txt");
                            tempFile.delete();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        return;
                    }
                    for (int i = 0; i < batch; i++) {
                        String serial = randomUUID(30, 5, '-');
                        keysList.add(serial);
                        Sql.execute("", "insert into `serverkeys` (`key`,`guildID`,`length`,`role`) values ('" + serial + "','" + event.getGuild().getId() + "','" + time + "','" + role + "');");
                    }

                    StringBuilder sb = new StringBuilder();
                    for (String s : keysList) {
                        sb.append(s);
                        sb.append("\n");
                    }
                    EmbedBuilder embedKeys = new EmbedBuilder();
                    embedKeys.setTitle("Key(s) Generated for server `" + event.getGuild().getName() + "`:");
                    embedKeys.setColor(Color.green);
                    embedKeys.addBlankField(true);
                    embedKeys.addField("Info: `" + time + "` for role `" + role + "`", sb.toString(), false);
                    event.replyInDm(embedKeys.build());
                    event.replyInDm("Raw Key(s) (If on mobile):");
                    event.replyInDm(sb.toString());
                } catch (ArrayIndexOutOfBoundsException e) {
                    event.reply("Please specify the amount of keys, the length of time, and the role to give for the key.");
                }
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

