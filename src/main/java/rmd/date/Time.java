package rmd.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Time {
    public static String timeLeft(String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        Date eventDate = dateFormat.parse(date);
        Date today = dateFormat.parse(Today.date());
        String finalTemp;

        long diffEmMili = eventDate.getTime()-today.getTime();

        if(diffEmMili + 86400000 <= 0) {
            finalTemp = "Tempo excedido!";
        } else if (diffEmMili <= 0) {
            finalTemp = "Evento já aconteceu!";
        } else {
            Date tempo = new Date(diffEmMili + 10800000);//+3 hours
            cal.setTime(tempo);
            int meses = cal.get(Calendar.MONTH);
            int dias = cal.get(Calendar.DAY_OF_MONTH) - 1;
            int anos = cal.get(Calendar.YEAR) - 1970;
            int horas = cal.get(Calendar.HOUR_OF_DAY);
            int minutos = cal.get(Calendar.MINUTE);
            int segundos = cal.get(Calendar.SECOND);

            String temp = dias + "/" + meses + "/" + anos + " " + horas + ":" + minutos + ":" + segundos;

            finalTemp = Format.correction(temp, true);
        }

        return finalTemp;
    }
    public static long[] daysLeft (String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date today = dateFormat.parse(Today.date());
        Date eventDate = dateFormat.parse(date);

        long diffEmMilli = Math.abs(today.getTime()-eventDate.getTime());

        long days = TimeUnit.DAYS.convert(diffEmMilli, TimeUnit.MILLISECONDS);

        long[] remaining = new long[2];
        remaining[0] = days;
        remaining[1] = diffEmMilli;
        return remaining;
    }
    public static long timeUntilSixAm() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date tomorrow = dateFormat.parse(Today.dateSixAm());
        Date today = dateFormat.parse(Today.date());

        return Math.abs(tomorrow.getTime() - today.getTime());
    }
}
