package us.verif.bot.sql;

import us.verif.bot.Config;
import us.verif.bot.DataSource;

import javax.validation.constraints.NotNull;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

public class Sql {

    public static boolean containsKey(String key) {
        boolean exists = false;
        try {
            Connection connection = DataSource.getConnection();
            connection.setCatalog(Config.getGuildId());
            PreparedStatement preparedStatement = connection.prepareStatement("select * from serverkeys where `key` = '" + key + "'");

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                exists = true;
                connection.close();
            }
            connection.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return exists;
    }

    public static String getKeyRole(String key) {
        String result = null;
        try {
            Connection connection = DataSource.getConnection();
            connection.setCatalog(Config.getGuildId());
            PreparedStatement preparedStatement = connection.prepareStatement("select roleId from serverkeys where `key` = '" + key + "'");

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getString("roleId");
                connection.close();
            }
            connection.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }

    public static String getKeyTime(String key) {
        String result = null;
        try {
            Connection connection = DataSource.getConnection();
            connection.setCatalog(Config.getGuildId());
            PreparedStatement preparedStatement = connection.prepareStatement("select length from serverkeys where `key` = '" + key + "'");

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getString("length");
                connection.close();
            }
            connection.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }

    public static Date getUserExpireDate(String discordId) {
        Date date = new Date();
        try {
            Connection connection = DataSource.getConnection();
            connection.setCatalog(Config.getGuildId());
            PreparedStatement preparedStatement = connection.prepareStatement("select * from verifiedusers where discordId = ?");
            preparedStatement.setString(1, discordId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Timestamp timestamp = resultSet.getTimestamp("expireDate");
                if (timestamp != null) {
                    date = new Date(timestamp.getTime());
                    connection.close();
                }
            }
            connection.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return date;
    }

    public static ArrayList<Date> getUserExpireDateList(String discordId) {
        try {
            Connection connection = DataSource.getConnection();
            connection.setCatalog(Config.getGuildId());
            PreparedStatement preparedStatement = connection.prepareStatement("select * from verifiedusers where discordId = ?");
            preparedStatement.setString(1, discordId);

            ResultSet resultSet = preparedStatement.executeQuery();
            ArrayList<Date> list = new ArrayList<>();
            while (resultSet.next()) {
                Timestamp timestamp = resultSet.getTimestamp("expireDate");
                if (timestamp != null)
                    list.add(new Date(timestamp.getTime()));
            }
            connection.close();
            return list;
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public static ArrayList<String> getUserRoles(String discordId) {
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            Connection connection = DataSource.getConnection();
            connection.setCatalog(Config.getGuildId());
            PreparedStatement preparedStatement = connection.prepareStatement("select * from verifiedusers where discordId = ?");
            preparedStatement.setString(1, discordId);

            ResultSet resultSet = preparedStatement.executeQuery();
            arrayList.clear();
            while (resultSet.next()) arrayList.add(resultSet.getString("roleId"));
            connection.close();
            return arrayList;
        } catch (SQLException e) { e.printStackTrace(); }
        return arrayList;
    }

    public static boolean userExistsInDatabaseWithGuildRole(String discordId, String roleId) {
        try {
            Connection connection = DataSource.getConnection();
            connection.setCatalog(Config.getGuildId());
            PreparedStatement preparedStatement = connection.prepareStatement("select expireDate from verifiedusers where discordId = ? and roleId = ?");
            preparedStatement.setString(1, discordId);
            preparedStatement.setString(2, roleId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                connection.close();
                return true;
            }
            connection.close();
        } catch (SQLException e) { e.printStackTrace(); }

        return false;
    }

    public static boolean userExistsInDatabaseWithGuild(String discordId) {
        try {
            Connection connection = DataSource.getConnection();
            connection.setCatalog(Config.getGuildId());
            PreparedStatement preparedStatement = connection.prepareStatement("select expireDate from verifiedusers where discordId = ?");
            preparedStatement.setString(1, discordId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                connection.close();
                return true;
            }

        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
    public static void updateVerifiedUser(String expireDate, String discordId, String roleId) {
        try {
            Connection connection = DataSource.getConnection();
            connection.setCatalog(Config.getGuildId());
            PreparedStatement preparedStatement = connection.prepareStatement("update verifiedusers set expireDate = ? where discordId = ? and roleId = ?");
            preparedStatement.setString(1, expireDate);
            preparedStatement.setString(2, discordId);
            preparedStatement.setString(3, roleId);
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void addVerifiedUser(String expireDate, String discordId, String roleId) {
        try {
            Connection connection = DataSource.getConnection();
            connection.setCatalog(Config.getGuildId());
            PreparedStatement preparedStatement = connection.prepareStatement("insert into verifiedusers (discordId,roleId,expireDate) values (?,?,?)");
            preparedStatement.setString(1, discordId);
            preparedStatement.setString(2, roleId);
            preparedStatement.setString(3, expireDate);
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void deleteExpiredGuilds() {
        try {
            Connection connection = DataSource.getConnection();
            connection.setCatalog("verifus");
            connection.prepareStatement("delete from activatedservers where expireDate <= NOW()").executeUpdate();
            connection.close();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    public static void deleteExpiredUsers() {
        try {
            Connection connection = DataSource.getConnection();
            connection.setCatalog(Config.getGuildId());
            connection.prepareStatement("delete from verifiedusers where expireDate <= NOW()").executeUpdate();
            connection.close();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    public static void registerKey(String key, String length, String roleId) {
        try {
            Connection connection = DataSource.getConnection();
            connection.setCatalog(Config.getGuildId());
            PreparedStatement preparedStatement = connection.prepareStatement("insert into serverkeys (`key`,length,roleId) values (?,?,?)");
            preparedStatement.setString(1, key);
            preparedStatement.setString(2, length);
            preparedStatement.setString(3, roleId);
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    public static void removeVerifiedUser(String discordId, String roleId) {
        try {
            Connection connection = DataSource.getConnection();
            connection.setCatalog(Config.getGuildId());
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM verifiedusers WHERE discordId = ? AND role = ?");
            preparedStatement.setString(1, discordId);
            preparedStatement.setString(2, roleId);
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    public static void deleteRegisteredKey(String key) {
        try {
            Connection connection = DataSource.getConnection();
            connection.setCatalog(Config.getGuildId());
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM serverkeys WHERE `key`=?");
            preparedStatement.setString(1, key);
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void setBotStatus(String status) {
        try {
            Connection connection = DataSource.getConnection();
            connection.setCatalog(Config.getGuildId());
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE serverinfo SET botStatus=? WHERE dataColumn = 1");
            preparedStatement.setString(1, status);

            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static String getBotStatus() throws SQLException {
        Connection connection = DataSource.getConnection();
        connection.setCatalog(Config.getGuildId());
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM serverinfo WHERE dataColumn = 1");

        ResultSet resultSet = preparedStatement.executeQuery();
        String botStatus = null;

        if (resultSet.next()) botStatus = resultSet.getString("botStatus");

        connection.close();
        return botStatus;
    }
}
