package rmd.errors;

import net.dv8tion.jda.api.EmbedBuilder;

public class Exceptions {
    public static EmbedBuilder parseID() {
        EmbedBuilder errorInfo = new EmbedBuilder();
        errorInfo.addField("ERRO : ", "Formato de ID incorreto! Lembre-se de utilizar apenas números", false );
        errorInfo.addField("Formato correto :", "!!rmd modify name/description [ID] [Texto]", false);
        errorInfo.addField("Ou tente :", "!!rmd commands para visualizar todos os comandos", false);
        errorInfo.setColor(0xff0000);

        return errorInfo;
    }
    public static EmbedBuilder parseDate() {
        EmbedBuilder errorInfo = new EmbedBuilder();
        errorInfo.addField("ERROR :", "Formato de data ou ID informado incorreto!", false);
        errorInfo.addField("Formato correto :", "!!rmd modify date [ID] [dd/MM/yyyy HH:mm:ss]", false);
        errorInfo.addField("Lembre-se :", "[dd] ≤ 31, [MM] ≤ 12 e [yyyy] > 0;\n" +
                                 "Considere também [dd] ≤ 29 (ano bissexto);\n" +
                                 "[HH] ≤ 23, [mm] ≤ 59 e [ss] ≤ 59, TODOS positivos.", false);
        errorInfo.addField("Ou tente :", "!!rmd commands para visualizar todos os comandos", false);
        errorInfo.setColor(0xff0000);

        return errorInfo;
    }
    public static EmbedBuilder sqlConnection() {
        EmbedBuilder errorInfo = new EmbedBuilder();
        errorInfo.addField("ERROR :", "Conexão com o banco de dados, tente novamente mais tarde!", false);
        errorInfo.setColor(0xff0000);

        return errorInfo;
    }
    public static EmbedBuilder outOfBounds() {
        EmbedBuilder errorInfo = new EmbedBuilder();
        errorInfo.addField("ERROR :", "Comando informado incorreto!", false);
        errorInfo.addField("Formato correto :", "!!rmd modify name/date/description [ID]", false);
        errorInfo.addField("Ou tente :", "!!rmd commands para visualizar todos os comandos", false);
        errorInfo.setColor(0xff0000);

        return errorInfo;
    }
    public static EmbedBuilder numberFormat(String ID) {
        EmbedBuilder errorInfo = new EmbedBuilder();
        errorInfo.addField("ERROR :", "Formato de ID informado (" + ID +") incorreto, tente utilizar apenas números!", false);
        errorInfo.addField("Formato correto :", "!!rmd modify name/date/description [ID]", false);
        errorInfo.addField("Ou tente :", "!!rmd commands para visualizar todos os comandos", false);
        errorInfo.setColor(0xff0000);

        return errorInfo;
    }
}
