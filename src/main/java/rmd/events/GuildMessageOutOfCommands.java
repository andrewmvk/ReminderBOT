package rmd.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rmd.reminding.Reminding;

public class GuildMessageOutOfCommands extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        String argumento;

        try {
            argumento = args[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            argumento = "ArrayIndexOutOfBoundsException";
        }

        if (args[0].equalsIgnoreCase(Reminding.prefix + "rmd") && argumento.equals("ArrayIndexOutOfBoundsException")) {
            EmbedBuilder info = new EmbedBuilder();
            info.addField("Esse comando não existe!","Use [ !!rmd commands ] para ver todas funções disponíveis!", false);
            info.setColor(0xff0000);

            event.getChannel().sendMessageEmbeds(info.build()).queue();
            info.clear();
        }

        //info.addField("Teste: ", "<#"+channelID+">",false); Exemplo de como marcar um chat...
    }
}
