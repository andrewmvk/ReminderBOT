package rmd.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rmd.errors.Exceptions;
import rmd.reminding.Reminding;
import rmd.sequelize.Insert;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

import static rmd.events.GuildMessageCommandsRequest.messageID;

public class GuildMessageCreateRequest extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");


        if (args[0].equalsIgnoreCase(Reminding.prefix + "rmd") && args.length >= 2 && args[1].equalsIgnoreCase("create")) {
            EmbedBuilder info = new EmbedBuilder();

            StringBuilder title = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                title.append(args[i]).append(" ");
            }

            info.setTitle("📚  RemindingBot: Evento  ⏰\n"
                    + "-----------------------------------");

            if (title.toString().equalsIgnoreCase("")) {
                info.addField("Nome : ", "{Nome do evento}", false);
            } else {
                info.addField("Nome : ", title.toString(), false);
            }
            info.addField("Data final :", "{Data do evento - Horário do evento}", false);
            info.addField("Tempo restante :", "{Tempo restante}", false);
            info.addField("Descrição :", "{Descrição}", false);
            info.setColor(0x2d3b7a);
            String author = event.getMember().getEffectiveName();
            info.setFooter("Evento criado por : " + author, event.getMember().getUser().getAvatarUrl());

            Long serverID = Long.parseLong(event.getGuild().getId());
            Long channelID = Long.parseLong(event.getChannel().getId());

            try {
                if (title.toString().equalsIgnoreCase("")) {
                    messageID = Insert.create(serverID, channelID, null, null, null, author);
                } else {
                    messageID = Insert.create(serverID, channelID, title.toString(), null, null, author);
                }
            } catch (SQLException | IOException | URISyntaxException e) {
                event.getChannel().sendMessageEmbeds(Exceptions.sqlConnection().build()).queue();
                Exceptions.sqlConnection().clear();
                e.printStackTrace();
            }

            info.addField("ID : " + messageID.toString(), "", false);

            event.getChannel().sendMessageEmbeds(info.build()).queue();
            info.clear();

        }
    }
}
