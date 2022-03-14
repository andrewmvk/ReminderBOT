package rmd.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rmd.date.Time;
import rmd.embed.EmbedMessage;
import rmd.errors.Exceptions;
import rmd.reminding.Reminding;
import rmd.sequelize.Select;
import rmd.sequelize.Start;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Properties;

public class GuildMessageUpcoming extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        if (args[0].equalsIgnoreCase(Reminding.prefix + "rmd")
                && args.length>=2
                && args[1].equalsIgnoreCase("upcoming")) {
            EmbedBuilder info = new EmbedBuilder();

            String argumento;

            boolean isBot = false;
            boolean noError = false;
            String role = null;
            int messagesLength = 0;
            String messageFinalID = null;

            Long serverID = Long.parseLong(event.getGuild().getId());
            //Long channelID = Long.parseLong(event.getChannel().getId());
            //Future implementation for a best message filter

            if (event.getMember().getUser().equals(event.getJDA().getSelfUser())) {
                String lastMessageID = event.getChannel().getLatestMessageId();
                List<Message> messageList = event.getChannel().getHistoryBefore(lastMessageID,1).complete().getRetrievedHistory();
                Message message = messageList.get(0);
                String messageID = message.getId();
                String author = message.getAuthor().getName();
                String messageType = message.getEmbeds().toString();
                String botName = event.getJDA().getSelfUser().getName();

                if(author.equals(botName) && messageType.contains("MessageEmbed")) {
                    //Verification to see if this is a embed message and if this is a message written by the bot (botName)
                    messageFinalID = messageID;
                }

                event.getMessage().delete().queue();
                isBot = true;
            }

            try {
                try {
                    argumento = args[2];
                } catch (ArrayIndexOutOfBoundsException e) {
                    argumento = "ArrayIndexOutOfBoundsException";
                }
                if(argumento.contains("ArrayIndexOutOfBoundsException")) {
                    //Without a second arg
                    String[][] messages;
                    try {
                        messages = Select.selectMessages(serverID);
                        messagesLength = messages.length;
                        try {
                            for (int i = 0; i < messagesLength; i++) {
                                if (messages[i][3]!=null) {
                                    if (!messages[i][3].contains("everyone")) {
                                        role = "<@&" + messages[i][3] + ">";
                                        break;
                                    }
                                }
                            }
                        } catch (NullPointerException e) {
                            //Role not defined for a new created event
                            role = null;
                        }

                        noError = true;
                        info = EmbedMessage.upcomingEmbed(info, messages, messagesLength);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        info.addField("ERROR:", "Não existe nenhum evento cadastrado neste servidor!", false);
                        info.setColor(0xff0000);
                    } catch (IOException | URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else {
                    //With a secondary arg (messageID)
                    long messageId = Long.parseLong(args[2]);
                    String[] message = Select.select(messageId ,serverID);
                    if(message[3]==null) {
                        info.addField("ERROR :", "Não existe nenhum evento com esse ID("+args[2]+") neste servidor!", false);
                        info.setColor(0xff0000);
                    } else {
                        try {
                            String title, date, description;
                            for (int j=0; j<4; j++) {
                                if(message[j]==null) {
                                    message[j]="Não informado!";
                                }
                            }
                            title = message[0];
                            date = message[1];
                            description = message[2];

                            String lastChangeName = event.getMember().getEffectiveName();
                            String lastChangeAvatarURL = event.getMember().getUser().getAvatarUrl();
                            info = EmbedMessage.modifiedEmbed(info, message, messageId, lastChangeName, lastChangeAvatarURL);
                            if (title.contains("Não informado!") && date.contains("Não informado!") && description.contains("Não informado!")) {
                                info.addField("Aviso :", "Este evento pode ser deletado em menos \nde um dia por falta de informações!", false);
                            }
                            info.setFooter("Criado por : " + message[3]);

                        } catch (NullPointerException | ParseException e) {
                            if (e.toString().contains("NullPointerException")) {
                                info = Exceptions.idNotFound(args[2]);
                            } else {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                if(noError && role!=null && !isBot) {
                   event.getChannel().sendMessage(role + ", Clique em 📆 para atualizar.").queue();
                }
                if (messageFinalID!=null) {
                    //The message is edited if the messageFinalID isn't NULL
                    event.getChannel().editMessageEmbedsById(messageFinalID, info.build()).queue();
                } else {
                    event.getChannel().sendMessageEmbeds(info.build()).queue();
                }
                info.clear();
            } catch (SQLException | NumberFormatException | ParseException | IOException | URISyntaxException | NullPointerException | ArrayIndexOutOfBoundsException e) {
                if (e.toString().contains("NullPointerException")) {
                    info = Exceptions.idNotFound(args[2]);
                } else if(e.toString().contains("SQLException")) {
                    info = Exceptions.sqlConnection();
                }
                e.printStackTrace();
                event.getChannel().sendMessageEmbeds(info.build()).queue();
                info.clear();
            }
        }
    }
}
