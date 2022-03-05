package rmd.events;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rmd.sequelize.Select;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

public class GuildMessageReactionAdd extends ListenerAdapter {
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        Long serverID = Long.parseLong(event.getGuild().getId());
        if(event.getReactionEmote().getName().equals("🔔") && !event.getMember().getUser().equals(event.getJDA().getSelfUser())) {
            //See if this is a bot reacting and if it's the same reaction as the above
            /*Role add with emote reaction*/
            try {
                String roleID =Select.selectRole(serverID);
                Role role = event.getGuild().getRoleById(roleID);
                event.getGuild().addRoleToMember(event.getMember(), role).queue();
            } catch (SQLException | URISyntaxException | IOException e) {
                e.printStackTrace();
            }
        } else if(event.getReactionEmote().getName().equals("🔕") && !event.getMember().getUser().equals(event.getJDA().getSelfUser())) {
            /*Role remove with another emote reaction*/
            try {
                String roleID =Select.selectRole(serverID);
                Role role = event.getGuild().getRoleById(roleID);
                event.getGuild().removeRoleFromMember(event.getMember(), role).queue();
            } catch (SQLException | URISyntaxException | IOException e) {
                e.printStackTrace();
            }
        } else if(event.getReactionEmote().getName().equals("📆") && !event.getMember().getUser().equals(event.getJDA().getSelfUser())) {
            event.getChannel().sendMessage("!!rmd upcoming").queue();
        }
    }
}
