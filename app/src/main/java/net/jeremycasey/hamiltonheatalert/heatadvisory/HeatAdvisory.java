package net.jeremycasey.hamiltonheatalert.heatadvisory;

import org.joda.time.DateTime;

import java.io.Serializable;

public class HeatAdvisory implements Serializable {

    private String stageText;
    private int stage;
    private String imageUrl;
    private DateTime lastBuildDate;


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

    public void setLastBuildDate(DateTime lastBuildDate) {
        this.lastBuildDate = lastBuildDate;
    }

    public DateTime getLastBuildDate() {
        return lastBuildDate;
    }
}
