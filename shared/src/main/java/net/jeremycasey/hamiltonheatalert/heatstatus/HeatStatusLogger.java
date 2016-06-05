package net.jeremycasey.hamiltonheatalert.heatstatus;

/**
 * Interface used between the server and android app to keep a history of when notifications are
 * sent and received
 */
public interface HeatStatusLogger {
    void setMostRecentStatus(HeatStatus heatStatus);
    void setLastNotifiedStatus(HeatStatus heatStatus);
    HeatStatus getMostRecentStatus();
    HeatStatus getLastNotifiedStatus();
}

