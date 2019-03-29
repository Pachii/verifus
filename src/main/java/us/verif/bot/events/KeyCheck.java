package us.verif.bot.events;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import us.verif.bot.Config;
import us.verif.bot.Helpers;
import us.verif.bot.sql.Sql;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class KeyCheck extends ListenerAdapter {
    private JDA jda;

    public KeyCheck(JDA jda) {
        this.jda = jda;
    }

    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {

        final String inputtedKey = event.getMessage().getContentRaw();
        String guildId = Config.getGuildId();

        if (Sql.containsKey(inputtedKey)) {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date today = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(today);
            String storedTime = Sql.getKeyTime(inputtedKey);
            int amount;
            try {
                amount = Integer.parseInt(storedTime.split(" ")[0]);
            } catch (Throwable e) {
                return;
            }
            Date expireDate = null;
            String role = Sql.getKeyRole(inputtedKey);
            switch (storedTime.split(" ")[1]) {
                case "SECOND":
                    if (Sql.userExistsInDatabaseWithGuildRole(event.getAuthor().getId(), role)) {
                        cal.setTime(Sql.getUserExpireDate(event.getAuthor().getId()));
                        cal.add(Calendar.SECOND, amount);
                        expireDate = cal.getTime();
                        Sql.updateVerifiedUser(dateFormat.format(expireDate), event.getAuthor().getId(), role);
                    } else {
                        cal.add(Calendar.SECOND, amount);
                        expireDate = cal.getTime();
                        Sql.addVerifiedUser(dateFormat.format(expireDate), event.getAuthor().getId(), role);
                    }
                    break;
                case "MINUTE":
                    if (Sql.userExistsInDatabaseWithGuildRole(event.getAuthor().getId(), role)) {
                        cal.setTime(Sql.getUserExpireDate(event.getAuthor().getId()));
                        cal.add(Calendar.MINUTE, amount);
                        expireDate = cal.getTime();
                        Sql.updateVerifiedUser(dateFormat.format(expireDate), event.getAuthor().getId(), role);

                    } else {
                        cal.add(Calendar.MINUTE, amount);
                        expireDate = cal.getTime();
                        Sql.addVerifiedUser(dateFormat.format(expireDate), event.getAuthor().getId(), role);

                    }
                    break;
                case "HOUR":
                    if (Sql.userExistsInDatabaseWithGuildRole(event.getAuthor().getId(), role)) {
                        cal.setTime(Sql.getUserExpireDate(event.getAuthor().getId()));
                        cal.add(Calendar.HOUR, amount);
                        expireDate = cal.getTime();
                        Sql.updateVerifiedUser(dateFormat.format(expireDate), event.getAuthor().getId(), role);

                    } else {
                        cal.add(Calendar.HOUR, amount);
                        expireDate = cal.getTime();
                        Sql.addVerifiedUser(dateFormat.format(expireDate), event.getAuthor().getId(), role);

                    }
                    break;
                case "DAY":
                    if (Sql.userExistsInDatabaseWithGuildRole(event.getAuthor().getId(), role)) {
                        cal.setTime(Sql.getUserExpireDate(event.getAuthor().getId()));
                        cal.add(Calendar.DATE, amount);
                        expireDate = cal.getTime();
                        Sql.updateVerifiedUser(dateFormat.format(expireDate), event.getAuthor().getId(), role);

                    } else {
                        cal.add(Calendar.DATE, amount);
                        expireDate = cal.getTime();
                        Sql.addVerifiedUser(dateFormat.format(expireDate), event.getAuthor().getId(), role);

                    }
                    break;
                case "MONTH":
                    if (Sql.userExistsInDatabaseWithGuildRole(event.getAuthor().getId(), role)) {
                        cal.setTime(Sql.getUserExpireDate(event.getAuthor().getId()));
                        cal.add(Calendar.MONTH, amount);
                        expireDate = cal.getTime();
                        Sql.updateVerifiedUser(dateFormat.format(expireDate), event.getAuthor().getId(), role);

                    } else {
                        cal.add(Calendar.MONTH, amount);
                        expireDate = cal.getTime();
                        Sql.addVerifiedUser(dateFormat.format(expireDate), event.getAuthor().getId(), role);

                    }
                    break;
                case "YEAR":
                    if (Sql.userExistsInDatabaseWithGuildRole(event.getAuthor().getId(), role)) {
                        cal.setTime(Sql.getUserExpireDate(event.getAuthor().getId()));
                        cal.add(Calendar.YEAR, amount);
                        expireDate = cal.getTime();
                        Sql.updateVerifiedUser(dateFormat.format(expireDate), event.getAuthor().getId(), role);

                    } else {
                        cal.add(Calendar.YEAR, amount);
                        expireDate = cal.getTime();
                        Sql.addVerifiedUser(dateFormat.format(expireDate), event.getAuthor().getId(), role);

                    }
                    break;
                case "LIFETIME":
                    cal.add(Calendar.YEAR, 200);
                    expireDate = cal.getTime();
                    Sql.addVerifiedUser(dateFormat.format(expireDate), event.getAuthor().getId(), role);
                    break;
            }
            Role keyRole = jda.getGuildById(guildId).getRoleById(Sql.getKeyRole(inputtedKey));
            jda.getGuildById(guildId).getController().addSingleRoleToMember(jda.getGuildById(guildId).getMemberById(event.getAuthor().getId()), keyRole).queue();

            Helpers.sendPrivateMessage(event.getAuthor(), "RECEIPT: You used the one-time activation key `" + inputtedKey +
                    "` to gain access to `" + jda.getGuildById(guildId).getName() + "` with the role `" + jda.getGuildById(guildId).getRoleById(Sql.getKeyRole(inputtedKey)).getName() + "`. Your `" + storedTime + "` activation will expire on `" + dateFormat.format(expireDate) + "`.");
            Sql.deleteRegisteredKey(inputtedKey);
        }
    }
}
