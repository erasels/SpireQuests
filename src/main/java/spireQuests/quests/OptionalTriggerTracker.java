package spireQuests.quests;

import spireQuests.Anniv8Mod;

/**
 * An UpdateTracker that doesn't track progress and is always considered complete to track certain parameters during quests.
 * @param <T>
 */
public class OptionalTriggerTracker<T> extends AbstractQuest.TriggerTracker<T> {

    public int localCount = 0;
    public int localGoal;
    boolean hideProgress = false;

    public OptionalTriggerTracker(Trigger<T> trigger, int count, boolean hideProgress) {
        super(trigger, count, ()->false);

        localGoal = count;

        this.hideProgress = hideProgress;
    }

    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public String progressString() {

        if (hideProgress){
            return String.format(" (%d)", this.localCount);
        }else{
            return String.format(" (%d/%d)", this.localCount, this.localGoal);
        }
    }

    @Override
    public String saveData() {
        return String.valueOf(localCount);
    }

    @Override
    public void loadData(String data) {
        super.loadData(data);
        try {
            localCount = Integer.parseInt(data);
        }
        catch (Exception e) {
            Anniv8Mod.logger.error("Failed to load tracker data for '" + text + "'", e);
        }
    }
}
