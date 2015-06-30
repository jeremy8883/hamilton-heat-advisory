package net.jeremycasey.hamiltonheatalert.heatadvisory;

import org.joda.time.DateTime;

import java.io.Serializable;

public class HeatAdvisory implements Serializable {

    private String stageText;
    private int stage;
    private String imageUrl;
    private long lastBuildDate;


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

    public static HeatAdvisory createHeatAdvisory(int stage) {
        HeatAdvisory heatAdvisory = new HeatAdvisory();
        String[] stageTexts = new String[] {
            "Monitoring", "Heat Advisory", "Heat Warning", "Extreme Heat Warning"
        };
        heatAdvisory.setStageText(stageTexts[stage] + " - Stage " + stage);
        heatAdvisory.setStage(stage);
        heatAdvisory.setImageUrl("http://old.hamilton.ca/databases/phcs/heatalert/current1.jpg");
        heatAdvisory.setLastBuildDate(new DateTime().getMillis());
        return heatAdvisory;
    }
}
