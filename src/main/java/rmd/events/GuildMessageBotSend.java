package rmd.events;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildMessageBotSend extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getMember().getUser().equals(event.getJDA().getSelfUser())) {
            String message = event.getMessage().toString();
            String messageType = event.getMessage().getEmbeds().toString();
            if (!messageType.contains("MessageEmbed") && !message.contains("!!rmd") && message.contains("@")) {
                //message.contains("!!rmd") is just a anti-loop
                event.getMessage().addReaction("🔔").queue();
                event.getMessage().addReaction("🔕").queue();
                event.getMessage().addReaction("📆").queue();
            } else if (!messageType.contains("MessageEmbed") && !message.contains("!!rmd")) {
                event.getMessage().addReaction("🔔").queue();
                event.getMessage().addReaction("🔕").queue();
            }
        }
    }
}
