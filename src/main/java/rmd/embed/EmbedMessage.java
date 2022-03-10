package rmd.embed;

import net.dv8tion.jda.api.EmbedBuilder;
import rmd.date.Time;

import java.text.ParseException;

public class EmbedMessage {
    public static EmbedBuilder upcomingEmbed(EmbedBuilder info, String[][] messages, int messagesLength) throws ParseException {
        int numero = 0;
        String[][] date = new String[messagesLength][1];
        info.setTitle("📚 RemindingBot: Evento por vir ⏰\n"
                + "-------------------------------------------");
        for (int i = 0; i < messagesLength; i++) {
            if (messages[i][1]!=null) {
                String[] aux;
                int duration = Integer.parseInt(messages[i][5]);
                if(messages[i][1]!=null) {
                    date[i][0] = messages[i][1];
                } else {
                    date[i][0] = "Não informado";
                }
                aux = rmd.date.Time.timeLeft(messages[i][1],duration);
                messages[i][1] = aux[0];
                messages[i][5] = aux[1];
            }

            for (int j=0; j<2; j++) {
                if(messages[i][j]==null) {
                    messages[i][j]="Não informado!";
                }
            }

            if (!messages[i][1].contains("Tempo")) {
                numero++;
                if(date[i][0]!=null && messages[i][5]!=null) {
                    info.addField("Evento " + numero + ": " + messages[i][0],
                            "Data de início :\n" + date[i][0] +
                            "\nTempo restante :\n" + messages[i][5] +
                                    "\nID: " + messages[i][2], false);
                } else if(!messages[i][1].contains("Evento")) {
                    if(date[i][0] == null) {
                        date[i][0] = "Não informado!";
                    }
                    info.addField("Evento " + numero + ": " + messages[i][0],
                            "Tempo até o evento :\n" + messages[i][1] +
                                    "\nData: " + date[i][0] +
                                    "\nID: " + messages[i][2], false);
                }
            }
        }

        info.setFooter("Criado por : Andrew Medeiros & Brayan Amaral");
        return  info;
    }
    public static EmbedBuilder modifiedEmbed (EmbedBuilder info, String[] message, long modificationID,
                                              String lastChangeUserName, String lastChangeUserURL) throws ParseException {
        String title = message[0];
        String date = message[1];
        String description = message[2];
        String duration = message[4];

        info.setTitle("📚  RemindingBot: Evento  ⏰\n"
                + "------------------------------------");
        if (title == null) {
            info.addField("Nome:", "{Nome do evento}", false);
        } else {
            info.addField("Nome :", title, false);
        }
        if (date == null) {
            info.addField("Data :", "{Data do evento}", false);
            if(duration==null || duration.equals("0")) {
                info.addField("Tempo restante :", "{Tempo restante}", false);
            }
        } else {
            info.addField("Data :", date, false);
            if(duration==null || duration.equals("0")) {
                String timeLeft = Time.timeLeft(date, 0)[0];
                if(!timeLeft.contains("-")) {
                    info.addField("Tempo até o evento :", timeLeft, false);
                } else {
                    info.addField("Tempo até o evento :", "O evento está acontecendo!", false);
                }
            }
        }
        if (duration!=null && !duration.equals("0")) {
            String remaining = Time.timeLeft(date, Integer.parseInt(duration))[1];
            String timeLeft = Time.timeLeft(date, Integer.parseInt(duration))[0];
            if(remaining!=null) {
                info.addField("Tempo restante :", remaining + " horas", false);
            } else {
                info.addField("Tempo até o evento :", timeLeft, false);
            }
            info.addField("Duração :",duration + " minutos", false);
        }
        if (description == null) {
            info.addField("Descrição : ", "{Descrição}", false);
        } else {
            info.addField("Descrição : ", description, false);
        }
        info.addField("ID : " + modificationID, "", false);
        info.setColor(0x2d3b7a);
        info.setFooter("Última alteração : " + lastChangeUserName, lastChangeUserURL);

        return info;
    }
}
