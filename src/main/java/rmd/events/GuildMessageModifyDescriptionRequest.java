package rmd.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rmd.errors.Exceptions;
import rmd.reminding.Reminding;
import rmd.sequelize.Select;
import rmd.sequelize.Update;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.ParseException;

import static rmd.events.GuildMessageCommandsRequest.messageID;

public class GuildMessageModifyDescriptionRequest extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if (args[0].equalsIgnoreCase(Reminding.prefix + "rmd")
                && args.length>=3
                && args[1].equalsIgnoreCase("modify")
                && args[2].equalsIgnoreCase("description")) {

            EmbedBuilder info = new EmbedBuilder();
            StringBuilder description = new StringBuilder();
            for (int i=4; i < args.length; i++) {
                description.append(args[i]).append(" ");
            }
            boolean informedText = true;

            Long serverID = Long.parseLong(event.getGuild().getId());
            Long channelID = Long.parseLong(event.getChannel().getId());
            try {
                if (description.toString().equals("")) {
                    informedText = false;
                }
                Long modificationID = Long.parseLong(args[3]);
                String[] message = Select.select(modificationID, serverID);
                if (message == null) {
                    info = Exceptions.idNotFound(messageID.toString());
                } else if (informedText){
                    String title = message[0];
                    String date = message[1];
                    Update.updateDescription(modificationID, description.toString(), serverID, channelID);

                    info.setTitle("📚  RemindingBot: Evento  ⏰\n"
                            + "------------------------------------");
                    if (message[0] == null) {
                        info.addField("Nome:", "{Nome do evento}", false);
                    } else {
                        info.addField("Nome :", title, false);
                    }
                    if (message[1] == null) {
                        info.addField("Data final :", "{Data do evento - Horário do evento}", false);
                        info.addField("Tempo restante :", "{Tempo restante}", false);
                    } else {
                        info.addField("Data final :", date, false);
                        String daysLeft = rmd.date.Time.timeLeft(message[1]);
                        info.addField("Tempo restante :", daysLeft, false);
                    }
                    info.addField("Descrição :", description.toString(), false);
                    info.addField("ID : " + modificationID, "", false);
                    info.setColor(0x2d3b7a);
                    info.setFooter("Última alteração feita por : " + event.getMember().getEffectiveName(), event.getMember().getUser().getAvatarUrl());
                } else {
                    info = Exceptions.incorrectModifyCommand("description");
                }
            } catch (SQLException | ParseException | NumberFormatException | IOException | URISyntaxException | ArrayIndexOutOfBoundsException | NullPointerException e) {
                if (e.toString().contains("ArrayIndexOutOfBoundsException")) {
                    info = Exceptions.incorrectModifyCommand("description");
                } else if (e.toString().contains("NullPointerException")) {
                    info = Exceptions.idNotFound(args[3]);
                } else {
                    info = Exceptions.sqlConnection();
                }
            }
        event.getChannel().sendMessageEmbeds(info.build()).queue();
        info.clear();
        }
    }
}

