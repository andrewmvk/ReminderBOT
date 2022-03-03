package rmd.sequelize;

import rmd.date.Time;
import rmd.reminding.Reminding;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;

public class Select {
    public static String[] select(Long messagesID, Long serverID) throws SQLException, IOException, URISyntaxException {
        Connection connection = Start.connecting();
        Statement statementSelect = connection.createStatement();
        ResultSet result = statementSelect.executeQuery(Reminding.selectMessage + messagesID + "AND server_id=" + serverID );

        if(!result.next()) {
            connection.close();
            return null;
        } else {
            String[] message = new String[4];
            message[0] = result.getString("title");
            message[1] = result.getString("date");
            message[2] = result.getString("description");
            message[3] = result.getString("author");
            connection.close();
            result.close();
            return message;
        }
    }
    public static String[][] selectMessages(Long serverID) throws SQLException, ParseException, IOException, URISyntaxException {
        Connection connection = Start.connecting();
        Statement statementSelect = connection.createStatement();

        ResultSet count = statementSelect.executeQuery("SELECT COUNT(*) FROM serversmessages WHERE server_id=" + serverID );

        count.next();
        if(count.getInt(1)==0) {
            connection.close();
            return null;
        } else {
            int i = count.getInt(1);
            count.close();
            ResultSet result = statementSelect.executeQuery(Reminding.selectMessages + serverID );
            int j = 0;
            String[][] messages = new String[i][4];
            String temp;
            long m=804000;
            long[] daysLeft = new long[i];
            while (result.next()) {
                messages[j][0] = result.getString("title");
                messages[j][1] = result.getString("date");
                messages[j][2] = String.valueOf(result.getLong("messages_id"));
                messages[j][3] = result.getString("role");
                if (messages[j][1] != null) {
                    daysLeft[j] = Time.daysLeft(messages[j][1]);
                } else {
                    daysLeft[j] = m;
                    m--;
                }
                j++;
            }
            for (int q=0; q<daysLeft.length; q++) {
                for(int w=q+1; w<daysLeft.length; w++) {
                    if(daysLeft[q]>daysLeft[w]) {
                        for(int l=0; l<4; l++) {
                            temp = messages[q][l];
                            messages[q][l] = messages[w][l];
                            messages[w][l] = temp;
                        }
                        temp = daysLeft[q] + "";
                        daysLeft[q] = daysLeft[w];
                        daysLeft[w] = Long.parseLong(temp);
                    }
                }
            }
            connection.close();
            result.close();
            return messages;
        }
    }
    public static String[][] selectALLMessages () throws SQLException, IOException, URISyntaxException {
        Connection connection = Start.connecting();
        Statement statementSelect = connection.createStatement();
        ResultSet count = statementSelect.executeQuery("SELECT COUNT(*) FROM serversmessages");

        count.next();
        int i = count.getInt(1);
        count.close();

        String[][] allMessages = new String[i][6];
        ResultSet result = statementSelect.executeQuery(Reminding.select);

        int j=0;
        while (result.next()) {
            allMessages[j][0] = result.getString("messages_id");
            allMessages[j][1] = result.getString("server_id");
            allMessages[j][2] = result.getString("channel_id");
            allMessages[j][3] = result.getString("date");
            allMessages[j][4] = result.getString("title");
            allMessages[j][5] = result.getString("description");
            j++;
        }

        result.close();
        connection.close();
        return allMessages;
    }
}
