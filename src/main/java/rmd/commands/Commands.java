package rmd.commands;

import rmd.date.Format;
import rmd.date.Time;
import rmd.reminding.Reminding;
import rmd.sequelize.Delete;
import rmd.sequelize.Insert;
import rmd.sequelize.Select;
import rmd.sequelize.Update;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rmd.errors.Exceptions;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.*;
import java.text.ParseException;
import java.util.Arrays;

public class Commands extends ListenerAdapter {
    public static Long messageID;

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if (args[0].equalsIgnoreCase(Reminding.prefix + "rmd") && args.length>=2 && args[1].equalsIgnoreCase("create")) {
            EmbedBuilder info = new EmbedBuilder();

            StringBuilder title = new StringBuilder();
            for (int i=2; i < args.length; i++) {
                title.append(args[i]).append(" ");
            }

            info.setTitle("📚  RemindingBot: Evento  ⏰\n"
                     + "-----------------------------------");

            if(title.toString().equalsIgnoreCase("")) {
                info.addField("Nome : ", "{Nome do evento}", false);
            } else {
                info.addField("Nome : ", title.toString(), false);
            }
            info.addField("Data final :", "{Data do evento - Horário do evento}", false);
            info.addField("Tempo restante :", "{Tempo restante}", false);
            info.addField("Descrição :", "{Descrição}", false);
            info.setColor(0x2d3b7a);
            String author = event.getMember().getEffectiveName();
            info.setFooter("Evento criado por : " + author, event.getMember().getUser().getAvatarUrl());

            Long serverID = Long.parseLong(event.getGuild().getId());
            Long channelID = Long.parseLong(event.getChannel().getId());

            try {
                if(title.toString().equalsIgnoreCase("")){
                    messageID = Insert.create(serverID, channelID, null,null,null, author);
                } else {
                    messageID = Insert.create(serverID, channelID, title.toString(),null,null, author);
                }
            } catch (SQLException | IOException | URISyntaxException e) {
                event.getChannel().sendMessageEmbeds(Exceptions.sqlConnection().build()).queue();
                Exceptions.sqlConnection().clear();
                e.printStackTrace();
            }

            info.addField("ID : " + messageID.toString(),"", false);

            event.getChannel().sendMessageEmbeds(info.build()).queue();
            info.clear();

        } else if (args[0].equalsIgnoreCase(Reminding.prefix + "rmd")
                && args.length==2 && args[1].equalsIgnoreCase("modify")) {

            event.getChannel().sendMessageEmbeds(Exceptions.outOfBounds().build()).queue();
            Exceptions.outOfBounds().clear();

        } else if (args[0].equalsIgnoreCase(Reminding.prefix + "rmd")
                && args.length>=3
                && args[1].equalsIgnoreCase("modify")
                && args[2].equalsIgnoreCase("name")) {

            StringBuilder title = new StringBuilder();
            for (int i=4; i< args.length; i++) {
                title.append(args[i]).append(" ");
            }

            Long serverID = Long.parseLong(event.getGuild().getId());
            Long channelID = Long.parseLong(event.getChannel().getId());

            try {
                Long modificationID = Long.parseLong(args[3]);
                String[] message = Select.select(modificationID, serverID);
                EmbedBuilder info = new EmbedBuilder();
                if(message==null) {
                    info.addField("ERRO: ", "Não existe uma mensagem com esse ID("+modificationID+") nesse servidor!", false );
                    info.setColor(0xff0000);
                } else {
                    Update.updateTitle(modificationID, title.toString(), serverID, channelID);

                    info.setTitle("📚  RemindingBot: Evento  ⏰\n"
                            + "-----------------------------------");
                    info.addField("Nome :", title.toString(), false);
                    if (message[1]==null) {
                        info.addField("Data final :", "{Data do evento - Horário do evento}", false);
                        info.addField("Tempo restante :", "{Tempo restante]", false);
                    } else {
                        info.addField("Data final :", message[1], false);
                        String daysLeft = rmd.date.Time.timeLeft(message[1].toString());
                        info.addField("Tempo restante : ", daysLeft, false);
                    }
                    if (message[2]==null) {
                        info.addField("Descrição : ", "{Descrição}", false);
                    } else {
                        info.addField("Descrição : " ,message[2], false);
                    }
                    info.addField("ID : " + modificationID,"", false);
                    info.setColor(0x2d3b7a);
                    info.setFooter("Última alteração feita por : " + event.getMember().getEffectiveName(), event.getMember().getUser().getAvatarUrl());
                }
                event.getChannel().sendMessageEmbeds(info.build()).queue();
                info.clear();
            } catch (SQLException | NumberFormatException | ParseException | ArrayIndexOutOfBoundsException | IOException | URISyntaxException e) {
                String erro = Arrays.toString(e.getStackTrace());
                if(erro.contains("SQLException")) {
                    event.getChannel().sendMessageEmbeds(Exceptions.sqlConnection().build()).queue();
                    Exceptions.sqlConnection().clear();
                } else if (erro.contains("DateFormat.java:399")){
                    event.getChannel().sendMessageEmbeds(Exceptions.parseDate().build()).queue();
                    Exceptions.parseDate().clear();
                } else {
                    event.getChannel().sendMessageEmbeds(Exceptions.parseID().build()).queue();
                    Exceptions.parseID().clear();
                }
            }

        } else if (args[0].equalsIgnoreCase(Reminding.prefix + "rmd")
                && args.length>=3
                && args[1].equalsIgnoreCase("modify")
                && args[2].equalsIgnoreCase("date")) {

            Long serverID = Long.parseLong(event.getGuild().getId());
            Long channelID = Long.parseLong(event.getChannel().getId());

            try {
                //Verificação do formato da data:
                boolean isValuesCorrect = true;
                boolean isLeapYear = false;
                boolean isThirtyOneMonth = false;

                String[] dateVerification = args[4].split("/");
                int dia = Integer.parseInt(dateVerification[0]);
                int mes = Integer.parseInt(dateVerification[1]);
                int ano = Integer.parseInt(dateVerification[2]);
                if (ano%400==0) {
                    isLeapYear=true;
                } else if (ano%4==0 && ano%100!=0) {
                    isLeapYear=true;
                }
                if ((mes%2!=0 || mes==8 || mes==10 || mes==12) && mes!=9 && mes!=11) {
                    isThirtyOneMonth=true;
                }
                if (!isThirtyOneMonth && dia>=31) {
                    isValuesCorrect = false;
                } else if ((dia>31 || dia<1) || (mes<1 || mes>12) || ano<0) {
                    isValuesCorrect = false;
                } else if (dia>28 && !isLeapYear && mes==2) {
                    isValuesCorrect = false;
                } else if (dia>29 && isLeapYear && mes==2) {
                    isValuesCorrect = false;
                }

                //Verificação da hora:
                String[] hourVerification = args[5].split(":");
                int hour = Integer.parseInt(hourVerification[0]);
                int minute = Integer.parseInt(hourVerification[1]);
                int seconds = Integer.parseInt(hourVerification[2]);

                if ((hour>23 || hour<0) || (minute>59 || minute<0) || (seconds>59 || seconds<0)) {
                    isValuesCorrect = false;
                }

                Long modificationID = Long.parseLong(args[3]);
                String[] message = Select.select(modificationID, serverID);
                EmbedBuilder info = new EmbedBuilder();
                if (isValuesCorrect) {
                    String concatenatedDate = dia + "/" + mes + "/" + ano + " " + hour + ":" + minute + ":" + seconds;
                    String finalDate = Format.correction(concatenatedDate, false);
                    if (message == null) {
                        info.addField("ERRO : ", "Não existe uma mensagem com esse ID(" + modificationID + ") nesse servidor!", false);
                        info.addField("Comando : ", "!!rmd modify date [ID] [dd/MM/yyyy HH:mm:ss]", false);
                        info.addField("Ou tente :", "!!rmd commands para visualizar todos os comandos", false);
                        info.setColor(0xff0000);
                    } else {
                        String title = message[0];
                        Update.updateDate(modificationID, finalDate, serverID, channelID);

                        String daysLeft = rmd.date.Time.timeLeft(finalDate);
                        info.setTitle("📚  RemindingBot: Evento  ⏰\n"
                                + "-----------------------------------");
                        if (message[0] == null) {
                            info.addField("Nome :", "{Nome do evento}", false);
                        } else {
                            info.addField("Nome :", title, false);
                        }
                        info.addField("Data final :", finalDate, false);
                        info.addField("Tempo restante :", daysLeft, false);
                        if (message[2] == null) {
                            info.addField("Descrição :", "{Descrição}", false);
                        } else {
                            info.addField("Descrição :", message[2], false);
                        }
                        info.addField("ID : " +  modificationID,"", false);
                        info.setColor(0x2d3b7a);
                        info.setFooter("Última alteração feita por: " + event.getMember().getEffectiveName(), event.getMember().getUser().getAvatarUrl());
                    }
                    event.getChannel().sendMessageEmbeds(info.build()).queue();
                    info.clear();
                } else {
                    event.getChannel().sendMessageEmbeds(Exceptions.parseDate().build()).queue();
                    Exceptions.parseDate().clear();
                }
            } catch (SQLException | ParseException | ArrayIndexOutOfBoundsException | NumberFormatException | IOException | URISyntaxException e) {
                e.printStackTrace();
                String error = Arrays.toString(e.getStackTrace());
                if(error.contains("sql")) {
                    event.getChannel().sendMessageEmbeds(Exceptions.sqlConnection().build()).queue();
                    Exceptions.sqlConnection().clear();
                } else {
                    event.getChannel().sendMessageEmbeds(Exceptions.parseDate().build()).queue();
                    Exceptions.parseDate().clear();
                }
            }


        } else if (args[0].equalsIgnoreCase(Reminding.prefix + "rmd")
                && args.length>=3
                && args[1].equalsIgnoreCase("modify")
                && args[2].equalsIgnoreCase("description")) {

            StringBuilder description = new StringBuilder();
            for (int i=4; i < args.length; i++) {
                description.append(args[i]).append(" ");
            }

            Long serverID = Long.parseLong(event.getGuild().getId());
            Long channelID = Long.parseLong(event.getChannel().getId());

            try {
                Long modificationID = Long.parseLong(args[3]);
                String[] message = Select.select(modificationID, serverID);
                EmbedBuilder info = new EmbedBuilder();
                if(message==null) {
                    info.addField("ERRO : ", "Não existe uma mensagem com esse ID("+modificationID+") nesse servidor!", false );
                    info.addField("Comando : ", "!!rmd modify description [ID] [Nova descrição]", false);
                    info.addField("Ou tente :", "!!rmd commands para visualizar todos os comandos", false);
                    info.setColor(0xff0000);
                } else {
                    String title = message[0];
                    String date = message[1];
                    Update.updateDescription(modificationID, description.toString(), serverID, channelID);

                    info.setTitle("📚  RemindingBot: Evento  ⏰\n"
                            + "------------------------------------");
                    if (message[0]==null) {
                        info.addField("Nome:", "{Nome do evento}", false);
                    } else  {
                        info.addField("Nome :", title, false);
                    }
                    if (message[1]==null) {
                        info.addField("Data final :", "{Data do evento - Horário do evento}", false);
                        info.addField("Tempo restante :", "{Tempo restante}", false);
                    } else {
                        info.addField("Data final :", date, false);
                        String daysLeft = rmd.date.Time.timeLeft(message[1].toString());
                        info.addField("Tempo restante :", daysLeft, false);
                    }
                    info.addField("Descrição :", description.toString(), false);
                    info.addField("ID : " + modificationID,"", false);
                    info.setColor(0x2d3b7a);
                    info.setFooter("Última alteração feita por : " + event.getMember().getEffectiveName(), event.getMember().getUser().getAvatarUrl());
                }
                event.getChannel().sendMessageEmbeds(info.build()).queue();
                info.clear();
            } catch (SQLException | ParseException | NumberFormatException | IOException | URISyntaxException e) {
                String erro = Arrays.toString(e.getStackTrace());
                if(erro.contains("NumberFormatException")){
                    event.getChannel().sendMessageEmbeds(Exceptions.parseID().build()).queue();
                    Exceptions.parseID().clear();
                } else if (erro.contains("DateFormat.java:399")){
                    event.getChannel().sendMessageEmbeds(Exceptions.parseDate().build()).queue();
                    Exceptions.parseDate().clear();
                } else {
                    event.getChannel().sendMessageEmbeds(Exceptions.sqlConnection().build()).queue();
                    Exceptions.sqlConnection().clear();
                }
            }

        } else if (args[0].equalsIgnoreCase(Reminding.prefix + "rmd")
                && args.length>=2
                && args[1].equalsIgnoreCase("upcoming")) {
            EmbedBuilder info = new EmbedBuilder();

            String argumento=null;

            boolean noError = false;
            String role = null;
            int messagesLength = 0;

            Long serverID = Long.parseLong(event.getGuild().getId());
            //Long channelID = Long.parseLong(event.getChannel().getId());
            //Pode ser usado posteriormente para filtrar melhor as mensagens

            try {
                try {
                    argumento = args[2];
                } catch (ArrayIndexOutOfBoundsException e) {
                    argumento = "ArrayIndexOutOfBoundsException";
                }
                if(argumento.contains("ArrayIndexOutOfBoundsException")) {
                    String[][] messages = null;
                    try {
                        messages = Select.selectMessages(serverID);
                        messagesLength = messages.length;
                    } catch (NullPointerException e) {
                        argumento = "NullPointerException";
                        info.addField("ERROR:", "Não existe nenhum evento cadastrado nesse servidor!", false);
                        info.setColor(0xff0000);
                    } catch (IOException | URISyntaxException e) {
                        e.printStackTrace();
                    }
                    if (!argumento.contains("NullPointerException")) {
                        for(int i=0; i < messagesLength; i++) {
                            if(!messages[i][3].contains("everyone")) {
                                role = "<@&" + messages[i][3] + ">";
                                break;
                            }
                        }

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
                        noError = true;
                    }
                } else {
                    String[] message = Select.select(Long.parseLong(args[2]) ,serverID);
                    if(message[3]==null) {
                        info.addField("ERROR :", "Não existe nenhum evento com esse ID("+args[2]+") nesse servidor!", false);
                        info.setColor(0xff0000);
                    } else {
                        try {
                            String daysLeft = "Data final não informada!";
                            String title, date, description;
                            if (message[1]!=null) {
                                daysLeft = Time.timeLeft(message[1]);
                            }
                            for (int j=0; j<4; j++) {
                                if(message[j]==null) {
                                    message[j]="Não informado!";
                                }
                            }
                            title = message[0];
                            date = message[1];
                            description = message[2];

                            info.setTitle("📚  RemindingBot: Evento por vir  ⏰\n"
                                    + "-------------------------------------------");
                            info.addField("Evento :", title, false);
                            info.addField("Data final :", date, false);
                            info.addField("Descrição :", description, false);
                            info.addField("Tempo restante :", daysLeft, false);
                            info.addField("ID : " + args[2],"", false);
                            if (title.contains("Não informado!") && date.contains("Não informado!") && description.contains("Não informado!")) {
                                info.addField("Aviso :", "Este evento pode ser deletado em menos \nde um dia por falta de informações!", false);
                            }
                            info.setFooter("Criado por : " + message[3].toString());
                            info.setColor(0x2d3b7a);

                        } catch (NullPointerException | ParseException e) {
                            String error = Arrays.toString(e.getStackTrace());
                            if(error.contains("DateFormat.java:")||error.contains("SimpleDateFormat")) {
                                event.getChannel().sendMessageEmbeds(Exceptions.parseDate().build()).queue();
                                Exceptions.parseDate().clear();
                            } else {
                                info.addField("ERROR :", "Mensagem não existe ou é nula!", false);
                                info.setColor(0xff0000);
                            }
                            e.printStackTrace();
                        }
                    }
                }
                event.getChannel().sendMessageEmbeds(info.build()).queue();
                if(noError && role!=null) {
                    event.getChannel().sendMessage(role).queue();
                }
                info.clear();
            } catch (SQLException | NumberFormatException | ParseException | IOException | URISyntaxException e) {
                String error = Arrays.toString(e.getStackTrace());
                if(error.contains("SQLException")) {
                    event.getChannel().sendMessageEmbeds(Exceptions.sqlConnection().build()).queue();
                    Exceptions.sqlConnection().clear();
                } else if (!argumento.contains("ArrayIndexOutOfBoundsException")) {
                    event.getChannel().sendMessageEmbeds(Exceptions.numberFormat(args[2]).build()).queue();
                    Exceptions.numberFormat(args[2]).clear();
                }
                e.printStackTrace();
            }

        } else if (args[0].equalsIgnoreCase(Reminding.prefix + "rmd")
                && args.length>=2
                && args[1].equalsIgnoreCase("delete")) {
            EmbedBuilder info = new EmbedBuilder();

            Long serverID = Long.parseLong(event.getGuild().getId());
            Long channelID = Long.parseLong(event.getChannel().getId());

            String argumento;
            try {
                argumento = args[2];
            } catch (ArrayIndexOutOfBoundsException e) {
                argumento = "ArrayIndexOutOfBoundsException";
                info.addField("ERROR :","Digite o ID de evento!", false);
                info.addField("Formato correto :", "!!rmd delete [ID]", false);
                info.setColor(0xff0000);
                event.getChannel().sendMessageEmbeds(info.build()).queue();
                info.clear();
            }
            if(!argumento.contains("ArrayIndexOutOfBoundsException")) {
                try {
                    Long messageID = Long.parseLong(args[2]);

                    String[] deletedMessage = Delete.delete(messageID, serverID, channelID);
                    if(deletedMessage==null) {
                        info.addField("ERROR :", "Não existe um evento com este ID("+ messageID +") neste servidor!", false);
                        info.addField("Tente :", "!!rmd idlist para ver os últimos 25 eventos deste servidor!", false);
                        info.setColor(0xff0000);
                    } else {
                        info.setTitle("📚  RemindingBot: Evento deletado  ⏰\n"
                                + "----------------------------------------------");
                        if (deletedMessage[0]==null) {
                            info.addField("Nome :", "{Nome do evento", false);
                        } else {
                            info.addField("Nome :", deletedMessage[0], false);
                        }
                        if (deletedMessage[1]==null) {
                            info.addField("Data final :", "{Data do evento - Horário do evento}", false);
                        } else {
                            info.addField("Data final :", deletedMessage[1].toString(), false);
                        }
                        if (deletedMessage[2]==null) {
                            info.addField("Descrição :", "{Descrição}", false);
                        } else {
                            info.addField("Descrição :", deletedMessage[2].toString(), false);
                        }
                        info.setFooter("Última atualização feita por : " + deletedMessage[3]);
                        info.setColor(0x2d3b7a);
                    }
                    event.getChannel().sendMessageEmbeds(info.build()).queue();
                    info.clear();
                } catch (SQLException | NumberFormatException | IOException | URISyntaxException e) {
                    String error = Arrays.toString(e.getStackTrace());
                    if (error.contains("SQLException")) {
                        e.printStackTrace();
                        event.getChannel().sendMessageEmbeds(Exceptions.sqlConnection().build()).queue();
                        Exceptions.sqlConnection().clear();
                    } else {
                        event.getChannel().sendMessageEmbeds(Exceptions.numberFormat(args[2]).build()).queue();
                        Exceptions.numberFormat(args[2]).clear();
                    }
                }
            }

        } else if (args[0].equalsIgnoreCase(Reminding.prefix + "rmd")
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
        } else if (args[0].equalsIgnoreCase(Reminding.prefix + "rmd")) {
            EmbedBuilder info = new EmbedBuilder();
            info.addField("Esse comando não existe!","Use [ !!rmd commands ] para ver todas funções disponíveis!", false);
            info.setColor(0xff0000);

            event.getChannel().sendMessageEmbeds(info.build()).queue();
            info.clear();
        }
    }
}
//info.addField("Teste: ", "<#"+channelID+">",false); Exemplo de como marcar um chat...
