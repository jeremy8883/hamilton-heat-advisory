package net.jeremycasey.hamiltonheatalert.datetime;

import org.joda.time.DateTime;

public interface TimeProvider {
    DateTime now();
}
