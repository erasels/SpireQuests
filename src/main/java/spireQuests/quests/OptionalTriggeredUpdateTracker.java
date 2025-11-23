package spireQuests.quests;

import spireQuests.quests.AbstractQuest.TriggeredUpdateTracker;

import java.util.function.Supplier;

/**
 * A TriggeredUpdateTracker that doesn't track progress and is always considered complete to track certain parameters during quests.
 * @param <T>
 */
public class OptionalTriggeredUpdateTracker<T, U> extends TriggeredUpdateTracker<T, U> {

    boolean hideTarget = true;

    public OptionalTriggeredUpdateTracker(Trigger<U> trigger, T start, T target, Supplier<T> getProgress) {
        super(trigger, start, target, getProgress);
    }

    public OptionalTriggeredUpdateTracker(Trigger<U> trigger, T start, T target, boolean hide, Supplier<T> getProgress) {
        super(trigger, start, target, getProgress);

        hideTarget = hide;
    }

    public OptionalTriggeredUpdateTracker(Trigger<U> trigger, T start, Supplier<T> getProgress) {
        super(trigger, start, null, getProgress);
    }

    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public String progressString() {

        if (!hideTarget) return super.progressString();

        return String.format(" (%s)", state);
    }
}
