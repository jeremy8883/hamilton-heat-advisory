package net.jeremycasey.hamiltonheatalert.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StackTrace {
    public static String toString(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }
}