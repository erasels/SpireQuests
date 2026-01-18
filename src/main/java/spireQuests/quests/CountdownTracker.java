package spireQuests.quests;


import spireQuests.Anniv8Mod;

import java.util.function.Function;

//made a modified version of trigger tracker, starts at a value and decreases by 1 on trigger, at 0 is failed
public class CountdownTracker<T> extends AbstractQuest.Tracker {
    protected final int limit;
    private Function<T, Boolean> triggerCondition = null;

    protected int count;

    public CountdownTracker(Trigger<T> trigger, int limit) {
        this.count = limit;
        this.limit = limit;

        setTrigger(trigger, this::trigger);
    }

    public CountdownTracker<T> triggerCondition(Function<T, Boolean> condition) {
        this.triggerCondition = condition;
        return this;
    }

    public void trigger(T param) {
        if (count > 0 && (triggerCondition == null || triggerCondition.apply(param)))
            --count;
    }

    @Override
    protected void reset() {
        count = limit;
    }

    @Override
    public boolean isComplete() {
        return !isFailed();
    }

    @Override
    public boolean isFailed() {
        return count <= 0;
    }

    @Override
    public String progressString() {
        return String.format(" (%d/%d)", count, limit);
    }

    @Override
    public String saveData() {
        return String.valueOf(count);
    }

    @Override
    public void loadData(String data) {
        try {
            count = Integer.parseInt(data);
        } catch (Exception e) {
            Anniv8Mod.logger.error("Failed to load tracker data for '" + text + "'", e);
        }
    }
}