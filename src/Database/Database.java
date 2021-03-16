package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    /**
     * A series of strings that allow me to connect to the WGU SQL Database.
     */
    private static final String DBNAME ="WJ07IyN";
    private static final String DB_URL ="jdbc:mysql://wgudb.ucertify.com:3306/" + DBNAME;
    private static final String USERNAME ="U07IyN";
    private static final String PASSWORD ="53689036516";
    private static final String JDBC_DRIVER ="com.mysql.jdbc.Driver";
    public static Connection connection;

    /**
     * Creates a connection to the database. Includes a lambda that prints out successful connections to the database, which is helpful to the user.
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static Connection createConnection() throws ClassNotFoundException, SQLException{
        Class.forName(JDBC_DRIVER);
        System.out.println("Connecting...");
        connection = (Connection)DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
        new Thread(() -> System.out.println("Connection successful!")).start();
        return connection;
    }

    /**
     * Returns the connection
     * @return
     */
    public static Connection getConnection(){
        return connection;
    }

    /**
     * Closes the connection to the database.Includes a lambda that prints after successfully closing the connection, which is helpful to the user.
     *
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static void closeConnection() throws ClassNotFoundException, SQLException{
        if(connection != null) {
            connection.close();
            //Lambda expression that prints after successfully closing the connection.
            new Thread(() -> System.out.println("Connection closed!")).start();
        }
    }
}
