package rmd.sequelize;

import rmd.reminding.Reminding;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Insert {
    public static Long create(@Nonnull Long serverID,@Nonnull Long channelID,
                              @Nullable String title,@Nullable String description,@Nullable String date,
                              @Nonnull String author) throws SQLException, IOException {
        PreparedStatement statement = Start.connecting().prepareStatement(Reminding.sql);
        Statement statementSelect = Start.connecting().createStatement();

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
        return messageID;
    }
}
