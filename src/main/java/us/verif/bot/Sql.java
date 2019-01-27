package us.verif.bot;

import java.sql.*;
import java.util.ArrayList;

public class Sql {


    public static void connect() {
        try (Connection connection = DriverManager.getConnection(Bot.url, Bot.user, Bot.password); Statement statement = connection.createStatement()) {
            statement.executeUpdate("create database if not exists verifus;");
            statement.executeUpdate("use verifus;");
            statement.executeUpdate("create table if not exists `activationserials` (`serial` varchar(30), `length` varchar(20));");
            statement.executeUpdate("create table if not exists `serverkeys` (`key` varchar(1900), `guildID` varchar(30), `length` varchar(20), `role` varchar(100));");
            statement.executeUpdate("create table if not exists `activatedservers` (`guildID` varchar(30), `expireDate` datetime);");
            statement.executeUpdate("create table if not exists `verifiedusers` (`userID` varchar(30), `guildID` varchar(30), `role` varchar(100), `expireDate` datetime);");
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

    public static boolean keyIsForGuild(String key, String guildID) {
        try (Connection connection = DriverManager.getConnection(Bot.url, Bot.user, Bot.password); Statement statement = connection.createStatement()) {
            statement.executeUpdate("use verifus;");
            ResultSet rs = statement.executeQuery("select `guildID` from `serverkeys` where `key` = '" + key + "' and `guildID` = '" + guildID + "';");
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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
}
