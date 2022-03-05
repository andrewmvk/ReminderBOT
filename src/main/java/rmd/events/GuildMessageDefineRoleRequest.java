package rmd.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rmd.reminding.Reminding;
import rmd.sequelize.Update;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

public class GuildMessageDefineRoleRequest extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if (args[0].equalsIgnoreCase(Reminding.prefix + "rmd")
                && args.length>=2
                && args[1].equalsIgnoreCase("define")) {

            Long serverID = Long.parseLong(event.getGuild().getId());

            String argumento;
            try {
                argumento = args[2];
            } catch (ArrayIndexOutOfBoundsException e) {
                argumento = null;
            }

            String roles = event.getGuild().getRoles().toString();
            roles = roles.replace("[","")
                    .replace("]","")
                    .replace(")","")
                    .replace(",", "")
                    .replace("R:","")
                    .replace("(",":")
                    .replace("@","");

            String[] rolesSplited = roles.split(" ");
            String[][] rolesFormated = new String[rolesSplited.length][];
            String result = null;

            for (int i=0; i<rolesSplited.length; i++) {
                rolesFormated[i] = rolesSplited[i].split(":");
                if (argumento!=null && rolesFormated[i][0].equalsIgnoreCase(args[2])) {
                    if (rolesFormated[i][0].equalsIgnoreCase("everyone")) {
                        result = "everyone";
                    } else {
                        result = rolesFormated[i][1];
                    }
                }
            }

            EmbedBuilder info = new EmbedBuilder();

            if(result!=null) {
                try {
                    if (result.equalsIgnoreCase("everyone")) {
                        info.addField("Sucesso!", "A partir de agora o cargo para notificação está definido em : @everyone", false);
                    } else {
                        info.addField("Sucesso!", "A partir de agora o cargo para notificação está definido em : <@&" + result + ">", false);
                    }
                    Update.updateRole(result, serverID);
                    info.setColor(0x2d3b7a);
                } catch (SQLException | IOException | URISyntaxException e) {
                    e.printStackTrace();
                }
            } else {
                String rolesNames = "";
                for (int i=0; i<rolesFormated.length; i++) {
                    if (i==rolesFormated.length-1) {
                        rolesNames += rolesFormated[i][0] + ".";
                    } else {
                        rolesNames += rolesFormated[i][0] + ", ";
                    }
                }
                info.addField("Escolha um dos cargos e siga as instruções :","Cargos : " + rolesNames + "\n" +
                        "Realize o comando novamente, mas com o nome do cargo escolhido." + "\n" +
                        "Exemplo : !!rmd define " + rolesFormated[1][0], false);
                info.setColor(0x2d3b7a);
            }
            event.getChannel().sendMessageEmbeds(info.build()).queue();

            if(result!=null) {
                event.getChannel().sendMessage("Reaja com 🔔 para habilitar as notificações ou com 🔕 para desativá-las").queue();
            }
        } else if (event.getMember().getUser().equals(event.getJDA().getSelfUser())) {
            String message = event.getMessage().getEmbeds().toString();
            if (!message.contains("MessageEmbed")) {
                event.getMessage().addReaction("🔔").queue();
                event.getMessage().addReaction("🔕").queue();
            }
        }
    }
}
