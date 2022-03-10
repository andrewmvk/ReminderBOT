package rmd.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rmd.reminding.Reminding;

public class GuildMessageCommandsRequest extends ListenerAdapter {
    public static Long messageID;

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if (args[0].equalsIgnoreCase(Reminding.prefix + "rmd")
                && args.length>=2
                && args[1].equalsIgnoreCase("commands")) {

            EmbedBuilder info = new EmbedBuilder();
            info.setTitle("⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀📚 RemindingBot: Comandos ⏰\n" +
                    "--------------------------------------------------------------------------------------");
            info.addField("Criar evento :", "Cria um novo evento com nome alocado [name] (opcional) ou nome pré alocado ({Nome do evento}).\n" +
                    "!!rmd create [name].", false);
            info.addField("Modificar nome :", "Modifica o nome do evento com o ID representando-o, sendo [name] o seu novo nome.\n" +
                    "!!rmd modify name [ID] [name]", false);
            info.addField("Modificar data :", "Modifica a data do evento cujo ID é igual ao demonstrado seguindo a formatação de data descrita abaixo.\n" +
                    "!!rmd modify date [ID] [dd/MM/yyyy HH:mm:ss]", false);
            info.addField("Modificar duração :", "Modifica a duração do evento, dessa forma a data passa a ser o começo do evento.\n" +
                    "!!rmd modify duration [ID] [minutos]", false);
            info.addField("Modificar descrição :", "Modifica a descrição do evento com identificador igual ao [ID]. Sem restrições aqui.\n" +
                    "!!rmd modify description [ID] [texto]", false);
            info.addField("Listar eventos :", "Lista os últimos 25 eventos criados no servidor em ordem de ocorrência.\n" +
                    "!!rmd upcoming", false);
            info.addField("Mostrar dados : ", "Lista apenas um evento em que ID descrito é o que o representa (descrição ampliada).\n" +
                    "!!rmd upcoming [ID]", false);
            info.addField("Remover evento : ", "Remove um evento de escolha a partir do ID. Não há confirmação para deletar!\n" +
                    "!!rmd delete [ID]", false);
            info.addField("Definir cargo :", "Define um cargo ao qual a notificação de evento próximo será dada quando listar eventos.\n" +
                    "!!rmd define [Role]", false);
            info.setColor(0x2d3b7a);
            info.setFooter("Criado por: Andrew Medeiros & Brayan Amaral");

            event.getChannel().sendMessageEmbeds(info.build()).queue();
            info.clear();
        }
    }
}
