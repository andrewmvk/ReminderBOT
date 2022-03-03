package rmd.sequelize;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import io.github.cdimascio.dotenv.Dotenv;

public class Start {
    public static Connection connecting() throws SQLException, IOException {

        Dotenv dotenv = null;
        dotenv = Dotenv.configure().load();
        //Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/discordbot", "postgres", "1234567");
        Properties prop = readPropertiesFile("application.properties");
        Connection connection = DriverManager.getConnection("jdbc:postgresql://ec2-54-147-93-73.compute-1.amazonaws.com:5432/d2f7u98g6oo9q7", "ibwopmmesblhiy", "e2ce7681f7d0bfcc6cb3f857449c8abec3adf1ca812b0121218e21ebe617abb3");

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
