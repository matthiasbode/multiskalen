/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Diese Klasse stellt eine Methode zum Erstellen einer Verbindung zu einer
 * Datenbank über JDBC zur Verfügung.
 *
 * @author Matthias
 */
public class JDBCConnect {

    static final String DATABASE = "BLU";
    static final String HOST = "130.75.120.66";
    static final String USER = "transshipment";
    static final String PASSWD = "KWTSonne&Schnee";
    static final String PORT = "1433";
    static final String JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    static final String JDBC_URL = "jdbc:sqlserver://" + HOST + ":" + PORT + ";";

    /**
     * Methode zum Erzeugen eines Verbindungsobjekts.
     *
     * @return Objekt, mit dem später weitergearbeitet werden kann.
     */
    public static Connection getConnection() throws ClassNotFoundException, SQLException {

        // Treiber registrieren
        // hier: Connector-J fuer MYSQL
        System.out.println("Binde JDBC-Treiber ein: " + JDBC_DRIVER);
        Class.forName(JDBC_DRIVER);

        // Verbindungsobjekt als String ' [host][:port]/[database] ' angeben
        System.out.println("Verbinde mit " + JDBC_URL);
        Connection con = DriverManager.getConnection(JDBC_URL
                + "database=" + DATABASE + ";"
                + "user=" + USER + ";"
                + "password=" + PASSWD);
        System.out.println("Verbindung wurde erstellt.");
        return con;
    }

    /**
     * zum Testen der Connection
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            getConnection();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
