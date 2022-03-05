package rmd.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rmd.errors.Exceptions;
import rmd.reminding.Reminding;
import rmd.sequelize.Delete;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

public class GuildMessageDeleteRequest extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if (args[0].equalsIgnoreCase(Reminding.prefix + "rmd")
                && args.length>=2
                && args[1].equalsIgnoreCase("delete")) {
            EmbedBuilder info = new EmbedBuilder();

            Long serverID = Long.parseLong(event.getGuild().getId());
            Long channelID = Long.parseLong(event.getChannel().getId());

            String argumento;
            try {
                argumento = args[2];
            } catch (ArrayIndexOutOfBoundsException e) {
                argumento = "ArrayIndexOutOfBoundsException";
                info = Exceptions.idNotInformed();
                event.getChannel().sendMessageEmbeds(info.build()).queue();
                info.clear();
            }
            if(!argumento.contains("ArrayIndexOutOfBoundsException")) {
                try {
                    Long messageID = Long.parseLong(args[2]);

                    String[] deletedMessage = Delete.delete(messageID, serverID, channelID);
                    if(deletedMessage==null) {
                        info = Exceptions.idNotFound(messageID.toString());
                    } else {
                        info.setTitle("📚  RemindingBot: Evento deletado  ⏰\n"
                                + "----------------------------------------------");
                        if (deletedMessage[0]==null) {
                            info.addField("Nome :", "{Nome do evento", false);
                        } else {
                            info.addField("Nome :", deletedMessage[0], false);
                        }
                        if (deletedMessage[1]==null) {
                            info.addField("Data final :", "{Data do evento - Horário do evento}", false);
                        } else {
                            info.addField("Data final :", deletedMessage[1].toString(), false);
                        }
                        if (deletedMessage[2]==null) {
                            info.addField("Descrição :", "{Descrição}", false);
                        } else {
                            info.addField("Descrição :", deletedMessage[2].toString(), false);
                        }
                        info.setFooter("Última atualização feita por : " + deletedMessage[3]);
                        info.setColor(0x2d3b7a);
                    }
                    event.getChannel().sendMessageEmbeds(info.build()).queue();
                    info.clear();
                } catch (SQLException | NumberFormatException | IOException | URISyntaxException e) {
                    if (e.toString().contains("SQLException")) {
                        info = Exceptions.sqlConnection();
                    } else {
                        info = Exceptions.idNotFound(args[2]);
                    }
                    event.getChannel().sendMessageEmbeds(info.build()).queue();
                    info.clear();
                }
            }
        }
    }
}
