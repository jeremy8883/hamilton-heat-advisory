package net.jeremycasey.hamiltonheatalert.server;

import net.jeremycasey.hamiltonheatalert.gcm.GcmSettings;
import net.jeremycasey.hamiltonheatalert.heatstatus.HeatStatus;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GcmSender {
    private static String GCM_SETTINGS_FILE = "gcm-settings.json";

    private HeatStatus mHeatStatus;

    public GcmSender(HeatStatus heatStatus) {
        mHeatStatus = heatStatus;
    }

    public void send() throws IOException {
        // Prepare JSON containing the GCM message content. What to send and where to send.
        JSONObject jGcmData = new JSONObject();
        JSONObject jData = new JSONObject();

        String message = new JSONObject(mHeatStatus).toString();

        jData.put("message", message);
        // Where to send GCM message.
        jGcmData.put("to", "/topics/" + GcmSettings.TOPIC);

        // What to send in GCM message.
        jGcmData.put("data", jData);

        // Create connection to send GCM Message request.
        URL url = null;
        try {
            url = new URL("https://android.googleapis.com/gcm/send");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Authorization", "key=" + getApiKey());
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        // Send GCM message content.
        OutputStream outputStream = conn.getOutputStream();
        outputStream.write(jGcmData.toString().getBytes());

        // Read GCM response.
        InputStream inputStream = conn.getInputStream();
        String resp = IOUtils.toString(inputStream);
        System.out.println(resp);
    }

    private String getApiKey() throws FileNotFoundException {
        JSONObject object = (JSONObject) new JSONTokener(new FileReader(GCM_SETTINGS_FILE)).nextValue();
        return object.getString("apiKey");
    }
}
