package rmd.sequelize;

import org.apache.commons.dbcp.BasicDataSource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Start {
    public static Connection connecting() throws SQLException, IOException, URISyntaxException {

        if(System.getenv("SPRING_DATASOURCE_USERNAME")!=null) {
            URI dbUri = new URI(System.getenv("URL"));

            String username = dbUri.getUserInfo().split(":")[0];
            System.out.println("Usuário: "+username);
            String password = dbUri.getUserInfo().split(":")[1];
            System.out.println("Senha: "+password);
            String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + dbUri.getPath();
            System.out.println("URL: "+dbUrl);

            /*BasicDataSource basicDataSource = dataSource(dbUrl, username, password);*/
            Connection connection = DriverManager.getConnection(dbUrl, username, password);
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
    public static BasicDataSource dataSource(String dbUrl, String username, String password) {
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(dbUrl);
        basicDataSource.setUsername(username);
        basicDataSource.setPassword(password);

        return basicDataSource;
    }
}
