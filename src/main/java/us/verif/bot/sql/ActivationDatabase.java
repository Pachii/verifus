package us.verif.bot.sql;

import us.verif.bot.Config;
import us.verif.bot.DataSource;

import java.sql.*;
import java.util.Date;

public class ActivationDatabase {

    public static String getActivationSerialTime(String serial) {
        String length = null;
        try {
        Connection connection = DataSource.getConnection();
        connection.setCatalog("verifus");
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM activationserials WHERE serial=?");
        preparedStatement.setString(1, serial);

        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) length = resultSet.getString("length");

        connection.close();
        } catch (SQLException e) { e.printStackTrace(); }

        return length;
    }

    public static boolean hasSerial(String serial) {
        boolean exists = false;
        try {
            Connection connection = DataSource.getConnection();
            connection.setCatalog("verifus");
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM activationserials WHERE serial = ?");
            preparedStatement.setString(1, serial);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) exists = true;

            connection.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return exists;
    }

    public static boolean isActivated() {
        try {
            Connection connection = DataSource.getConnection();
            connection.setCatalog("verifus");
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM activatedservers WHERE guildId = '" + Config.getGuildId() + "'");

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) return true;

            connection.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public static java.util.Date activationExpireDate(String guildId) {
        try {
            Connection connection = DataSource.getConnection();
            connection.setCatalog("verifus");
            PreparedStatement preparedStatement = connection.prepareStatement("select expireDate from activatedservers where guildId=?");
            preparedStatement.setString(1, guildId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Timestamp timestamp = resultSet.getTimestamp("expireDate");
                if (timestamp != null) {
                    connection.close();
                    return new Date(timestamp.getTime());
                }
            }
            connection.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    public static void updateGuildActivation(String expireDate, String guildId) {
        try {
            Connection connection = DataSource.getConnection();
            connection.setCatalog("verifus");
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO activatedservers (expireDate,guildId) VALUES(?,?) ON DUPLICATE KEY UPDATE expireDate=?");
            preparedStatement.setString(1, expireDate);
            preparedStatement.setString(2, guildId);
            preparedStatement.setString(3, expireDate);
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    public static void addSerial(String serial, String time) {
        try {
            Connection connection = DataSource.getConnection();
            connection.setCatalog("verifus");
            PreparedStatement preparedStatement = connection.prepareStatement("insert into activationserials (serial,length) values (?,?)");
            preparedStatement.setString(1, serial);
            preparedStatement.setString(2, time);
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    public static void deleteRegisteredSerial(String serial) {
        try {
            Connection connection = DataSource.getConnection();
            connection.setCatalog("verifus");
            PreparedStatement preparedStatement = connection.prepareStatement("delete from activationserials where serial = ?");
            preparedStatement.setString(1, serial);
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    public static void deleteActivatedGuild(String guildId) {
        try {
            Connection connection = DataSource.getConnection();
            connection.setCatalog("verifus");
            PreparedStatement preparedStatement = connection.prepareStatement("delete from activatedservers where guildId = ?");
            preparedStatement.setString(1, guildId);
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
