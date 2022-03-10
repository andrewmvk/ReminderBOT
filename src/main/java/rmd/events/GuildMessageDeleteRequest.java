package rmd.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rmd.embed.EmbedMessage;
import rmd.errors.Exceptions;
import rmd.reminding.Reminding;
import rmd.sequelize.Delete;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.ParseException;

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
                info = Exceptions.idNotInformed("delete");
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
                        String lastChangeName = event.getMember().getEffectiveName();
                        String lastChangeAvatarURL = event.getMember().getUser().getAvatarUrl();

                        info = EmbedMessage.modifiedEmbed(info, deletedMessage, messageID, lastChangeName, lastChangeAvatarURL);
                    }
                    event.getChannel().sendMessageEmbeds(info.build()).queue();
                    info.clear();
                } catch (SQLException | NumberFormatException | IOException | URISyntaxException | ParseException e) {
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
