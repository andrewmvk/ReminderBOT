package rmd.sequelize;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class Start {
    public static Connection connecting() throws SQLException, IOException {

        if(System.getenv("SPRING_DATASOURCE_USERNAME")!=null) {
            String dataBaseURL = System.getenv("SPRING_DATASOURCE_URL");
            System.out.println("USERNAME: "+System.getenv("JDBC_DATABASE_USERNAME"));
            System.out.println("USERNAME: "+System.getenv("JDBC_DATABASE_PASSWORD"));
            String dbUrl = System.getenv("JDBC_DATABASE_URL");
            Connection connection = DriverManager.getConnection(dbUrl);
            return connection;
        } else {
            Properties prop = readPropertiesFile("application.properties");
            Connection connection = DriverManager.getConnection(prop.getProperty("db_url"), prop.getProperty("db_username"), prop.getProperty("db_password"));
            return connection;
        }

    }
    public static Properties readPropertiesFile(String fileName) throws IOException {
        FileInputStream fis = null;
        Properties prop = null;
        try {
            fis = new FileInputStream(fileName);
            prop = new Properties();
            prop.load(fis);
        } catch(FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        } finally {
            fis.close();
        }
        return prop;
    }
}
