package rmd.sequelize;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Start {
    public static Connection connecting() throws SQLException, IOException {

        //Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/discordbot", "postgres", "1234567");
        Properties prop = readPropertiesFile("application.properties");
        System.out.println(prop);
        Connection connection = DriverManager.getConnection("jdbc:"+prop.get("db.url"), prop.getProperty("db.username"), prop.getProperty("db.password"));

        return connection;
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
