package us.verif.bot.sql;

import us.verif.bot.Config;
import us.verif.bot.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class Setup {
    public static boolean startFinished = false;
    public static void start() {
        try (Connection connection = DataSource.getConnection()) {
            ArrayList<String> array = new ArrayList<>();
            array.add("CREATE DATABASE IF NOT EXISTS `verifus`");
            array.add("USE verifus");
            array.add("create table if not exists `activationserials` (`serial` varchar(30), `length` varchar(20))");
            array.add("create table if not exists `activatedservers` (`guildId` varchar(30), `expireDate` datetime)");
            array.add("CREATE DATABASE IF NOT EXISTS `" + Config.getGuildId() + "`");
            array.add("USE `" + Config.getGuildId() + "`");
            array.add("CREATE TABLE IF NOT EXISTS serverkeys(`key` VARCHAR(1000), `length` VARCHAR(20), `roleId` VARCHAR(100))");
            array.add("CREATE TABLE IF NOT EXISTS verifiedusers(`discordId` VARCHAR(30), `roleId` VARCHAR(100), `expireDate` DATETIME)");
            array.add("CREATE TABLE IF NOT EXISTS serverinfo(`dataColumn` int, `stripeKey` VARCHAR(200), `emailSubject` VARCHAR (300), `emailHtml` VARCHAR(10000) CHARACTER SET utf8, `botStatus` VARCHAR (200))");
            array.add("INSERT IGNORE INTO serverinfo (dataColumn, stripeKey, emailSubject, emailHtml, botStatus) VALUES (1, 'empty', 'empty', 'empty', '/help')");
            array.add("CREATE TABLE IF NOT EXISTS stripekeys(`key` VARCHAR(100), `subscriptionId` VARCHAR(100), `roleId` VARCHAR(100))");
            array.add("CREATE TABLE IF NOT EXISTS stripeusers(`discordId` VARCHAR(100), `key` VARCHAR(100))");
            PreparedStatement ps = null;
            for (String q : array) {
                if (q.equals("CREATE TABLE IF NOT EXISTS serverkeys(`key` VARCHAR(1000), `length` VARCHAR(20), `roleId` VARCHAR(100))")) connection.setCatalog(Config.getGuildId());
                ps = connection.prepareStatement(q);
                ps.executeUpdate();
            }
            ps.close();
            startFinished = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
