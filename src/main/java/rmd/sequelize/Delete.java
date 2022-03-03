package rmd.sequelize;

import rmd.reminding.Reminding;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Delete {
    public static String[] delete(Long messageID, Long serverID, Long channelID) throws SQLException, IOException {

        Connection connection = Start.connecting();
        Statement statementSelect = connection.createStatement();

        ResultSet result = statementSelect.executeQuery(Reminding.delete + messageID + "AND server_id=" + serverID + " RETURNING *");

        if(!result.next()) {
            connection.close();
            return null;
        } else {
            String[] deleted = new String[5];
            deleted[0] = result.getString("title");
            deleted[1] = result.getString("date");
            deleted[2] = result.getString("description");
            deleted[3] = result.getString("author");
            deleted[4] = messageID.toString();
            result.close();
            connection.close();
            return deleted;
        }
    }
}
