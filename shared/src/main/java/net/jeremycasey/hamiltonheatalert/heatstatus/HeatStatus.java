package net.jeremycasey.hamiltonheatalert.heatstatus;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Objects;

public class HeatStatus implements Serializable {

    private String stageText;
    private int stage;
    private String imageUrl;
    private long lastBuildDate;
    private long fetchDate;


    public void setStageText(String stageText) {
        this.stageText = stageText;
    }

    public String getStageText() {
        return stageText;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public int getStage() {
        return stage;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setLastBuildDate(long lastBuildDate) {
        this.lastBuildDate = lastBuildDate;
    }

    public long getLastBuildDate() {
        return lastBuildDate;
    }

    public static HeatStatus createHeatAdvisory(int stage) {
        HeatStatus heatStatus = new HeatStatus();
        String[] stageTexts = new String[] {
            "Monitoring", "Heat Advisory", "Heat Warning", "Extreme Heat Warning"
        };
        heatStatus.setStageText(stageTexts[stage] + " - Stage " + stage);
        heatStatus.setStage(stage);
        heatStatus.setImageUrl("http://old.hamilton.ca/databases/phcs/heatalert/current1.jpg");
        heatStatus.setLastBuildDate(new DateTime().getMillis());
        return heatStatus;
    }

    private boolean equals(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }

    public long getFetchDate() {
        return fetchDate;
    }

    public void setFetchDate(long fetchDate) {
        this.fetchDate = fetchDate;
    }
}
