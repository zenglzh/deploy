package com.jiuqi.deplay.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.apache.log4j.Logger;

/**
 *
 * @author esalaza
 */
public class TimeUtils {
    
    static Logger logger = Logger.getLogger(TimeUtils.class);

    public static long getTodayElapsedMilliseconds() {
        GregorianCalendar calendar = new GregorianCalendar();
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        return getMilliseconds(hours, minutes, seconds);
    }
    
    public static long getMilliseconds(int hours, int minutes, int seconds) {
        long milliseconds = (hours * 60 * 60 * 1000 ) + (minutes * 60 * 1000) + (seconds * 1000);
        return milliseconds;
    }
    
    public static long parseDateForInitiatedAtString(String dateString) {
        // String date in format:
        // - 2008-11-06T17:35:42+00:00
        // - 2008-11-06T17:35:42Z
        // - ...
        long datemillis = 0;
        dateString = dateString.substring(0, 19);
        dateString = dateString.replaceAll("T", " ");
        System.out.println("dateString: " + dateString);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = df.parse(dateString);
            datemillis = date.getTime();
        } catch (ParseException e) {
            logger.error("Error al parsear fecha", e);
        }
        return datemillis;
    }
    
    /**
     * Retorna el numero de milisegundos desde "la epoca" para una fecha en
     * formato DD/MM/AA.
     * @param fecha
     * @return
     * @throws java.lang.Exception
     */
    public static long getTime(String fecha) throws Exception {
        String[] partesFecha = fecha.split("/");
        int dia = Integer.parseInt(partesFecha[0]);
        int mes = Integer.parseInt(partesFecha[1]) - 1;
        int ano = Integer.parseInt(partesFecha[2]);
        GregorianCalendar f = new GregorianCalendar(ano, mes, dia);
        return f.getTime().getTime();
    }

    // :-S
    public static long parseTime(String formattedTime) throws Exception {
        long time = 0;
        boolean isValid = true;
        String[] parts = formattedTime.split(":");
        if (parts.length != 3) {
            isValid = false;
        }
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);
        if (hours > 59 || hours < 0) {
            isValid = false;
        } else if (minutes > 59 || minutes < 0) {
            isValid = false;
        } else if (seconds > 59 || seconds < 0) {
            isValid = false;
        }
        if (!isValid) {
            throw new Exception("Formato de hora invalido");
        }
        time =
                (hours * 60 * 60 * 1000) +
                (minutes * 60 * 1000) +
                (seconds * 1000);
        return time;
    }
    
    public static String longDuration2TimeDurationString(long duration) {
        long hours, minutes, seconds, miliseconds = 0;
        seconds     = duration / 1000;
        hours       = seconds  / 3600;
        seconds     = seconds  % 3600;
        minutes     = seconds  /   60;
        seconds     = seconds  %   60;
        miliseconds = duration % 1000;
        return hours + "h :" + minutes + "m :" + seconds + "s :" + miliseconds + "ms";
    }

}
