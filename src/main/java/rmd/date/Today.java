package rmd.date;

import java.text.ParseException;
import java.util.Calendar;

public class Today {
    public static String date() throws ParseException {
        Calendar hoje = Calendar.getInstance();

        int year = hoje.get(Calendar.YEAR);
        int month = hoje.get(Calendar.MONTH) + 1;
        int day = hoje.get(Calendar.DAY_OF_MONTH);
        int hour = hoje.get(Calendar.HOUR_OF_DAY);
        int minute = hoje.get(Calendar.MINUTE);
        int second = hoje.get(Calendar.SECOND);

        return day + "/" + month + "/" + year + " " + hour + ":" + minute + ":" + second;
    }
    public static String dateSixAm() {
        Calendar hoje = Calendar.getInstance();

        int year = hoje.get(Calendar.YEAR);
        int month = hoje.get(Calendar.MONTH) + 1;
        int day = hoje.get(Calendar.DAY_OF_MONTH) + 1;
        int hour = 6;
        int minute = 0;
        int second = 0;

        return day + "/" + month + "/" + year + " 0" + hour + ":0" + minute + ":0" + second;
    }

}
