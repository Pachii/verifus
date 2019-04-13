package us.verif.bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import us.verif.bot.Config;
import us.verif.bot.sql.ActivationDatabase;
import us.verif.bot.sql.Sql;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BotActivation extends Command {
    private JDA jda;
    private final static Logger LOGGER = Logger.getLogger(BotActivation.class.getName());

    public BotActivation(JDA jda) {
        this.jda = jda;
        super.name = "activate";
        super.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        LOGGER.log(Level.INFO, event.getAuthor() + " executed the command '" + event.getMessage().getContentRaw() + "'");
        String serial = event.getArgs();
        if (ActivationDatabase.hasSerial(serial) && jda.getGuildById(Config.getGuildId()).getMemberById(event.getAuthor().getId()).hasPermission(Permission.ADMINISTRATOR)) {
            if (event.getArgs().isEmpty()) return;
            Date today = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(today);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String serialTime = ActivationDatabase.getActivationSerialTime(serial);
            int amount;
            amount = Integer.parseInt(serialTime.split(" ")[0]);
            String time = serialTime.split(" ")[1];
            Date expireDate = null;
            switch (time) {
                case "MINUTE":
                    if (ActivationDatabase.isActivated()) {
                        cal.setTime(ActivationDatabase.activationExpireDate(Config.getGuildId()));
                        cal.add(Calendar.MINUTE, amount);
                        expireDate = cal.getTime();
                        ActivationDatabase.updateGuildActivation(dateFormat.format(expireDate), Config.getGuildId());
                    } else {
                        cal.add(Calendar.MINUTE, amount);
                        expireDate = cal.getTime();
                        ActivationDatabase.updateGuildActivation(dateFormat.format(expireDate), Config.getGuildId());
                    }
                    break;
                case "HOUR":
                    if (ActivationDatabase.isActivated()) {
                        cal.setTime(ActivationDatabase.activationExpireDate(Config.getGuildId()));
                        cal.add(Calendar.HOUR, amount);
                        expireDate = cal.getTime();
                        ActivationDatabase.updateGuildActivation(dateFormat.format(expireDate), Config.getGuildId());

                    } else {
                        cal.add(Calendar.HOUR, amount);
                        expireDate = cal.getTime();
                        ActivationDatabase.updateGuildActivation(dateFormat.format(expireDate), Config.getGuildId());

                    }
                    break;
                case "DAY":
                    if (ActivationDatabase.isActivated()) {
                        cal.setTime(ActivationDatabase.activationExpireDate(Config.getGuildId()));
                        cal.add(Calendar.DATE, amount);
                        expireDate = cal.getTime();
                        ActivationDatabase.updateGuildActivation(dateFormat.format(expireDate), Config.getGuildId());

                    } else {
                        cal.add(Calendar.DATE, amount);
                        expireDate = cal.getTime();
                        ActivationDatabase.updateGuildActivation(dateFormat.format(expireDate), Config.getGuildId());

                    }
                    break;
                case "MONTH":
                    if (ActivationDatabase.isActivated()) {
                        cal.setTime(ActivationDatabase.activationExpireDate(Config.getGuildId()));
                        cal.add(Calendar.MONTH, amount);
                        expireDate = cal.getTime();
                        ActivationDatabase.updateGuildActivation(dateFormat.format(expireDate), Config.getGuildId());

                    } else {
                        cal.add(Calendar.MONTH, amount);
                        expireDate = cal.getTime();
                        ActivationDatabase.updateGuildActivation(dateFormat.format(expireDate), Config.getGuildId());

                    }
                    break;
                case "YEAR":
                    if (ActivationDatabase.isActivated()) {
                        cal.setTime(ActivationDatabase.activationExpireDate(Config.getGuildId()));
                        cal.add(Calendar.YEAR, amount);
                        expireDate = cal.getTime();
                        ActivationDatabase.updateGuildActivation(dateFormat.format(expireDate), Config.getGuildId());

                    } else {
                        cal.add(Calendar.YEAR, amount);
                        expireDate = cal.getTime();
                        ActivationDatabase.updateGuildActivation(dateFormat.format(expireDate), Config.getGuildId());

                    }
                    break;
                case "LIFETIME":
                    cal.add(Calendar.YEAR, 200);
                    expireDate = cal.getTime();
                    ActivationDatabase.updateGuildActivation(dateFormat.format(expireDate), Config.getGuildId());
                    break;
            }
            ActivationDatabase.deleteRegisteredSerial(serial);
            System.out.println(serial + amount + time + dateFormat.format(expireDate));
            event.reply("SUCCESS: You used the one-time activation serial `" + serial + "` to activate your server `" + jda.getGuildById(Config.getGuildId()).getName() + "`. Your " +
                    "`" + amount + " " + time + "` activation will expire on `" + dateFormat.format(expireDate) + "`. Type `/help` for setup info.");
            LOGGER.log(Level.INFO, event.getAuthor() + " used the serial " + serial + ". The " + amount + " " + time + " activation expires on " + dateFormat.format(expireDate) + ".");
        }
    }
}
