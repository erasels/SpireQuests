package spireQuests.quests.ramchops.trackers;

import spireQuests.quests.OptionalTriggerTracker;

public class ClericRewardTracker extends OptionalTriggerTracker<Integer> {

    public ClericRewardTracker(){
        super(ClericFundsTracker.CLERIC_FUND_CHANGE, 0, true);
    }

    @Override
    public void trigger(Integer param) {

        int DIVISOR = 15;
        localCount = Math.floorDiv(param, DIVISOR);
    }
}
