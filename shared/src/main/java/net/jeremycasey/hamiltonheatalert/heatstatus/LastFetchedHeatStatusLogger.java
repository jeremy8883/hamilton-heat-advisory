package net.jeremycasey.hamiltonheatalert.heatstatus;

/**
 * Interface used between the server and android app to keep a history of when notifications are
 * sent and received
 */
public interface LastFetchedHeatStatusLogger {
    void logFetchedStatus(LastFetchedHeatStatus loggedHeatAlert);
    void logFetchedAndNotifiedStatus(LastFetchedHeatStatus loggedHeatAlert);
    LastFetchedHeatStatus getLastFetchedHeatStatus();
    LastFetchedHeatStatus getLastFetchedAndNotifiedHeatStatus();
}

