package us.verif.bot.sql;

import us.verif.bot.Config;
import us.verif.bot.DataSource;

import java.sql.*;

public class StripeSql {
    public static String getStripeApiKey() {
        String key = null;
        try {
            Connection connection = DataSource.getDataSource().getConnection();
            connection.setCatalog(Config.getGuildId());
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM serverinfo");
            if (rs.next()) {
                key = rs.getString("stripeKey");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return key;
    }

    public static boolean keyRegistered(String key) throws SQLException {
        Connection connection = DataSource.getDataSource().getConnection();
        connection.setCatalog(Config.getGuildId());
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM stripeusers WHERE `key`=?");
        preparedStatement.setString(1, key);

        ResultSet resultSet = preparedStatement.executeQuery();
        boolean registered = false;

        if (resultSet.next()) registered = true;


        return registered;
    }

    public static boolean containsStripeKey(String key) throws SQLException {
        Connection connection = DataSource.getDataSource().getConnection();
        connection.setCatalog(Config.getGuildId());
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM stripekeys WHERE `key`=?");
        preparedStatement.setString(1, key);

        ResultSet resultSet = preparedStatement.executeQuery();
        boolean contains = false;

        if (resultSet.next()) contains = true;


        return contains;
    }

    public static void registerKey(String key, String subscriptionId, String roleId) throws SQLException {
        Connection connection = DataSource.getDataSource().getConnection();
        connection.setCatalog(Config.getGuildId());
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO stripekeys (`key`, subscriptionId, roleId) VALUES (?, ?, ?)");
        preparedStatement.setString(1, key);
        preparedStatement.setString(2, subscriptionId);
        preparedStatement.setString(3, roleId);

        preparedStatement.executeUpdate();

    }

    public static String getRoleByStripeKey(String key) throws SQLException {
        Connection connection = DataSource.getDataSource().getConnection();
        connection.setCatalog(Config.getGuildId());
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM stripekeys WHERE `key`=?");
        preparedStatement.setString(1, key);

        ResultSet resultSet = preparedStatement.executeQuery();
        String roleId = null;

        if (resultSet.next()) roleId = resultSet.getString("roleId");


        return roleId;
    }

    public static String getSubscriptionByStripeKey(String key) throws SQLException {
        Connection connection = DataSource.getDataSource().getConnection();
        connection.setCatalog(Config.getGuildId());
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM stripekeys WHERE `key`=?");
        preparedStatement.setString(1, key);

        ResultSet resultSet = preparedStatement.executeQuery();
        String subscriptionId = null;

        if (resultSet.next()) subscriptionId = resultSet.getString("subscriptionId");


        return subscriptionId;
    }

    public static boolean userHasStripeKey(String key) throws SQLException {
        Connection connection = DataSource.getDataSource().getConnection();
        connection.setCatalog(Config.getGuildId());
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM stripeusers WHERE `key`=?");
        preparedStatement.setString(1, key);

        ResultSet resultSet = preparedStatement.executeQuery();
        boolean contains = false;

        if (resultSet.next()) contains = true;


        return contains;
    }

    public static String getUserByStripeKey(String key) throws SQLException {
        Connection connection = DataSource.getDataSource().getConnection();
        connection.setCatalog(Config.getGuildId());
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM stripeusers where `key`=?");
        preparedStatement.setString(1, key);

        ResultSet resultSet = preparedStatement.executeQuery();
        String user = null;

        if (resultSet.next()) user = resultSet.getString("discordId");


        return user;
    }

    public static String getKeyBySubscription(String subscriptionId) throws SQLException {
        Connection connection = DataSource.getDataSource().getConnection();
        connection.setCatalog(Config.getGuildId());
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM stripekeys where subscriptionId=?");
        preparedStatement.setString(1, subscriptionId);

        ResultSet resultSet = preparedStatement.executeQuery();
        String key = null;

        if (resultSet.next()) key = resultSet.getString("key");


        return key;
    }

    public static boolean subscriptionExists(String subscriptionId) throws SQLException {
        Connection connection = DataSource.getDataSource().getConnection();
        connection.setCatalog(Config.getGuildId());
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM stripekeys where subscriptionId=?");
        preparedStatement.setString(1, subscriptionId);

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {

            return true;
        }

        return false;
    }

    public static String getRoleByKey(String key) throws SQLException {
        Connection connection = DataSource.getDataSource().getConnection();
        connection.setCatalog(Config.getGuildId());
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM stripekeys where `key`=?");
        preparedStatement.setString(1, key);

        ResultSet resultSet = preparedStatement.executeQuery();
        String role = null;

        if (resultSet.next()) role = resultSet.getString("roleId");


        return role;
    }

    public static void removeKeyAndStripeUser(String key) throws SQLException {
        Connection connection = DataSource.getDataSource().getConnection();
        connection.setCatalog(Config.getGuildId());
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM stripekeys where `key`=?");
        preparedStatement.setString(1, key);
        preparedStatement.executeUpdate();
        PreparedStatement preparedStatement2 = connection.prepareStatement("DELETE FROM stripeusers where `key`=?");
        preparedStatement2.setString(1, key);
        preparedStatement2.executeUpdate();
    }

    public static void removeStripeKey(String key) throws SQLException {
        Connection connection = DataSource.getDataSource().getConnection();
        connection.setCatalog(Config.getGuildId());
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM stripekeys where `key`=?");
        preparedStatement.setString(1, key);
        preparedStatement.executeUpdate();
    }

    public static void unbindKey(String discordId, String key) {
        try {
            Connection connection = DataSource.getDataSource().getConnection();
            connection.setCatalog(Config.getGuildId());
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM stripeusers where discordId=? AND `key`=?");
            preparedStatement.setString(1, discordId);
            preparedStatement.setString(2, key);

            preparedStatement.executeUpdate();

        } catch (SQLException e) { e.printStackTrace(); }
    }
    public static void registerStripeUser(String discordId, String key) {
        try {
            Connection connection = DataSource.getDataSource().getConnection();
            connection.setCatalog(Config.getGuildId());
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO stripeusers (discordId, `key`) VALUES (?,?)");
            preparedStatement.setString(1, discordId);
            preparedStatement.setString(2, key);

            preparedStatement.executeUpdate();

        } catch (SQLException e) { e.printStackTrace(); }
    }
}
