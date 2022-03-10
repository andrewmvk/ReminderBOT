package rmd.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rmd.embed.EmbedMessage;
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
                    Update.updateDescription(modificationID, description.toString(), serverID, channelID);

                    String lastChangeName = event.getMember().getEffectiveName();
                    String lastChangeAvatarURL = event.getMember().getUser().getAvatarUrl();
                    info = EmbedMessage.modifiedEmbed(info, message, modificationID, lastChangeName, lastChangeAvatarURL);
                } else {
                    info = Exceptions.incorrectModifyCommand("description");
                }
            } catch (SQLException | NumberFormatException | IOException | URISyntaxException | ArrayIndexOutOfBoundsException | NullPointerException | ParseException e) {
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

