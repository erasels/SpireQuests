package spireQuests.quests;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.UIStrings;
import spireQuests.Anniv8Mod;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static spireQuests.Anniv8Mod.makeID;

public abstract class AbstractQuest implements Comparable<AbstractQuest> {
    private static final String[] TEXT = CardCrawlGame.languagePack.getUIString(makeID("AbstractQuest")).TEXT;

    public enum QuestType {
        SHORT,
        LONG
    }

    public enum QuestDifficulty {
        EASY,
        NORMAL,
        HARD,
        CHALLENGE
    }

    public final String id;
    public final QuestType type;
    public final QuestDifficulty difficulty;

    protected final UIStrings localization;
    public String name;
    public String description;
    public String author;
    public float width = 0;

    public boolean useDefaultReward;
    public List<QuestReward> questRewards;

    private int trackerTextIndex = 0;

    public List<Tracker> trackers;
    protected List<Consumer<Trigger<?>>> triggers;
    private boolean complete;
    private boolean failed;

    private static final int HP_COST_MIN_RANGE = 3;
    private static final int HP_COST_MAX_RANGE = 6;
    private static final int GOLD_COST_MIN_RANGE = 15;
    private static final int GOLD_COST_MAX_RANGE = 25;
    public int hpCost;
    public int goldCost;
    public boolean usingGoldCost;

    private ArrayList<PowerTip> previewTooltips;

    /*
    examples of how trackers would be added in the constructor of a quest
    addTracker(new PassiveTracker<>(() -> AbstractDungeon.player.currentHealth, 1));
    addTracker(new TriggerTracker<>(QuestTriggers.REMOVE_CARD, 1));
    addTracker(new TriggerTracker<AbstractCard>(QuestTriggers.ADD_CARD, 5) {
        @Override
        public void trigger(AbstractCard param) {
            if (param.rarity == AbstractCard.CardRarity.COMMON) {
                super.trigger(param);
            }
        }
    });*/
    /*
    trackers that require another tracker to be completed first
    idk which format i like better

    Tracker condition = addTracker(new TriggerTracker<>(QuestTriggers.ADD_CARD, 1).hide());
    condition = addTracker(new TriggerTracker<>(QuestTriggers.REMOVE_CARD, 1).after(condition));

    new TriggerTracker<>(QuestTriggers.ADD_CARD, 1).hide().add(this)
            .before(new TriggerTracker<>(QuestTriggers.REMOVE_CARD, 1)).add(this);*/

    public AbstractQuest(QuestType type, QuestDifficulty difficulty) {
        this.id = makeID(getClass().getSimpleName()); //makeID is used because the strings exist in UIStrings
        this.type = type;
        this.difficulty = difficulty;

        useDefaultReward = true;
        questRewards = new ArrayList<>();

        trackers = new ArrayList<>();
        triggers = new ArrayList<>();

        complete = false;

        localization = CardCrawlGame.languagePack.getUIString(id);
        if (localization == null) {
            throw new RuntimeException("Localization for the quest " + id + " not found!");
        }
        setText();
    }

    public void setCost() {
        this.hpCost = AbstractDungeon.miscRng.random(HP_COST_MIN_RANGE, HP_COST_MAX_RANGE);
        this.goldCost = AbstractDungeon.miscRng.random(GOLD_COST_MIN_RANGE, GOLD_COST_MAX_RANGE);

        // neow room quests only cost hp to prevent weird shit with buying quests with gold and then losing all your gold to neow
        if (AbstractDungeon.floorNum > 1) {
            this.usingGoldCost = AbstractDungeon.miscRng.randomBoolean();
        } else {
            this.usingGoldCost = false;
        }
    }

    public int getCost() {
        if (usingGoldCost) {
            return goldCost;
        } else {
            return hpCost;
        }
    }

    //override if you want to set up the text differently
    protected void setText() {
        name = localization.TEXT[0];
        description = localization.TEXT[1];
        author = localization.TEXT[2];
    }

    //override if you want to set up the text differently
    public String getDescription() {
        return description;
    }

    //override if you want to set up the text differently
    private String rewardsText = null;
    public String getRewardsText() {
        if (rewardsText == null) {
            StringBuilder sb = new StringBuilder();
            for (QuestReward reward : questRewards) {
                sb.append(reward.rewardText).append(" NL ");
            }
            rewardsText = sb.toString();
        }
        return rewardsText;
    }

    public final ArrayList<PowerTip> getPreviewTips() {
        if (previewTooltips == null) {
            previewTooltips = new ArrayList<>();
            makeTooltips(previewTooltips);
        }

        return previewTooltips;
    }

    /**
     * To add custom tips to a quest, override this method.
     * @param tipList
     */
    public void makeTooltips(List<PowerTip> tipList) {
        tipList.clear();
        for (QuestReward reward : questRewards) {
            reward.addTooltip(tipList);
        }
    }

    /**
     * Adds an objective tracker to a quest. Should be used in the constructor. Can also call Tracker.add
     * @param questTracker
     * @return
     */
    protected final Tracker addTracker(Tracker questTracker) {
        trackers.add(questTracker);

        if (!questTracker.hidden) {
            if (trackerTextIndex >= localization.EXTRA_TEXT.length) {
                throw new RuntimeException("Quest " + id + " needs more entries in EXTRA_TEXT for its trackers");
            }
            questTracker.text = localization.EXTRA_TEXT[trackerTextIndex];
            ++trackerTextIndex;
        }

        if (questTracker.trigger != null) triggers.add(questTracker.trigger);
        if (questTracker.reset != null) triggers.add(questTracker.reset);

        return questTracker;
    }

    protected final AbstractQuest addReward(QuestReward reward) {
        useDefaultReward = false;

        questRewards.add(reward);

        return this;
    }

    public boolean complete() {
        if (failed) return false;
        if (complete) return true;

        for (Tracker tracker : trackers) {
            if (!tracker.isComplete()) return false;
        }
        complete = true;
        trackers.clear();
        triggers.clear();
        trackers.add(new QuestCompleteTracker());
        return true;
    }

    public boolean fail() {
        if(complete) return false;
        if(failed) return true;

        boolean notFailed = true;
        for (Tracker tracker : trackers) {
            if (tracker.isFailed()) {
                notFailed = false;
                break;
            }
        }
        if (notFailed) return false;

        forceFail();
        return true;
    }

    public void forceFail() {
        if(complete) Anniv8Mod.logger.warn("Forcefully failed quest that was complete {}", this.id);

        failed = true;
        trackers.clear();
        triggers.clear();
        trackers.add(new QuestFailedTracker());
    }

    public final void obtainRewards() {
        onComplete();
        if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.COMBAT_REWARD) {
            for (QuestReward reward : questRewards) {
                reward.obtainRewardItem();
            }
        }
        else {
            for (QuestReward reward : questRewards) {
                reward.obtainInstant();
            }
        }
    }

    public void update() {

    }

    public void onStart() {

    }

    public void onComplete() {
        //How should quest rewards be handled? Should they be immediate?
        //At the end of the room?
        //most likely when they are in a complete state, they can be clicked to claim the reward?
    }

    public void onFail() {
    }

    public void triggerTrackers(Trigger<?> trigger) {
        for (Consumer<Trigger<?>> triggerMethod : triggers) {
            triggerMethod.accept(trigger);
        }
    }

    public void refreshState() {
        for (Tracker t : trackers) {
            t.refreshState();
        }
    }

    public void loadSave(String[] questData) {
        if (questData.length == 1) {
            if(QuestCompleteTracker.COMPLETE_STRING.equals(questData[0])) {
                complete = true;
                trackers.clear();
                triggers.clear();
                trackers.add(new QuestCompleteTracker());
                return;
            } else if (QuestFailedTracker.FAIL_STRING.equals(questData[0])) {
                failed = true;
                trackers.clear();
                triggers.clear();
                trackers.add(new QuestFailedTracker());
            }
        }

        for (int i = 0; i < questData.length; ++i) {
            if (i >= trackers.size()) {
                Anniv8Mod.logger.warn("Saved tracker data for quest " + id + " does not match tracker count");
            }
            trackers.get(i).loadData(questData[i]);
        }
    }

    public String[] trackerSaves() {
        String[] data = new String[trackers.size()];
        for (int i = 0; i < data.length; ++i) {
            data[i] = trackers.get(i).saveData();
        }
        return data;
    }
    public QuestReward.QuestRewardSave[] rewardSaves() {
        QuestReward.QuestRewardSave[] rewardSaves = new QuestReward.QuestRewardSave[questRewards.size()];
        for (int i = 0; i < rewardSaves.length; ++i) {
            rewardSaves[i] = questRewards.get(i).getSave();
        }
        return rewardSaves;
    }

    //instances of quests are registered, makeCopy is used to get the one to provide to the player
    public AbstractQuest makeCopy() {
        AbstractQuest quest;
        try {
            quest = this.getClass().getDeclaredConstructor().newInstance();
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException("Failed to auto-generate makeCopy for quest " + getClass().getName(), e);
        }

        if (quest.useDefaultReward) {
            //TODO: default reward roll
        }
        return quest;
    }

    @Override
    public int compareTo(AbstractQuest o) {
        int compare = type.compareTo(o.type);
        if (compare != 0) return compare;

        compare = difficulty.compareTo(o.difficulty);
        if (compare != 0) return compare;

        return name.compareTo(o.name);
    }


    //chained method to make a sequential tracker? Each part of a sequential tracker should still be displayed if not explicitly hidden?
    //Also a "reset" condition? or some other way to make a tracker quickly reset?
    //maybe a getResetTrigger that defaults to null
    //I don't like this. Make a generic method that gets called to add a reset trigger instead?
    //Probably swap Tracker to an actual class, no reason for it to be an interface at this point.
    //also add a method hide() which will make a tracker invisible for something like part of a sequential tracker

    public abstract static class Tracker {
        public String text;
        protected boolean hidden = false;
        protected Supplier<Boolean> condition = null;
        protected Consumer<Trigger<?>> trigger = null;
        protected Consumer<Trigger<?>> reset = null;

        public abstract boolean isComplete();
        public boolean isFailed() {
            return false;
        }

        public boolean hidden() {
            return hidden;
        }

        public final void addCondition(Supplier<Boolean> condition) {
            if (this.condition != null) {
                Supplier<Boolean> oldCondition = this.condition;
                this.condition = () -> oldCondition.get() && condition.get();
            }
            else {
                this.condition = condition;
            }
        }

        /**
         * Causes a tracker to not be displayed. This should be done for a subcondition, like "be in a shop" before "obtain x cards"
         */
        public final Tracker hide() {
            this.hidden = true;
            return this;
        }

        public final <A> void setTrigger(Trigger<A> trigger, Consumer<A> onTrigger) {
            this.trigger = trigger.getTriggerMethod((param) -> {
                if (Tracker.this.condition == null || Tracker.this.condition.get()) onTrigger.accept(param);
            });
        }

        /**
         * Sets a trigger that will reset this tracker's progress. No effect by default on passive trackers, but the reset method can be overridden.
         * @param trigger
         */
        public final <A> Tracker setResetTrigger(Trigger<A> trigger) {
            return setResetTrigger(trigger, (param) -> true);
        }

        /**
         * Sets a trigger that will reset this tracker's progress. No effect by default on passive trackers, but the reset method can be overridden.
         * @param trigger
         * @param condition Receives the trigger parameter and only resets the tracker if true is returned and the trigger is incomplete.
         */
        public final <A> Tracker setResetTrigger(Trigger<A> trigger, Function<A, Boolean> condition) {
            return setResetTrigger(trigger, condition, true);
        }

        /**
         * Sets a trigger that will reset this tracker's progress. No effect by default on passive trackers, but the reset method can be overridden.
         * @param trigger
         * @param condition Receives the trigger parameter and only resets the tracker if true is returned.
         * @param lockCompletion If true, the reset trigger will be ignored once this tracker is completed.
         */
        public final <A> Tracker setResetTrigger(Trigger<A> trigger, Function<A, Boolean> condition, boolean lockCompletion) {
            this.reset = trigger.getTriggerMethod((param)->{
                if (lockCompletion && this.isComplete()) return;
                if (condition.apply(param)) {
                    this.reset();
                }
            });
            return this;
        }

        protected void reset() {

        }


        public abstract String progressString();

        @Override
        public String toString() {
            return text + progressString();
        }

        /**
         * Add a condition for the provided tracker to be complete before this tracker begins to function.
         * @param other
         * @return
         */
        protected final Tracker after(Tracker other) {
            addCondition(other::isComplete);
            return other;
        }

        /**
         * Add a condition for this tracker to be complete before the provided tracker begins to function.
         * @param other
         * @return
         */
        protected final Tracker before(Tracker other) {
            other.addCondition(this::isComplete);
            return other;
        }

        public final Tracker add(AbstractQuest quest) {
            return quest.addTracker(this);
        }

        public String saveData() {
            return null;
        }

        /**
         * Called upon loading save, to ensure quest displays an accurate state
         */
        public void refreshState() {

        }

        public void loadData(String data) {
        }
    }

    /**
     * A tracker checking an easily accessible value and comparing it against a target value.
     * @param <T>
     */
    public static class PassiveTracker<T> extends Tracker {
        private final Supplier<T> progress;
        private final T target;
        private final BiFunction<T, T, Boolean> comparer;
        private final Supplier<Boolean> isFailed;

        public PassiveTracker(Supplier<T> getProgress, T target) {
            this(getProgress, target, Object::equals, ()->false);
        }

        public PassiveTracker(Supplier<T> getProgress, T target, BiFunction<T, T, Boolean> comparer) {
            this(getProgress, target, comparer, ()->false);
        }

        public PassiveTracker(Supplier<T> getProgress, T target, Supplier<Boolean> isFailed) {
            this(getProgress, target, Object::equals, isFailed);
        }

        public PassiveTracker(Supplier<T> getProgress, T target, BiFunction<T, T, Boolean> comparer, Supplier<Boolean> isFailed) {
            this.progress = getProgress;
            this.target = target;
            this.comparer = comparer;
            this.isFailed = isFailed;
        }

        //Resetting does nothing for a passive tracker, but you could override it to do something.

        @Override
        public boolean isComplete() {
            return (condition == null || condition.get()) && comparer.apply(progress.get(), target) && !isFailed();
        }

        @Override
        public boolean isFailed() {
            return isFailed.get();
        }

        @Override
        public String progressString() {
            return String.format(" (%s/%s)", progress.get(), target);
        }
    }

    /**
     * A tracker that requires a trigger to occur a certain number of times.
     * Adding a condition causes the tracker to not being tracking until it is fulfilled.
     * A reset trigger can be added to set the count back to 0
     */
    public static class TriggerTracker<T> extends Tracker {
        private final int targetCount;
        private Function<T, Boolean> triggerCondition = null;
        private final Supplier<Boolean> isFailed;

        private int count;

        public TriggerTracker(Trigger<T> trigger, int count) {
            this(trigger, count, ()->false);
        }
        public TriggerTracker(Trigger<T> trigger, int count, Supplier<Boolean> isFailed) {
            this.count = 0;
            this.targetCount = count;
            this.isFailed = isFailed;

            setTrigger(trigger, this::trigger);
        }

        public TriggerTracker<T> triggerCondition(Function<T, Boolean> condition) {
            this.triggerCondition = condition;
            return this;
        }

        public void trigger(T param) {
            if (triggerCondition == null || triggerCondition.apply(param))
                ++count;
        }

        @Override
        protected void reset() {
            count = 0;
        }

        @Override
        public boolean isComplete() {
            return count >= targetCount && !isFailed();
        }

        @Override
        public boolean isFailed() {
            return isFailed.get();
        }

        @Override
        public String progressString() {
            return String.format(" (%d/%d)", count, targetCount);
        }

        @Override
        public String saveData() {
            return String.valueOf(count);
        }

        @Override
        public void loadData(String data) {
            try {
                count = Integer.parseInt(data);
            }
            catch (Exception e) {
                Anniv8Mod.logger.error("Failed to load tracker data for '" + text + "'", e);
            }
        }
    }

    /**
     * A tracker checking a value only when a specific trigger occurs (recommended for stuff that could potentially be costly if checked every frame to display quest state and has a clear update event)
     * @param <T>
     */
    public static class TriggeredUpdateTracker<T, U> extends Tracker {
        protected T start, state, target;
        private final Supplier<T> getState;
        private final Supplier<Boolean> isFailed;

        public TriggeredUpdateTracker(Trigger<U> trigger, T start, T target,  Supplier<T> getProgress) {
            this(trigger, start, target, getProgress, ()->false);
        }

        public TriggeredUpdateTracker(Trigger<U> trigger, T start, T target, Supplier<T> getState, Supplier<Boolean> isFailed) {
            this.start = start;
            this.state = start;
            this.target = target;
            this.getState = getState;
            this.isFailed = isFailed;

            setTrigger(trigger, this::trigger);
        }

        public void trigger(U param) {
            refreshState();
        }

        @Override
        protected void reset() {
            this.state = start;
        }

        @Override
        public boolean isComplete() {
            return (condition == null || condition.get()) && target.equals(state);
        }

        @Override
        public boolean isFailed() {
            return isFailed.get();
        }

        @Override
        public String progressString() {
            return String.format(" (%s/%s)", state, target);
        }

        @Override
        public String saveData() {
            return String.valueOf(state);
        }

        @Override
        public void refreshState() {
            state = getState.get();
        }
    }

    /**
     * A tracker used to mark a quest as completed to avoid having the state change afterward
     */
    private static class QuestCompleteTracker extends Tracker {
        public static final String COMPLETE_STRING = "COMPLETE";

        public QuestCompleteTracker() {

        }

        @Override
        public boolean isComplete() {
            return true;
        }

        @Override
        public String progressString() {
            return TEXT[0];
        }

        @Override
        public String toString() {
            return TEXT[0];
        }

        @Override
        public String saveData() {
            return COMPLETE_STRING;
        }
    }

    /**
     * A tracker used to mark a quest as completed to avoid having the state change afterward
     */
    private static class QuestFailedTracker extends Tracker {
        public static final String FAIL_STRING = "FAILED";

        public QuestFailedTracker() {

        }

        @Override
        public boolean isComplete() {
            return false;
        }

        @Override
        public boolean isFailed() {
            return true;
        }

        @Override
        public String progressString() {
            return TEXT[1];
        }

        @Override
        public String toString() {
            return TEXT[1];
        }

        @Override
        public String saveData() {
            return FAIL_STRING;
        }
    }

    /**
     * A tracker that defaults to a hidden complete state, used to run code when a trigger occurs.
     */
    public static class TriggerEvent<T> extends Tracker {
        private int triggerCount;

        public TriggerEvent(Trigger<T> trigger, Consumer<T> onTrigger) {
            this(trigger, onTrigger, -1);
        }

        /**
         * Runs code when a trigger occurs. If trigger count is omitted it will trigger any number of times.
         * @param trigger
         * @param onTrigger
         * @param triggerCount
         */
        public TriggerEvent(Trigger<T> trigger, Consumer<T> onTrigger, int triggerCount) {
            this.triggerCount = triggerCount;
            setTrigger(trigger, (param)->{
                if (this.triggerCount != 0) {
                    --this.triggerCount; //theoretically this limits the triggers of "infinite" to like 2 billion
                    onTrigger.accept(param);
                }
            });
            hide();
        }

        @Override
        public boolean isComplete() {
            return true;
        }

        @Override
        public String progressString() {
            return "";
        }
    }
}
