package net.jeremycasey.hamiltonheatalert.server;

import net.jeremycasey.hamiltonheatalert.heatadvisory.HeatAdvisory;
import net.jeremycasey.hamiltonheatalert.heatadvisory.HeatAdvisoryFetcher;
import net.jeremycasey.hamiltonheatalert.heatadvisory.HeatAdvisoryIsImportantChecker;
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
            HeatAdvisory heatAdvisory = HeatAdvisory.createHeatAdvisory(heatRating);
            log("Sending alert to gcm for \"" + heatAdvisory.getStageText() + "\"");
            new GcmSender(heatAdvisory).send();
            log("Sent");
        } catch (Exception ex) {
            log(StackTrace.toString(ex));
        }
    }

    private static void continuouslyCheckAlertStatusAndSendGcmMessageIfNessesary() {
        while (true) {
            try {
                HeatAdvisory heatAdvisory = new HeatAdvisoryFetcher().run();
                if (new HeatAdvisoryIsImportantChecker(heatAdvisory).isImportant()) {
                    log("Sending alert to gcm for \"" + heatAdvisory.getStageText() + "\"");
                    new GcmSender(heatAdvisory).send();
                    log("Sent");
                }
            } catch (Exception ex) {
                log(StackTrace.toString(ex));
                ErrorNotifier.notify(ex);
            }
            try {
                Thread.sleep((long)(5 * 1000 * 60));
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