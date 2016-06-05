package net.jeremycasey.hamiltonheatalert.server;

import java.lang.Exception;
import java.lang.System;

public class ErrorNotifier {
    public static void notify(Exception ex) {
        System.out.println(ex.getStackTrace());
        //TODO send an error notification and log it
    }
}