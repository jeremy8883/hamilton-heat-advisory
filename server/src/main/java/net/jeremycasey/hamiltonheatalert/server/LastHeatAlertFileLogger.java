package net.jeremycasey.hamiltonheatalert.server;


import com.google.gson.Gson;

import net.jeremycasey.hamiltonheatalert.heatstatus.LastFetchedHeatStatus;
import net.jeremycasey.hamiltonheatalert.heatstatus.LastFetchedHeatStatusLogger;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class LastHeatAlertFileLogger implements LastFetchedHeatStatusLogger {
    private static final String LOG_FILENAME = "last-fetched-heat-status.json";

    public LastHeatAlertFileLogger() { }

    @Override
    public void logFetchedStatus(LastFetchedHeatStatus heatStatusToLog) {
        FetchedHeatStatuses fetchedHeatStatuses = getFetchedHeatStatuses();
        fetchedHeatStatuses.setLastFetchedHeatStatus(heatStatusToLog);
        writeFetchedHeatStatuses(fetchedHeatStatuses);
    }

    @Override
    public void logFetchedAndNotifiedStatus(LastFetchedHeatStatus heatStatusToLog) {
        FetchedHeatStatuses fetchedHeatStatuses = getFetchedHeatStatuses();
        fetchedHeatStatuses.setLastFetchedAndNotifiedHeatStatus(heatStatusToLog);
        writeFetchedHeatStatuses(fetchedHeatStatuses);
    }

    @Override
    public LastFetchedHeatStatus getLastFetchedHeatStatus() {
        return getFetchedHeatStatuses().getLastFetchedHeatStatus();
    }

    @Override
    public LastFetchedHeatStatus getLastFetchedAndNotifiedHeatStatus() {
        return getFetchedHeatStatuses().getLastFetchedAndNotifiedHeatStatus();
    }

    private void writeFetchedHeatStatuses(FetchedHeatStatuses fetchedHeatStatuses) {
        Gson gson = new Gson();
        String json = gson.toJson(fetchedHeatStatuses);
        try {
            FileUtils.writeStringToFile(new File(LOG_FILENAME), json, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private FetchedHeatStatuses getFetchedHeatStatuses() {
        String json = null;
        try {
            File file = new File(LOG_FILENAME);
            if (!file.exists()) {
                return new FetchedHeatStatuses();
            }
            json = FileUtils.readFileToString(file, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Gson gson = new Gson();
        return gson.fromJson(json, FetchedHeatStatuses.class);
    }

    public class FetchedHeatStatuses {
        private LastFetchedHeatStatus lastFetchedHeatStatus = null;
        private LastFetchedHeatStatus lastFetchedAndNotifiedHeatStatus = null;

        public LastFetchedHeatStatus getLastFetchedAndNotifiedHeatStatus() {
            return lastFetchedAndNotifiedHeatStatus;
        }

        public void setLastFetchedAndNotifiedHeatStatus(LastFetchedHeatStatus lastFetchedAndNotifiedHeatStatus) {
            this.lastFetchedAndNotifiedHeatStatus = lastFetchedAndNotifiedHeatStatus;
        }

        public LastFetchedHeatStatus getLastFetchedHeatStatus() {
            return lastFetchedHeatStatus;
        }

        public void setLastFetchedHeatStatus(LastFetchedHeatStatus lastFetchedHeatStatus) {
            this.lastFetchedHeatStatus = lastFetchedHeatStatus;
        }
    }
}
