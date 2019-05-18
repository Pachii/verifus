package us.verif.bot;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DataSource {
    private static us.verif.bot.DataSource dataSourceClass;
    private final BasicDataSource dataSource = new BasicDataSource();
    private Connection conn;

    public DataSource() {
        dataSourceClass = this;

        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=EST&allowPublicKeyRetrieval=true");
        dataSource.setUsername("root");
        dataSource.setPassword("Verifus168");

        try {
            this.conn = DataSource.getDataSource().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the DataSource class
     * Should be used to access the database connection
     * @return
     */
    public static DataSource getDataSource() {
        return dataSourceClass;
    }

    /**
     * Gets the database connection
     * Connection doesn't need to be closed
     * @return
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {
        conn = (conn == null) ? DataSource.getDataSource().getConnection() : conn;

        return conn;
    }
}