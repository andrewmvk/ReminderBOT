package rmd.errors;

import net.dv8tion.jda.api.EmbedBuilder;

public class Exceptions {
    public static EmbedBuilder sqlConnection() {
        EmbedBuilder errorInfo = new EmbedBuilder();
        errorInfo.addField("ERROR :", "Conexão com o banco de dados, tente novamente mais tarde!", false);
        errorInfo.setColor(0xff0000);

        return errorInfo;
    }
    public static EmbedBuilder outOfBounds() {
        EmbedBuilder errorInfo = new EmbedBuilder();
        errorInfo.addField("ERROR :", "ID da mensagem incorreto ou não digitado!", false);
        errorInfo.addField("Lembre-se :", "!!rmd modify name/date/description [ID]", false);
        errorInfo.setColor(0xff0000);

        return errorInfo;
    }
    public static EmbedBuilder idNotFound(String ID) {
        EmbedBuilder errorInfo = new EmbedBuilder();
        errorInfo.addField("ERRO : ", "Não existe uma mensagem com esse ID(" + ID + ") neste servidor!", false);
        errorInfo.addField("Comando : ", "!!rmd modify name/date/description [ID] [texto]", false);
        errorInfo.addField("Ou tente :", "!!rmd commands para visualizar todos os comandos", false);
        errorInfo.setColor(0xff0000);

        return errorInfo;
    }
    public static EmbedBuilder idNotInformed() {
        EmbedBuilder errorInfo = new EmbedBuilder();
        errorInfo.addField("ERROR :","Digite o ID de evento!", false);
        errorInfo.addField("Formato correto :", "!!rmd delete [ID]", false);
        errorInfo.setColor(0xff0000);

        return errorInfo;
    }
    public static EmbedBuilder incorrectModifyCommand(String type) {
        EmbedBuilder errorInfo = new EmbedBuilder();
        if(type.equals("name")) {
            errorInfo.addField("ERROR :", "Nome/ID não digitado ou formato incorreto!", false);
            errorInfo.addField("Formato correto :", "!!rmd modify name [ID] [name]", false);
            errorInfo.setColor(0xff0000);
        } else if(type.equals("description")) {
            errorInfo.addField("ERROR :", "Descrição/ID não digitado ou formato incorreto!", false);
            errorInfo.addField("Formato correto :", "!!rmd modify description [ID] [texto]", false);
            errorInfo.setColor(0xff0000);
        } else if(type.equals("date")) {
            errorInfo.addField("ERROR :", "Formato de data ou ID informado incorreto!", false);
            errorInfo.addField("Formato correto :", "!!rmd modify date [ID] [dd/MM/yyyy HH:mm:ss]", false);
            errorInfo.addField("Lembre-se :", "[dd] ≤ 31, [MM] ≤ 12 e [yyyy] > 0;\n" +
                    "Considere também [dd] ≤ 29 (ano bissexto);\n" +
                    "[HH] ≤ 23, [mm] ≤ 59 e [ss] ≤ 59, TODOS positivos.", false);
            errorInfo.addField("Ou tente :", "!!rmd commands para visualizar todos os comandos", false);
            errorInfo.setColor(0xff0000);
        }
        return errorInfo;
    }
}
