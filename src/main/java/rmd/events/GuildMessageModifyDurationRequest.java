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

public class GuildMessageModifyDurationRequest extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if (args[0].equalsIgnoreCase(Reminding.prefix + "rmd")
                && args.length>=3
                && args[1].equalsIgnoreCase("modify")
                && args[2].equalsIgnoreCase("duration")) {

            EmbedBuilder info = new EmbedBuilder();
            long messageId;
            String argumento = null;
            int duration = -1;
            try {
                argumento = args[3];
                messageId = Long.parseLong(args[3]);
                duration = Integer.parseInt(args[4]);
                Long serverID = Long.parseLong(event.getGuild().getId());

                String[] message = Select.select(messageId, serverID);
                Update.updateDuration(duration, serverID, messageId);

                message[4] = args[4];
                String lastChangeName = event.getMember().getEffectiveName();
                String lastChangeAvatarURL = event.getMember().getUser().getAvatarUrl();
                info = EmbedMessage.modifiedEmbed(info, message, messageId, lastChangeName, lastChangeAvatarURL);

            } catch (ArrayIndexOutOfBoundsException | SQLException | NumberFormatException e) {
                if(argumento == null) {
                    info = Exceptions.idNotInformed("modify duration");
                } else if (duration == -1 && !e.toString().contains("NumberFormatException")) {
                    info.addField("ERROR :","Digite a duração do evento em horas!", false);
                    info.setColor(0xff0000);
                } else {
                    info = Exceptions.idNotFound(argumento);
                }
            } catch (IOException | URISyntaxException | ParseException throwable) {
                throwable.printStackTrace();
            }
            event.getChannel().sendMessageEmbeds(info.build()).queue();
            info.clear();
        }
    }
}
