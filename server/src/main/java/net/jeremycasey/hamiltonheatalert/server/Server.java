package net.jeremycasey.hamiltonheatalert.server;

import net.jeremycasey.hamiltonheatalert.heatstatus.HeatStatus;
import net.jeremycasey.hamiltonheatalert.heatstatus.HeatStatusFetcher;
import net.jeremycasey.hamiltonheatalert.heatstatus.HeatStatusIsImportantChecker;
import net.jeremycasey.hamiltonheatalert.utils.*;

import java.lang.Exception;
import java.lang.Integer;
import java.lang.System;
import java.lang.Thread;

public class Server {
    public static void main(String[] args) {
        log("Heat Alert server running...");
        if (args.length > 0) {
            int heatRating = Integer.parseInt(args[0]);
            sendManualMessageToGcm(heatRating);
        } else {
            continuouslyCheckAlertStatusAndSendGcmMessageIfNessesary();
        }
    }

    private static void sendManualMessageToGcm(int heatRating) {
        try {
            System.out.println(heatRating);
            HeatStatus heatStatus = HeatStatus.createHeatAdvisory(heatRating);
            log("Sending custom alert to gcm for \"" + heatStatus.getStageText() + "\"");
            new GcmSender(heatStatus).send();
            log("Sent");
        } catch (Exception ex) {
            log(StackTrace.toString(ex));
        }
    }

    private static void continuouslyCheckAlertStatusAndSendGcmMessageIfNessesary() {
        int checkEveryMinutes = 5;
        log("Watcher started. The hamilton heat alert rss feed will be checked every " + checkEveryMinutes + " minutes.");
        while (true) {
            try {
                HeatStatus heatStatus = new HeatStatusFetcher().run();
                if (new HeatStatusIsImportantChecker(heatStatus).isImportant()) {
                    log("Sending alert to gcm for \"" + heatStatus.getStageText() + "\"");
                    new GcmSender(heatStatus).send();
                    log("Sent");
                }
            } catch (Exception ex) {
                log(StackTrace.toString(ex));
                ErrorNotifier.notify(ex);
            }
            try {
                Thread.sleep((long)(checkEveryMinutes * 1000 * 60));
            } catch (InterruptedException ex) {
                log(StackTrace.toString(ex));
            }
        }
    }

    private static void log(String text) {
        System.out.println(text);
        //TODO write to log file
    }
}