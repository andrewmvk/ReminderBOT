package rmd.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rmd.date.Format;
import rmd.errors.Exceptions;
import rmd.reminding.Reminding;
import rmd.sequelize.Select;
import rmd.sequelize.Update;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;

import static rmd.events.GuildMessageCommandsRequest.messageID;

public class GuildMessageModifyDateRequest extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if (args[0].equalsIgnoreCase(Reminding.prefix + "rmd")
                && args.length>=3
                && args[1].equalsIgnoreCase("modify")
                && args[2].equalsIgnoreCase("date")) {

            EmbedBuilder info = new EmbedBuilder();
            Long serverID = Long.parseLong(event.getGuild().getId());
            Long channelID = Long.parseLong(event.getChannel().getId());

            try {
                //Verificação do formato da data:
                boolean isValuesCorrect = true;
                boolean isLeapYear = false;
                boolean isThirtyOneMonth = false;

                String[] dateVerification = args[4].split("/");
                int dia = Integer.parseInt(dateVerification[0]);
                int mes = Integer.parseInt(dateVerification[1]);
                int ano = Integer.parseInt(dateVerification[2]);
                if (ano%400==0) {
                    isLeapYear=true;
                } else if (ano%4==0 && ano%100!=0) {
                    isLeapYear=true;
                }
                if ((mes%2!=0 || mes==8 || mes==10 || mes==12) && mes!=9 && mes!=11) {
                    isThirtyOneMonth=true;
                }
                if (!isThirtyOneMonth && dia>=31) {
                    isValuesCorrect = false;
                } else if ((dia>31 || dia<1) || (mes<1 || mes>12) || ano<0) {
                    isValuesCorrect = false;
                } else if (dia>28 && !isLeapYear && mes==2) {
                    isValuesCorrect = false;
                } else if (dia>29 && isLeapYear && mes==2) {
                    isValuesCorrect = false;
                }

                //Hour verification
                String[] hourSplitter = args[5].split(":");
                int hour = Integer.parseInt(hourSplitter[0]);
                int minute = Integer.parseInt(hourSplitter[1]);
                int seconds = Integer.parseInt(hourSplitter[2]);

                if ((hour>23 || hour<0) || (minute>59 || minute<0) || (seconds>59 || seconds<0)) {
                    isValuesCorrect = false;
                }

                Long modificationID = Long.parseLong(args[3]);
                String[] message = Select.select(modificationID, serverID);
                if (isValuesCorrect) {
                    String concatenatedDate = dia + "/" + mes + "/" + ano + " " + hour + ":" + minute + ":" + seconds;
                    String finalDate = Format.correction(concatenatedDate, false);
                    if (message == null) {
                        info = Exceptions.idNotFound(messageID.toString());
                    } else {
                        String title = message[0];
                        Update.updateDate(modificationID, finalDate, serverID, channelID);

                        String daysLeft = rmd.date.Time.timeLeft(finalDate);
                        info.setTitle("📚  RemindingBot: Evento  ⏰\n"
                                + "-----------------------------------");
                        if (message[0] == null) {
                            info.addField("Nome :", "{Nome do evento}", false);
                        } else {
                            info.addField("Nome :", title, false);
                        }
                        info.addField("Data final :", finalDate, false);
                        info.addField("Tempo restante :", daysLeft, false);
                        if (message[2] == null) {
                            info.addField("Descrição :", "{Descrição}", false);
                        } else {
                            info.addField("Descrição :", message[2], false);
                        }
                        info.addField("ID : " +  modificationID,"", false);
                        info.setColor(0x2d3b7a);
                        info.setFooter("Última alteração feita por: " + event.getMember().getEffectiveName(), event.getMember().getUser().getAvatarUrl());
                    }

                } else {
                    info = Exceptions.incorrectModifyCommand("date");
                }
            } catch (SQLException | ParseException | ArrayIndexOutOfBoundsException | NumberFormatException | IOException | URISyntaxException e) {
                String error = Arrays.toString(e.getStackTrace());
                if(error.contains("sql")) {
                    info = Exceptions.sqlConnection();
                } else if (e.toString().contains("Index 4 out of bounds for length 3")){
                    info = Exceptions.outOfBounds();
                } else if (e.toString().contains("Index 4 out of bounds for length 4")) {
                    info = Exceptions.idNotFound(args[3]);
                } else {
                    info = Exceptions.incorrectModifyCommand("date");
                }
            }
            event.getChannel().sendMessageEmbeds(info.build()).queue();
            info.clear();
        }
    }
}
