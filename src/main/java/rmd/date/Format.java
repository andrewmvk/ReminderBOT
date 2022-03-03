package rmd.date;

public class Format {
    public static String correction (String concatenatedString ,boolean isTimeLeft) {
        String[] dateHour = concatenatedString.split(" ");
        String date = dateHour[0];
        String hours = dateHour[1];

        String[] dayMonthYear = date.split("/");
        int dias = Integer.parseInt(dayMonthYear[0]);
        int meses = Integer.parseInt(dayMonthYear[1]);
        int anos = Integer.parseInt(dayMonthYear[2]);

        String[] hourMinuteSecond = hours.split(":");
        String hour = hourMinuteSecond[0];
        String minute = hourMinuteSecond[1];
        String second = hourMinuteSecond[2];

        String finalTemp = "";
        if (isTimeLeft) {
            if (Integer.parseInt(hour)<10){
                hour = "0" + hour;
            }

            if (Integer.parseInt(minute)<10) {
                minute = "0" + minute;
            }

            if (Integer.parseInt(second)<10){
                second = "0" + second;
            }

            if (anos!=0 && anos!=1) {
                finalTemp += anos + " anos, ";
            } else if(anos==1){
                finalTemp += anos + " ano, ";
            }
            if (meses!=0 && meses!=1) {
                finalTemp += meses + " meses, ";
            } else if (meses==1) {
                finalTemp += meses + " mês, ";
            }
            if (dias!=0 && dias!=1) {
                finalTemp += dias + " dias";
            } else if (dias==1) {
                finalTemp += dias + " dia";
            }
            if (anos==0 && meses==0 && dias==0) {
                finalTemp += hour + ":" + minute + ":" + second + " horas";
            } else {
                finalTemp += " e " + hour + ":" + minute + ":" + second + " horas";
            }

        } else {
            if (dias<10) {
                finalTemp += "0" + dias + "/";
            } else {
                finalTemp += dias + "/";
            }
            if (meses<10) {
                finalTemp += "0" + meses + "/";
            } else {
                finalTemp += meses + "/";
            }
            finalTemp += anos + " ";

            if (Integer.parseInt(hour)<10) {
                finalTemp += "0" + hour + ":";
            }  else {
                finalTemp += hour + ":";
            }
            if (Integer.parseInt(minute)<10) {
                finalTemp += "0" + minute + ":";
            } else {
                finalTemp += minute + ":";
            }
            if (Integer.parseInt(second)<10) {
                finalTemp += "0" + second;
            } else {
                finalTemp += second;
            }
        }
        return  finalTemp;
    }
}
