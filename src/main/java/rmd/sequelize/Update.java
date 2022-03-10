package rmd.sequelize;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Update {
    public static void updateTitle (Long messageId, String title, Long serverID, Long channelID) throws SQLException, IOException, URISyntaxException {
        Connection connection = Start.connecting();
        PreparedStatement statement = connection.prepareStatement(
                "UPDATE serversmessages SET title='"+ title
                        +"' "+"WHERE messages_id=" + messageId
                        +"AND server_id="+serverID
                );

        statement.execute();
        connection.close();
        statement.close();
    }
    public static void updateDate(Long messageId, String date, Long serverID, Long channelID) throws SQLException, IOException, URISyntaxException {
        Connection connection = Start.connecting();
        PreparedStatement statement = connection.prepareStatement(
                "UPDATE serversmessages SET date='"+ date
                        +"' "+"WHERE messages_id=" + messageId
                        +"AND server_id="+serverID
                );

        statement.execute();
        connection.close();
        statement.close();
    }
    public static void updateDescription(Long messageId, String description, Long serverID, Long channelID) throws SQLException, IOException, URISyntaxException {
        Connection connection = Start.connecting();
        PreparedStatement statement = Start.connecting().prepareStatement(
                "UPDATE serversmessages SET description='"+ description
                        +"' "+"WHERE messages_id=" + messageId
                        +"AND server_id="+serverID
                );

        statement.execute();
        connection.close();
        statement.close();
    }
    public static void updateRole (String role ,Long serverID) throws SQLException, IOException, URISyntaxException {
        Connection connection = Start.connecting();
        PreparedStatement statement = connection.prepareStatement(
                "UPDATE serversmessages SET role='" + role + "'"
                        + "WHERE server_id=" + serverID);

        statement.execute();
        connection.close();
        statement.close();
    }
    public static void updateDuration (int duration, Long serverID, long messageId) throws SQLException, IOException, URISyntaxException {
        Connection connection = Start.connecting();
        PreparedStatement statement = Start.connecting().prepareStatement(
                "UPDATE serversmessages SET duration='"+ duration
                        +"' "+"WHERE messages_id=" + messageId
                        +"AND server_id="+serverID
        );

        statement.execute();
        connection.close();
        statement.close();
    }
}
