package net.jeremycasey.hamiltonheatalert.heatstatus;

public class HeatStatusIsImportantChecker {
    private HeatStatus mHeatStatus;
    public HeatStatusIsImportantChecker(HeatStatus heatStatus) {
        mHeatStatus = heatStatus;
    }

    //TODO
    public boolean isImportant() {
        return true;
//        return dangerIsSeriousEnough() &&
//                isTheHighestDangerLevelInThePastDay();
    }
//
//    private boolean dangerIsSeriousEnough() {
//        return mHeatStatus.getStage() > 0;
//    }
//
//    private boolean isTheHighestDangerLevelInThePastDay() {
//        for (HeatStatus prevHeatAdvisory : getNotificationsSentInThePastDay()) {
//            if (mHeatStatus.getStage() <= prevHeatAdvisory.getStage()) {
//                return false;
//            }
//        }
//        return true;
//    }
}
