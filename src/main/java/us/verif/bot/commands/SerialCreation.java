package us.verif.bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import us.verif.bot.sql.ActivationDatabase;

import java.awt.*;
import java.security.SecureRandom;
import java.util.Random;

public class SerialCreation extends Command {
    static final private String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    final private Random rng = new SecureRandom();
    private final static Logger LOGGER = Logger.getLogger(SerialCreation.class.getName());

    public SerialCreation() {
        super.name = "genserial";
        super.guildOnly = false;
        super.ownerCommand = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        String serial = randomUUID(24, 6, '-');
        String[] splitString = event.getArgs().split(" ");
        String time = splitString[0] + " " + splitString[1].toUpperCase();
        ActivationDatabase.addSerial(serial, time);

        EmbedBuilder embedSerial = new EmbedBuilder();
        embedSerial.setTitle("Serial Generated");
        embedSerial.setColor(Color.green);
        embedSerial.addBlankField(true);
        embedSerial.addField("Duration: " + time, serial, false);
        event.replyInDm(embedSerial.build());
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
