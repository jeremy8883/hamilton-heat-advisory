package net.jeremycasey.hamiltonheatalert.heatstatus;

/**
 * Interface used between the server and android app to keep a history of when notifications are
 * sent and received
 */
public interface HeatStatusLogger {
    void setMostRecentStatus(LoggedHeatStatus loggedHeatAlert);
    void setLastNotifiedStatus(LoggedHeatStatus loggedHeatAlert);
    LoggedHeatStatus getMostRecentStatus();
    LoggedHeatStatus getLastNotifiedStatus();
}

