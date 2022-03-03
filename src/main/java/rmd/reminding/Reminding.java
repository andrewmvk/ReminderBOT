package rmd.reminding;

import rmd.date.Time;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import rmd.commands.Commands;
import rmd.date.Today;
import rmd.sequelize.Delete;
import rmd.sequelize.Select;
import rmd.sequelize.Start;

import javax.security.auth.login.LoginException;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static rmd.sequelize.Start.readPropertiesFile;

public class Reminding {

    public static String prefix = "!!";
    public static String delete = "DELETE FROM serversmessages WHERE messages_id=";
    public static String selectMessage = "SELECT * FROM serversmessages WHERE messages_id=";
    public static String selectMessages = "SELECT messages_id,title,date,role FROM serversmessages WHERE server_id=";
    public static String select = "SELECT * FROM serversmessages";
    public static String selectId = "SELECT MAX(messages_id) FROM serversmessages";
    public static String sql = "INSERT INTO serversmessages (server_id, channel_id, title, description, date, author) VALUES (?, ?, ?, ?, ?, ?)";
    public static Date dataDuasSemanas, dataUmaSemana, dataUmDia;

    public static void main(String[] args) throws LoginException, SQLException, IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Calendar hoje = Calendar.getInstance();

        try {
            Start.connecting().close();
            System.out.println("Connected to the PostgreSQL");

            String token = null;
            if(System.getenv("TOKEN")!=null) {
                token = System.getenv("TOKEN");
            } else {
                Properties prop = readPropertiesFile("application.properties");
                token = prop.getProperty("token");
            }
            JDABuilder builder = JDABuilder.createDefault(token);
            builder.setActivity(Activity.playing("!!rmd commands"));
            builder.addEventListeners(new Commands());

            builder.build();
        } catch (SQLException | IOException e) {
            System.out.println("Error in connecting to PostgreSQL server");
            e.printStackTrace();
            return;
        }

        //Timer que roda a cada 24h:
        Timer timer = new Timer();
        final long milisegundos = 86400000;

        TimerTask task = new TimerTask(){
            @Override
            public void run() {
                try {
                    String data = Today.date();

                    dataDuasSemanas = dateFormat.parse(data);
                    dataDuasSemanas.setTime(dataDuasSemanas.getTime() + 86400000L * 14);
                    dataUmaSemana = dateFormat.parse(data);
                    dataUmaSemana.setTime(dataUmaSemana.getTime() + 86400000 * 10);
                    dataUmDia = dateFormat.parse(data);
                    dataUmDia.setTime(dataUmDia.getTime() + 86400000);

                    String[][] allMessages = Select.selectALLMessages();
                    for(int i=0; i<allMessages.length; i++) {
                        try {
                            Long messageID = Long.parseLong(allMessages[i][0]);
                            Long serverID = Long.parseLong(allMessages[i][1]);
                            Long channelID = Long.parseLong(allMessages[i][2]);
                            String date = allMessages[i][3];
                            String title = allMessages[i][4];
                            String description = allMessages[i][5];
                            String timeLeft;

                            if (date!=null){
                                try {
                                    timeLeft = Time.timeLeft(date);
                                    if (timeLeft.contains("Tempo excedido!")) {
                                        Delete.delete(messageID, serverID, channelID);
                                        System.out.println("Time exceeded event deleted!");
                                    }
                                } catch (ParseException e) {
                                    Delete.delete(messageID, serverID, channelID);
                                    System.out.println("No date event deleted!");
                                }
                            } else if (title == null && description == null) {
                                Delete.delete(messageID, serverID, channelID);
                                System.out.println("Not used event deleted!");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Incorrect date format, number: " + i);
                        }
                    }
                } catch (ParseException | SQLException | IOException e) {
                    e.printStackTrace();
                }
            }
        };
        timer.scheduleAtFixedRate(task, 1000, milisegundos);
    }

}