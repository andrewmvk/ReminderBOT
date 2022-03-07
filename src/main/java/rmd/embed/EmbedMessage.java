package rmd.embed;

import net.dv8tion.jda.api.EmbedBuilder;

import java.text.ParseException;

public class EmbedMessage {
    public static EmbedBuilder upcomingEmbed(EmbedBuilder info, String[][] messages, int messagesLength) throws ParseException {
        for (int i = 0; i < messagesLength; i++) {
            int numero = i + 1;
            info.setTitle("📚 RemindingBot: Evento por vir ⏰\n"
                    + "-------------------------------------------");
            if (messages[i][1]!=null) {
                messages[i][1] = rmd.date.Time.timeLeft(messages[i][1]);
            }

            for (int j=0; j<2; j++) {
                if(messages[i][j]==null) {
                    messages[i][j]="Não informado!";
                }
            }
            info.addField("Evento " + numero + ": " + messages[i][0],
                    "Tempo restante :\n" + messages[i][1] +
                            "\nID: " + messages[i][2], false);
        }

        info.setFooter("Criado por : Andrew Medeiros & Brayan Amaral");
        return  info;
    }
}
