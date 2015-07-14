package net.jeremycasey.hamiltonheatalert.datetime;

import org.joda.time.DateTime;

public class SystemTimeProvider implements TimeProvider {
    @Override
    public DateTime now() {
        return DateTime.now();
    }
}
