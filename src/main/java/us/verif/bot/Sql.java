package us.verif.bot;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import com.stripe.model.Customer;
import com.stripe.model.Subscription;
import net.dv8tion.jda.core.entities.User;

public class Sql {


    public static void connect() {
        try (Connection connection = DriverManager.getConnection(Bot.url, Bot.user, Bot.password); Statement statement = connection.createStatement()) {
            statement.executeUpdate("create database if not exists verifus;");
            statement.executeUpdate("use verifus;");
            statement.executeUpdate("create table if not exists `activationserials` (`serial` varchar(30), `length` varchar(20));");
            statement.executeUpdate("create table if not exists `serverkeys` (`key` varchar(1900), `guildID` varchar(30), `length` varchar(20), `role` varchar(100));");
            statement.executeUpdate("create table if not exists `activatedservers` (`guildID` varchar(30), `expireDate` datetime);");
            statement.executeUpdate("create table if not exists `verifiedusers` (`userID` varchar(30), `guildID` varchar(30), `role` varchar(100), `expireDate` datetime);");
            statement.executeUpdate("create table if not exists `serverInfo` (`guildID` varchar(30) PRIMARY KEY, `stripeKey` varchar(200));");

            PreparedStatement createCustomerTable = connection.prepareStatement("CREATE TABLE IF NOT EXISTS stripeCustomers(discordId VARCHAR(100), customerId VARCHAR(100), email VARCHAR(100), guildId VARCHAR(100))");
            PreparedStatement createSubscriptionTable = connection.prepareStatement("CREATE TABLE IF NOT EXISTS stripeSubscriptions(subscriptionId VARCHAR(100) PRIMARY KEY, customerId VARCHAR(100), active VARCHAR(100))");

            createCustomerTable.executeUpdate();
            createSubscriptionTable.executeUpdate();
            createCustomerTable.close();
            createSubscriptionTable.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void execute(String type, String command) {
        try (Connection connection = DriverManager.getConnection(Bot.url, Bot.user, Bot.password); Statement statement = connection.createStatement()) {
            statement.executeUpdate("use verifus;");
            if (type.equalsIgnoreCase("update")) statement.executeUpdate(command);
            if (type.equalsIgnoreCase("")) statement.execute(command);
            if (type.equalsIgnoreCase("query")) statement.executeQuery(command);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getActivationSerialTime(String serial) {
        try (Connection connection = DriverManager.getConnection(Bot.url, Bot.user, Bot.password); Statement statement = connection.createStatement()) {
            statement.executeUpdate("use verifus;");
            ResultSet rs = statement.executeQuery("select `length` from `activationserials` where `serial` = '" + serial + "';");
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean activationSerialsHas(String serial) {
        try (Connection connection = DriverManager.getConnection(Bot.url, Bot.user, Bot.password); Statement statement = connection.createStatement()) {
            statement.executeUpdate("use verifus;");
            ResultSet rs = statement.executeQuery("select exists(select * from `activationserials` where `serial` = '" + serial + "');");
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean activatedServersHas(String guildID) {
        try (Connection connection = DriverManager.getConnection(Bot.url, Bot.user, Bot.password); Statement statement = connection.createStatement()) {
            statement.executeUpdate("use verifus;");
            ResultSet rs = statement.executeQuery("select * from `activatedservers` where `guildID` = '" + guildID + "';");
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean containsServerKey(String guildID) {
        try (Connection connection = DriverManager.getConnection(Bot.url, Bot.user, Bot.password); Statement statement = connection.createStatement()) {
            statement.executeUpdate("use verifus;");
            ResultSet rs = statement.executeQuery("select * from `serverkeys` where `guildID` = '" + guildID + "';");
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean containsKey(String key) {
        try (Connection connection = DriverManager.getConnection(Bot.url, Bot.user, Bot.password); Statement statement = connection.createStatement()) {
            statement.executeUpdate("use verifus;");
            ResultSet rs = statement.executeQuery("select * from `serverkeys` where `key` = '" + key + "';");
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Date activationExpireDate(String guildID) {
        try (Connection connection = DriverManager.getConnection(Bot.url, Bot.user, Bot.password); Statement statement = connection.createStatement()) {
            statement.executeUpdate("use verifus;");
            ResultSet rs = statement.executeQuery("select `expireDate` from `activatedservers` where `guildID` = '" + guildID + "';");
            if (rs.next()) {
                Timestamp timestamp = rs.getTimestamp("expireDate");
                if (timestamp != null)
                    return new Date(timestamp.getTime());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getKeyRole(String key) {
        try (Connection connection = DriverManager.getConnection(Bot.url, Bot.user, Bot.password); Statement statement = connection.createStatement()) {
            statement.executeUpdate("use verifus;");
            ResultSet rs = statement.executeQuery("select `role` from `serverkeys` where `key` = '" + key + "';");
            if (rs.next()) {
                return rs.getString("role");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getKeyTime(String key) {
        try (Connection connection = DriverManager.getConnection(Bot.url, Bot.user, Bot.password); Statement statement = connection.createStatement()) {
            statement.executeUpdate("use verifus;");
            ResultSet rs = statement.executeQuery("select `length` from `serverkeys` where `key` = '" + key + "';");
            if (rs.next()) {
                return rs.getString("length");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date getUserExpireDate(String user, String guildID) {
        try (Connection connection = DriverManager.getConnection(Bot.url, Bot.user, Bot.password); Statement statement = connection.createStatement()) {
            statement.executeUpdate("use verifus;");
            ResultSet rs = statement.executeQuery("select * from `verifiedusers` where `guildID` = '" + guildID + "' and `userID` = '" + user + "';");
            if (rs.next()) {
                Timestamp timestamp = rs.getTimestamp("expireDate");
                if (timestamp != null)
                    return new Date(timestamp.getTime());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Date> getUserExpireDateList(String user, String guildID) {
        try (Connection connection = DriverManager.getConnection(Bot.url, Bot.user, Bot.password); Statement statement = connection.createStatement()) {
            statement.executeUpdate("use verifus;");
            ResultSet rs = statement.executeQuery("select * from `verifiedusers` where `guildID` = '" + guildID + "' and `userID` = '" + user + "';");
            ArrayList<Date> list = new ArrayList<>();
            while (rs.next()) {
                Timestamp timestamp = rs.getTimestamp("expireDate");
                if (timestamp != null)
                    list.add(new Date(timestamp.getTime()));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<String> getUserRoles(String user, String guildID) {
        try (Connection connection = DriverManager.getConnection(Bot.url, Bot.user, Bot.password); Statement statement = connection.createStatement()) {
            statement.executeUpdate("use verifus;");
            ResultSet rs = statement.executeQuery("select * from `verifiedusers` where `guildID` = '" + guildID + "' and `userID` = '" + user + "';");
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.clear();
            while (rs.next()) {
                arrayList.add(rs.getString("role"));
            }
            return arrayList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getGuildFromKey(String key) {
        try (Connection connection = DriverManager.getConnection(Bot.url, Bot.user, Bot.password); Statement statement = connection.createStatement()) {
            statement.executeUpdate("use verifus;");
            ResultSet rs = statement.executeQuery("select `guildID` from `serverkeys` where `key` = '" + key + "';");
            if (rs.next()) {
                return rs.getString("guildID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean userExistsInDatabaseWithGuildRole(String user, String guildID, String role) {
        try (Connection connection = DriverManager.getConnection(Bot.url, Bot.user, Bot.password); Statement statement = connection.createStatement()) {
            statement.executeUpdate("use verifus;");
            ResultSet rs = statement.executeQuery("select `expireDate` from `verifiedusers` where `guildID` = '" + guildID + "' and `userID` = '" + user + "' and `role` = '" + role + "';");
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean userExistsInDatabaseWithGuild(String user, String guildID) {
        try (Connection connection = DriverManager.getConnection(Bot.url, Bot.user, Bot.password); Statement statement = connection.createStatement()) {
            statement.executeUpdate("use verifus;");
            ResultSet rs = statement.executeQuery("select `expireDate` from `verifiedusers` where `guildID` = '" + guildID + "' and `userID` = '" + user + "';");
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    //STRIPE
    public static String getGuildFromStripeKey(String stripeKey) {
        try (Connection connection = DriverManager.getConnection(Bot.url, Bot.user, Bot.password); Statement statement = connection.createStatement()) {
            statement.executeUpdate("use verifus;");
            ResultSet rs = statement.executeQuery("select `guildID` from `serverinfo` where `stripeKey` = '" + stripeKey + "';");
            if (rs.next()) {
                return rs.getString("guildID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static ArrayList<String> stripeKeyList() {
        try (Connection connection = DriverManager.getConnection(Bot.url, Bot.user, Bot.password); Statement statement = connection.createStatement()) {
            statement.executeUpdate("use verifus;");
            ResultSet rs = statement.executeQuery("select `stripeKey` from `serverinfo`;");
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.clear();
            while (rs.next()) {
                arrayList.add(rs.getString("stripeKey"));
            }
            return arrayList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getStripeKeyFromGuild(String guildID) {
        try (Connection connection = DriverManager.getConnection(Bot.url, Bot.user, Bot.password); Statement statement = connection.createStatement()) {
            statement.executeUpdate("use verifus;");
            ResultSet rs = statement.executeQuery("select `stripeKey` from `serverInfo` where `guildID` = '" + guildID + "';");
            if (rs.next()) {
                return rs.getString("stripeKey");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void addCustomer(User user, String customerId, String email, String guildId) throws SQLException {
        Connection connection = DriverManager.getConnection(Bot.url, Bot.user, Bot.password);
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO stripeCustomers (discordId, customerId, email, guildId) VALUES (?,?,?,?)");
        preparedStatement.setString(1, user.getId());
        preparedStatement.setString(2, customerId);
        preparedStatement.setString(3, email);
        preparedStatement.setString(4, guildId);

        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    public static String getCustomerByEmail(String email) throws SQLException {
        Connection connection = DriverManager.getConnection(Bot.url, Bot.user, Bot.password);
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM stripeCustomers WHERE email=?");
        preparedStatement.setString(1, email);

        ResultSet resultSet = preparedStatement.executeQuery();
        String customer = null;

        if (resultSet.next()) customer = resultSet.getString("customerId");

        resultSet.close();
        preparedStatement.close();
        return customer;
    }


    public static String getCustomerIdByDiscord(String discordId) throws SQLException {
        Connection connection = DriverManager.getConnection(Bot.url, Bot.user, Bot.password);
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM stripeCustomers WHERE discordId=?");
        preparedStatement.setString(1, discordId);

        ResultSet resultSet = preparedStatement.executeQuery();
        String customer = null;

        if (resultSet.next()) customer = resultSet.getString("customer");

        resultSet.close();
        preparedStatement.close();
        return customer;
    }

    public static String getDiscordIdByCustomer(String customer) throws SQLException {
        Connection connection = DriverManager.getConnection(Bot.url, Bot.user, Bot.password);
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM stripeCustomers WHERE customer=?");
        preparedStatement.setString(1, customer);

        ResultSet resultSet = preparedStatement.executeQuery();
        String discordId = null;

        if (resultSet.next()) discordId = resultSet.getString("discordId");

        resultSet.close();
        preparedStatement.close();
        return discordId;
    }

    public static Collection<String> getSubscriptions() throws SQLException {
        Connection connection = DriverManager.getConnection(Bot.url, Bot.user, Bot.password);
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM stripeSubscriptions WHERE active=true");

        ResultSet resultSet = preparedStatement.executeQuery();
        Collection<String> subscriptions = new ArrayList<>();

        while (resultSet.next()) {
            subscriptions.add(resultSet.getString("subscriptionId"));
        }

        resultSet.close();
        preparedStatement.close();
        return subscriptions;
    }

    public static void registerSubscription(Subscription subscription, Customer customer) throws SQLException {
        Connection connection = DriverManager.getConnection(Bot.url, Bot.user, Bot.password);
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO stripeSubscriptions (subscriptionId, customerId, active) VALUES (?, ?, ?)");
        preparedStatement.setString(1, subscription.getId());
        preparedStatement.setString(2, customer.getId());
        preparedStatement.setBoolean(3, true);

        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    public static void setSubscriptionActive(String subscriptionId, boolean active) throws SQLException {
        Connection connection = DriverManager.getConnection(Bot.url, Bot.user, Bot.password);
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE stripeSubscriptions active=? WHERE subscriptionId=?");
        preparedStatement.setBoolean(1, active);
        preparedStatement.setString(2, subscriptionId);

        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    public static boolean subscriptionRegistered(Subscription subscription) throws SQLException {
        Connection connection = DriverManager.getConnection(Bot.url, Bot.user, Bot.password);
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM stripeSubscriptions WHERE subscriptionId=?");
        preparedStatement.setString(1, subscription.getId());

        ResultSet resultSet = preparedStatement.executeQuery();
        boolean registered = false;

        if (resultSet.next()) registered = true;

        resultSet.close();
        preparedStatement.close();
        return registered;
    }
}
