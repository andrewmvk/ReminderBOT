package rmd.date;

import javax.annotation.Nullable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Time {
    public static String[] timeLeft(String date, int duration) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        //TODO dateFormat.setTimeZone(TimeZone.getTimeZone("BRT"));
        Calendar cal = Calendar.getInstance();
        Date eventDate = dateFormat.parse(date);
        Date today = dateFormat.parse(Today.date());
        String finalTemp;
        String remaining = null;
        long timeRemaining = -1;

        long diffEmMili = eventDate.getTime()-today.getTime();
        
        if(System.getenv("TOKEN")!=null) {
            diffEmMili += 10800000;
        }
        if (diffEmMili <= 0 && duration != 0) {
            timeRemaining = diffEmMili+((long) duration *1000*60);
        }
        if(diffEmMili + 3600000 <= 0 && timeRemaining < 0) {
            finalTemp = "Tempo excedido!";
        } else if (diffEmMili <= 0 && timeRemaining < 0) {
            finalTemp = "Evento já aconteceu!";
        } else {
            Date tempo;
            if(System.getenv("TOKEN")!=null) {
                tempo = new Date(diffEmMili);
            } else {
                tempo = new Date(diffEmMili + 10800000);//+3 hours
            }
            cal.setTime(tempo);
            int meses = cal.get(Calendar.MONTH);
            int dias = cal.get(Calendar.DAY_OF_MONTH) - 1;
            int anos = cal.get(Calendar.YEAR) - 1970;
            int horas = cal.get(Calendar.HOUR_OF_DAY);
            int minutos = cal.get(Calendar.MINUTE);
            int segundos = cal.get(Calendar.SECOND);

            String temp = dias + "/" + meses + "/" + anos + " " + horas + ":" + minutos + ":" + segundos;

            Date remainingTime;
            if(timeRemaining>=0) {
                if(System.getenv("TOKEN")!=null) {
                    remainingTime = new Date(timeRemaining);
                } else {
                    remainingTime = new Date(timeRemaining + 10800000);
                }
                cal.setTime(remainingTime);
                int hours = cal.get(Calendar.HOUR_OF_DAY);
                int minutes = cal.get(Calendar.MINUTE);
                int seconds = cal.get(Calendar.SECOND);

                String hoursAux = String.valueOf(hours);
                String minutesAux = String.valueOf(minutes);
                String secondsAux = String.valueOf(seconds);

                if(hours < 10) {
                    hoursAux = 0 + "" + hours;
                }
                if(minutes < 10) {
                    minutesAux = 0 + "" + minutes;
                }
                if(seconds < 10) {
                    secondsAux = 0 + "" + seconds;
                }

                remaining = hoursAux + ":" + minutesAux + ":" + secondsAux;
            }

            finalTemp = Format.correction(temp, true);
        }
        String[] result = new String[2];
        result[0] = finalTemp;
        result[1] = remaining;

        return result;
    }
    public static long[] daysLeft (String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date today = dateFormat.parse(Today.date());
        Date eventDate = dateFormat.parse(date);

        long diffEmMilli = eventDate.getTime()-today.getTime();

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
