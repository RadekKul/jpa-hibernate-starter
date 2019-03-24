package pl.sda.jdbc.starter;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {
    private static Logger logger = LoggerFactory.getLogger(ConnectionFactory.class);

    private DataSource dataSource;

    // tworzymy dwa konstruktory jeden domyslny ktory wywola drugi z domyslna sciezka do wlasciwosci database, a drugi ktory przyjmnie sciezke do pliku z wlasciwosciami gdybysmy chcieli zmienic database

    public ConnectionFactory(String path) throws SQLException {
        Properties properties = getDataBaseProperties(path);    // na podstawie tych properties z "path" stworzymy dataSource
        dataSource = createDataSource(properties);
    }

    public ConnectionFactory() throws SQLException {
        this("/database.properties");   // ta sciezka zaczyna szukac od resources, skoro tam mamy plik database.properties to nie musimy dawac wiekszej sciezki
    }

    private DataSource createDataSource(Properties properties) throws SQLException {
        MysqlDataSource dataSource;

        String serverName = properties.getProperty("pl.sda.jdbc.db.server");    // tutaj bierzemy key (ktorym jest nazwa z database.properties w resources) ponizej robimy to samo z innymi polami
        String databaseName = properties.getProperty("pl.sda.jdbc.db.name");
        String user = properties.getProperty("pl.sda.jdbc.db.user");
        String password = properties.getProperty("pl.sda.jdbc.db.password");
        String port = properties.getProperty("pl.sda.jdbc.db.port");

        // i tutaj zmieniamy te wlasciwosci dla naszej bazy


            dataSource = new MysqlDataSource();
            dataSource.setServerName(serverName);
            dataSource.setDatabaseName(databaseName);
            dataSource.setUser(user);
            dataSource.setPassword(password);
            dataSource.setPort(Integer.parseInt(port)); // parsujemy String na int
            dataSource.setServerTimezone("Europe/Warsaw");
            dataSource.setUseSSL(false);
            dataSource.setCharacterEncoding("UTF-8");

            return dataSource;
    }


    private Properties getDataBaseProperties(String filename) {
        Properties properties = new Properties();
        try {
            /**
             * Pobieramy zawartość pliku za pomocą classloadera, plik musi znajdować się w katalogu ustawionym w CLASSPATH
             */
            InputStream propertiesStream = ConnectionFactory.class.getResourceAsStream(filename);
            if(propertiesStream == null) {
                throw new IllegalArgumentException("Can't find file: " + filename);
            }
            /**
             * Pobieramy dane z pliku i umieszczamy w obiekcie klasy Properties
             */
            properties.load(propertiesStream);
        } catch (IOException e) {
            logger.error("Error during fetching properties for database", e);
            return null;
        }

        return properties;
    }

    public Connection getConnection() throws SQLException {
        if(dataSource ==null){
            throw new IllegalStateException("Data source must be created");
        }

        return dataSource.getConnection();
    }

    public static void main(String[] args) {


        try {
            ConnectionFactory connectionFactory = new ConnectionFactory("remote-database.properties");
            try(Connection connection = connectionFactory.getConnection()){
                logger.info("Connected database successfully...");
                logger.info("Connection = " + connection);
                logger.info("Database name = " + connection.getCatalog());
            }
        } catch (SQLException e) {
            logger.error("Exception during connection");
        }
    }
}