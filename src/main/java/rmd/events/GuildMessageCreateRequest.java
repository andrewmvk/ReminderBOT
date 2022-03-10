package rmd.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rmd.embed.EmbedMessage;
import rmd.errors.Exceptions;
import rmd.reminding.Reminding;
import rmd.sequelize.Insert;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.ParseException;

import static rmd.events.GuildMessageCommandsRequest.messageID;
//Can use this to a dynamic modification request without an ID

public class GuildMessageCreateRequest extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if (args[0].equalsIgnoreCase(Reminding.prefix + "rmd") && args.length >= 2 && args[1].equalsIgnoreCase("create")) {
            EmbedBuilder info = new EmbedBuilder();

            StringBuilder title = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                title.append(args[i]).append(" ");
            }

            Long serverID = Long.parseLong(event.getGuild().getId());
            Long channelID = Long.parseLong(event.getChannel().getId());

            String lastChangeName = event.getMember().getEffectiveName();
            String lastChangeAvatarURL = event.getMember().getUser().getAvatarUrl();

            try {
                if (title.toString().equalsIgnoreCase("")) {
                    messageID = Insert.create(serverID, channelID, null, null, null, lastChangeName);
                } else {
                    messageID = Insert.create(serverID, channelID, title.toString(), null, null, lastChangeName);
                }
            } catch (SQLException | IOException | URISyntaxException e) {
                event.getChannel().sendMessageEmbeds(Exceptions.sqlConnection().build()).queue();
                Exceptions.sqlConnection().clear();
                e.printStackTrace();
            }

            String[] message = new String[5];
            if (!title.toString().equalsIgnoreCase("")) {
                message[0] = title.toString();
            }

            try {
                info = EmbedMessage.modifiedEmbed(info, message, messageID, lastChangeName, lastChangeAvatarURL);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            event.getChannel().sendMessageEmbeds(info.build()).queue();
            info.clear();

        }
    }
}
