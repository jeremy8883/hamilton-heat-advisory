package net.jeremycasey.hamiltonheatalert.server;


import com.google.gson.Gson;

import net.jeremycasey.hamiltonheatalert.heatstatus.LoggedHeatStatus;
import net.jeremycasey.hamiltonheatalert.heatstatus.HeatStatusLogger;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class HeatStatusFileLogger implements HeatStatusLogger {
    private static final String LOG_FILENAME = "last-fetched-heat-status.json";

    public HeatStatusFileLogger() { }

    @Override
    public void setMostRecentStatus(LoggedHeatStatus heatStatusToLog) {
        FetchedHeatStatuses fetchedHeatStatuses = getFetchedHeatStatuses();
        fetchedHeatStatuses.setLastFetchedHeatStatus(heatStatusToLog);
        writeFetchedHeatStatuses(fetchedHeatStatuses);
    }

    @Override
    public void setLastNotifiedStatus(LoggedHeatStatus heatStatusToLog) {
        FetchedHeatStatuses fetchedHeatStatuses = getFetchedHeatStatuses();
        fetchedHeatStatuses.setLastFetchedAndNotifiedHeatStatus(heatStatusToLog);
        writeFetchedHeatStatuses(fetchedHeatStatuses);
    }

    @Override
    public LoggedHeatStatus getMostRecentStatus() {
        return getFetchedHeatStatuses().getLastFetchedHeatStatus();
    }

    @Override
    public LoggedHeatStatus getLastNotifiedStatus() {
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
        private LoggedHeatStatus lastFetchedHeatStatus = null;
        private LoggedHeatStatus lastFetchedAndNotifiedHeatStatus = null;

        public LoggedHeatStatus getLastFetchedAndNotifiedHeatStatus() {
            return lastFetchedAndNotifiedHeatStatus;
        }

        public void setLastFetchedAndNotifiedHeatStatus(LoggedHeatStatus lastFetchedAndNotifiedHeatStatus) {
            this.lastFetchedAndNotifiedHeatStatus = lastFetchedAndNotifiedHeatStatus;
        }

        public LoggedHeatStatus getLastFetchedHeatStatus() {
            return lastFetchedHeatStatus;
        }

        public void setLastFetchedHeatStatus(LoggedHeatStatus lastFetchedHeatStatus) {
            this.lastFetchedHeatStatus = lastFetchedHeatStatus;
        }
    }
}
