package net.jeremycasey.hamiltonheatalert.app.utils;

import org.joda.time.DateTime;

public class DateUtil {
    private static final double SECOND = 1000;
    private static final double MINUTE = SECOND * 60;
    private static final double HOUR = MINUTE * 60;
    private static final double DAY = HOUR * 24;

    public static String toRelativeString(long millis) {
        double now = new DateTime().getMillis();
        double difference = millis - now;

        return difference >= 2 * DAY ? new DateTime(millis).toString("d MMM yyyy") :
                difference >= DAY ? "Tomorrow" :
                difference >= HOUR ? plural("In %s hour", "In %s hours", (int)Math.floor(difference / HOUR)) :
                difference >= MINUTE ? plural("In %s minute", "In %s minutes", (int)Math.floor(difference / MINUTE)) :
                difference > 0 ? plural("In %s second", "In %s seconds", (int)Math.floor(difference / SECOND)) :
                difference > -1 * MINUTE ? "Just now" :
                difference > -1 * HOUR ? plural("%s minute ago", "%s minutes ago", (int)Math.floor(-difference / MINUTE)) :
                difference > -1 * DAY ? plural("%s hour ago", "%s hours ago", (int)Math.floor(-difference / HOUR)) :
                difference > -2 * DAY ? "In one day" :
                new DateTime(millis).toString("d MMM yyyy");
    }

    private static String plural(String strSingle, String strPlural, int amount) {
        return String.format(amount == 1 ? strSingle : strPlural, amount);
    }

    public static boolean isWithinDaysAgo(long dateTime, int numDays) {
        return new DateTime(dateTime).isAfter(DateTime.now().minusHours(numDays));
    }

    public static boolean isWithinMinsAgo(long dateTime, int numMins) {
        return new DateTime(dateTime).isAfter(DateTime.now().minusMinutes(numMins));
    }
}
