package rmd.sequelize;

import rmd.reminding.Reminding;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.*;

public class Insert {
    public static Long create(@Nonnull Long serverID,@Nonnull Long channelID,
                              @Nullable String title,@Nullable String description,@Nullable String date,
                              @Nonnull String author) throws SQLException, IOException, URISyntaxException {
        Connection connection = Start.connecting();
        PreparedStatement statement = connection.prepareStatement(Reminding.sql);
        Statement statementSelect = connection.createStatement();

        int rows = 0;

        statement.setLong(1, serverID);
        statement.setLong(2, channelID);
        statement.setString(3, title);
        statement.setString(4, description);
        statement.setString(5, date);
        statement.setString(6, author);

        rows = statement.executeUpdate();
        if (rows>0) {
            System.out.println("Evento criado!");
        }

        ResultSet result = statementSelect.executeQuery(Reminding.selectId);
        result.next();
        long messageID = result.getLong(1);
        result.close();

        connection.close();
        return messageID;
    }
}
