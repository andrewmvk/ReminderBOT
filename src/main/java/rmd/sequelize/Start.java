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
            Connection connection = DriverManager.getConnection("postgres://ec2-54-147-93-73.compute-1.amazonaws.com:5432/d2f7u98g6oo9q7", System.getenv("JDBC_DATABASE_USERNAME"), System.getenv("JDBC_DATABASE_PASSWORD"));
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
