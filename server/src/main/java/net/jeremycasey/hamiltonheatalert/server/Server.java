package net.jeremycasey.hamiltonheatalert.server;

import net.jeremycasey.hamiltonheatalert.datetime.SystemTimeProvider;
import net.jeremycasey.hamiltonheatalert.heatstatus.HeatStatus;
import net.jeremycasey.hamiltonheatalert.heatstatus.HeatStatusFetcher;
import net.jeremycasey.hamiltonheatalert.heatstatus.HeatStatusIsImportantChecker;
import net.jeremycasey.hamiltonheatalert.heatstatus.HeatStatusLogger;
import net.jeremycasey.hamiltonheatalert.heatstatus.ServerHeatStatusIsImportantChecker;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Server {
    static Logger logger = LogManager.getLogger(Server.class.getName());

    public static void main(String[] args) {
        logger.info("Heat Alert server running...");
        if (args.length > 0) {
            int heatRating = Integer.parseInt(args[0]);
            sendManualMessageToGcm(heatRating);
        } else {
//            continuouslyCheckAlertStatusAndSendGcmMessageIfNecessary();
            checkAlertStatusAndSendGcmMessageIfNecessary();
        }
        logger.info("...Heat Alert finished");
    }

    private static void sendManualMessageToGcm(int heatRating) {
        try {
            System.out.println(heatRating);
            HeatStatus heatStatus = HeatStatus.createMockedHeatStatus(heatRating);
            logger.trace("Sending custom alert to gcm for \"" + heatStatus.getStageText() + "\"");
            new GcmSender(heatStatus).send();
            logger.info("Sent custom alert to gcm for \"" + heatStatus.getStageText() + "\"");
        } catch (Exception ex) {
            logger.error("Failed to broadcast one-off heat alert for heat rating " + heatRating, ex);
        }
    }

    private static void continuouslyCheckAlertStatusAndSendGcmMessageIfNecessary() {
        final int checkEveryMinutes = 5;
        logger.info("Watcher started. The hamilton heat alert rss feed will be checked every " + checkEveryMinutes + " minutes.");
        while (true) {
            checkAlertStatusAndSendGcmMessageIfNecessary();

            try {
                Thread.sleep((long)(checkEveryMinutes * 1000 * 60));
            } catch (InterruptedException ex) {
                logger.error("Sleep thread interrupted", ex);
            }
        }
    }

    private static void checkAlertStatusAndSendGcmMessageIfNecessary() {
        try {
            HeatStatus heatStatus = new HeatStatusFetcher().run();

            HeatStatusLogger heatStatusLogger = new HeatStatusFileLogger();
            HeatStatusIsImportantChecker checker = new ServerHeatStatusIsImportantChecker(heatStatus.getStage(), new SystemTimeProvider());

            if (checker.shouldNotify(heatStatusLogger)) {
                logger.trace("Sending alert to gcm for \"" + heatStatus.getStageText() + "\"");
                new GcmSender(heatStatus).send();
                heatStatusLogger.setLastNotifiedStatus(heatStatus);
                logger.info("Sent alert to gcm for \"" + heatStatus.getStageText() + "\"");
            }
            heatStatusLogger.setMostRecentStatus(heatStatus);
        } catch (Exception ex) {
            logger.error("There was an error on the heat alert server", ex);
            ErrorNotifier.notify(ex);
        }
    }
}