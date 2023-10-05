package top.ienjoy.cybergarage.http;

import java.util.Calendar;
import java.util.TimeZone;

@SuppressWarnings("unused")
public class Date {
    private Calendar cal;

    public Date(Calendar cal) {
        this.cal = cal;
    }

    public Calendar getCalendar() {
        return cal;
    }

    ////////////////////////////////////////////////
    //	Time
    ////////////////////////////////////////////////

    public int getHour() {
        // Thanks for Theo Beisch (10/20/04)
        return getCalendar().get(Calendar.HOUR_OF_DAY);
    }

    public int getMinute() {
        return getCalendar().get(Calendar.MINUTE);
    }

    public int getSecond() {
        return getCalendar().get(Calendar.SECOND);
    }

    ////////////////////////////////////////////////
    //	paint
    ////////////////////////////////////////////////

    public static Date getLocalInstance() {
        return new Date(Calendar.getInstance());
    }

    public static Date getInstance() {
        // Thanks for Theo Beisch (10/20/04)
        return new Date(Calendar.getInstance(TimeZone.getTimeZone("GMT")));
    }

    ////////////////////////////////////////////////
    //	getDateString
    ////////////////////////////////////////////////

    public static String toDateString(int value) {
        if (value < 10)
            return "0" + value;
        return Integer.toString(value);
    }

    private final static String[] MONTH_STRING = {
            "Jan",
            "Feb",
            "Mar",
            "Apr",
            "May",
            "Jun",
            "Jul",
            "Aug",
            "Sep",
            "Oct",
            "Nov",
            "Dec",
    };

    public static String toMonthString(int value) {
        if (0 <= value && value < 12)
            return MONTH_STRING[value];
        return "";
    }

    private final static String[] WEEK_STRING = {
            "Sun",
            "Mon",
            "Tue",
            "Wed",
            "Thu",
            "Fri",
            "Sat",
    };

    public static String toWeekString(int value) {
        value -= Calendar.SUNDAY;
        if (0 <= value && value < 7)
            return WEEK_STRING[value];
        return "";
    }

    public static String toTimeString(int value) {
        String str = "";
        if (value < 10)
            str += "0";
        str += Integer.toString(value);
        return str;
    }

    public String getDateString() {
        // Thanks for Theo Beisch (10/20/04)
        Calendar cal = getCalendar();
        return toWeekString(cal.get(Calendar.DAY_OF_WEEK)) + ", " +
                toTimeString(cal.get(Calendar.DATE)) + " " +
                toMonthString(cal.get(Calendar.MONTH)) + " " +
                cal.get(Calendar.YEAR) + " " +
                toTimeString(cal.get(Calendar.HOUR_OF_DAY)) + ":" +
                toTimeString(cal.get(Calendar.MINUTE)) + ":" +
                toTimeString(cal.get(Calendar.SECOND)) + " GMT";
    }

    ////////////////////////////////////////////////
    //	getTimeString
    ////////////////////////////////////////////////

    public String getTimeString() {
        // Thanks for Theo Beisch (10/20/04)
        Calendar cal = getCalendar();
        return toDateString(cal.get(Calendar.HOUR_OF_DAY)) +
                (((cal.get(Calendar.SECOND) % 2) == 0) ? ":" : " ") +
                toDateString(cal.get(Calendar.MINUTE));
    }

}

