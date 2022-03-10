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

public class GuildMessageModifyNameRequest extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if (args[0].equalsIgnoreCase(Reminding.prefix + "rmd")
                && args.length==2 && args[1].equalsIgnoreCase("modify")) {

            event.getChannel().sendMessageEmbeds(Exceptions.outOfBounds().build()).queue();
            Exceptions.outOfBounds().clear();

        } else if (args[0].equalsIgnoreCase(Reminding.prefix + "rmd")
                && args.length>=3
                && args[1].equalsIgnoreCase("modify")
                && args[2].equalsIgnoreCase("name")) {

            StringBuilder title = new StringBuilder();
            EmbedBuilder info = new EmbedBuilder();
            boolean informedText = true;

            for (int i=4; i< args.length; i++) {
                title.append(args[i]).append(" ");
            }

            Long serverID = Long.parseLong(event.getGuild().getId());
            Long channelID = Long.parseLong(event.getChannel().getId());

            try {
                if(title.toString().equals("")) {
                    informedText = false;
                }
                Long modificationID = Long.parseLong(args[3]);
                String[] message = Select.select(modificationID, serverID);
                if (message == null) {
                    info = Exceptions.idNotFound(args[3]);
                } else if(informedText) {
                    Update.updateTitle(modificationID, title.toString(), serverID, channelID);

                    String lastChangeName = event.getMember().getEffectiveName();
                    String lastChangeAvatarURL = event.getMember().getUser().getAvatarUrl();

                    message[0] = title.toString();
                    info = EmbedMessage.modifiedEmbed(info, message, modificationID, lastChangeName, lastChangeAvatarURL);
                } else {
                    info = Exceptions.incorrectModifyCommand("name");
                }
            } catch (SQLException | NumberFormatException | ArrayIndexOutOfBoundsException | IOException | URISyntaxException | ParseException e) {
                if (e.toString().contains("ArrayIndexOutOfBoundsException")) {
                    info = Exceptions.incorrectModifyCommand("name");
                } else if(e.toString().contains("NullPointerException")) {
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
