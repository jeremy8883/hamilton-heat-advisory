package net.jeremycasey.hamiltonheatalert.heatadvisory;

public class HeatAdvisoryIsImportantChecker {
    private HeatAdvisory mHeatAdvisory;
    public HeatAdvisoryIsImportantChecker(HeatAdvisory heatAdvisory) {
        mHeatAdvisory = heatAdvisory;
    }

    //TODO
    public boolean isImportant() {
        return true;
//        return dangerIsSeriousEnough() &&
//                isTheHighestDangerLevelInThePastDay();
    }
//
//    private boolean dangerIsSeriousEnough() {
//        return mHeatAdvisory.getStage() > 0;
//    }
//
//    private boolean isTheHighestDangerLevelInThePastDay() {
//        for (HeatAdvisory prevHeatAdvisory : getNotificationsSentInThePastDay()) {
//            if (mHeatAdvisory.getStage() <= prevHeatAdvisory.getStage()) {
//                return false;
//            }
//        }
//        return true;
//    }
}
