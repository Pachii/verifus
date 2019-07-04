package us.verif.bot.stripe.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import us.verif.bot.sql.Sql;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DBCommand extends Command {
    private final static Logger LOGGER = Logger.getLogger(Cancel.class.getName());

    public DBCommand() {
        super.name = "db";
        super.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
//
//        LOGGER.log(Level.INFO, event.getAuthor() + " executed the command '" + event.getMessage().getContentRaw() + "'");
//
//        try {
//            List<String> dbList = new ArrayList<>();
//
//            for (int i = 0; i < batch; i++) {
//                String serial = randomUUID(30, 5, '-');
//                dbList.add(serial);
//                Sql.registerKey(serial, number + " " + interval.toUpperCase(), id);
//            }
//            Writer writer;
//            File tempFile = File.createTempFile("db-", ".txt");
//            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8));
//            for (String s : dbList) {
//                writer.append(s);
//                writer.append("\n");
//            }
//            writer.close();
//            Consumer<Message> callback = (response) -> tempFile.delete();
//            User user = event.getAuthor();
//            user.openPrivateChannel().queue((channel) -> channel.sendFile(tempFile).queue(callback));
//        } catch (Throwable ex) {
//            ex.printStackTrace();
//        }
    }
}