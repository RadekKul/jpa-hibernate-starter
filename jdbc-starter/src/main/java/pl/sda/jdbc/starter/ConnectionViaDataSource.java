package pl.sda.jdbc.starter;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionViaDataSource {
    private static Logger logger = LoggerFactory.getLogger(ConnectionViaDataSource.class);  // logger to jest taki edytor tekstu jak sout tylko mozemy ustawiac typy np error waringn itp
    // jest to przydatne zeby sie latwije komunikowac i pokazywac gdzie blad, gdzie ostrzezenie, gdzie info


    private static final String DB_SERVER_NAME = "localhost";
    private static final String DB_NAME = "sakila";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";
    private static final int DB_PORT = 3306;

    public static void main(String[] args) {

//        logger.info("tutaj tekst z loggera");
//        System.out.println("tutaj teskt z sysouta");

        /**
         * Krok 1: Tworzymy obiekt klasy DataSource
         */
        MysqlDataSource dataSource;
        try {
            dataSource = new MysqlDataSource();
            dataSource.setServerName(DB_SERVER_NAME);
            dataSource.setDatabaseName(DB_NAME);
            dataSource.setUser(DB_USER);
            dataSource.setPassword(DB_PASSWORD);
            dataSource.setPort(DB_PORT);
            dataSource.setServerTimezone("Europe/Warsaw");
            dataSource.setUseSSL(false);
            dataSource.setCharacterEncoding("UTF-8");
        } catch (SQLException e) {
            logger.error("Error during creating MysqlDataSource", e);
            return;
        }

        logger.info("Connecting to a selected database...");

        /**
         * Krok 2: Otwieramy połączenie do bazy danych
         */
        //Connection connection = null;
        try (Connection connection = dataSource.getConnection()){   // dzieki try with resources try zrobi nam connection i automatycznie zamknie - zabezpieczenie zeby nie zapomniec zamknac polaczenia
            //connection = dataSource.getConnection();
            logger.info("Connected database successfully...");

            /**
             * Krok 3: Pobieramy informacje o bazie danych i połączeniu
             */
            logger.info("Connection = " + connection);
            logger.info("Database name = " + connection.getCatalog());
        } catch (SQLException e) {
            /**
             * Krok 4: Obsługa wyjątków które mogą pojawić się w trakcie pracy z bazą danych
             */
            logger.error("Error during using connection", e);
        } /*finally { // polaczenie z baza danych zawsze trzeba zamknac w taki sposob jak tu w bloku finally, lub przez try with resources
            *//**
             * Krok 5: Zawsze zamykamy połączenie po skończonej pracy!
             *//*
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                logger.error("Error during closing connection", e);
            }
        }*/
    }
}
