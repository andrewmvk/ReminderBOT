package rmd.reminding;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import rmd.date.Time;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import rmd.date.Today;
import rmd.embed.EmbedMessage;
import rmd.events.*;
import rmd.sequelize.Delete;
import rmd.sequelize.Select;
import rmd.sequelize.Start;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static rmd.sequelize.Start.readPropertiesFile;

public class Reminding {

    public static String prefix = "!!";
    public static String delete = "DELETE FROM serversmessages WHERE messages_id=";
    public static String selectMessage = "SELECT * FROM serversmessages WHERE messages_id=";
    public static String selectMessages = "SELECT messages_id,title,date,role,channel_id FROM serversmessages WHERE server_id=";
    public static String select = "SELECT * FROM serversmessages";
    public static String selectId = "SELECT MAX(messages_id) FROM serversmessages";
    public static String sql = "INSERT INTO serversmessages (server_id, channel_id, title, description, date, author) VALUES (?, ?, ?, ?, ?, ?)";
    public static Date dataDuasSemanas, dataUmaSemana, dataUmDia;
    public static JDA jda;

    public static void main(String[] args) throws LoginException, ParseException {
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
            builder.addEventListeners(new GuildMessageReactionAdd());
            builder.addEventListeners(new GuildMessageCommandsRequest());
            builder.addEventListeners(new GuildMessageCreateRequest());
            builder.addEventListeners(new GuildMessageModifyNameRequest());
            builder.addEventListeners(new GuildMessageModifyDateRequest());
            builder.addEventListeners(new GuildMessageModifyDescriptionRequest());
            builder.addEventListeners(new GuildMessageUpcoming());
            builder.addEventListeners(new GuildMessageDeleteRequest());
            builder.addEventListeners(new GuildMessageDefineRoleRequest());
            builder.addEventListeners(new GuildMessageOutOfCommands());
            builder.addEventListeners(new GuildMessageBotSend());

            jda = builder.build();
            jda.awaitReady();

        } catch (SQLException | IOException | URISyntaxException | InterruptedException e) {
            System.out.println("Error in connecting to PostgreSQL server or in JDA instance");
            e.printStackTrace();
            return;
        }

        //Timer que roda a cada 24h:
        Timer timer = new Timer();
        final long milisegundos = 86400000;
        long timeUntilSixAm = Time.timeUntilSixAm();

        TimerTask task = new TimerTask(){
            @Override
            public void run() {
                try {
                    List<Guild> guilds = jda.getGuilds();
                    String data = Today.date();

                    String[][] messages;

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
                    
                    for(Guild guild : guilds) {
                        long serverID = Long.parseLong(guild.getId());

                        messages = Select.selectMessages(serverID);
                        TextChannel textChannel = guild.getTextChannelById(Long.parseLong(messages[0][4]));

                        int messagesLength = messages.length;
                        String role = null;
                        for (int i = 0; i < messagesLength; i++) {
                            if (!messages[i][3].contains("everyone")) {
                                role = "<@&" + messages[i][3] + ">";
                                break;
                            }
                        }
                        EmbedBuilder info = new EmbedBuilder();
                        info = EmbedMessage.upcomingEmbed(info, messages, messagesLength);

                        sendMessage(textChannel, info, role);
                    }
                } catch (ParseException | SQLException | IOException | URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        };
        timer.scheduleAtFixedRate(task, timeUntilSixAm, milisegundos);
    }

    static void sendMessage(TextChannel ch, EmbedBuilder message, String role) {
        ch.sendMessage(role+", Clique em 📆 para atualizar.").queue();
        ch.sendMessageEmbeds(message.build()).queue();
    }

}