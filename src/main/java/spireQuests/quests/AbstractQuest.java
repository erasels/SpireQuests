package spireQuests.quests;

import basemod.helpers.CardPowerTip;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.potions.AbstractPotion.PotionRarity;
import com.megacrit.cardcrawl.relics.AbstractRelic.RelicTier;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import javassist.CtBehavior;
import spireQuests.Anniv8Mod;
import spireQuests.patches.ShowMarkedNodesOnMapPatch;
import spireQuests.questStats.StatRewardBox;
import spireQuests.util.QuestStrings;
import spireQuests.util.QuestStringsUtils;
import spireQuests.util.WeightedList;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static spireQuests.Anniv8Mod.makeID;

public abstract class AbstractQuest implements Comparable<AbstractQuest> {
    private static final String[] TEXT = CardCrawlGame.languagePack.getUIString(makeID("AbstractQuest")).TEXT;

    public static Random rng;

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

    protected final QuestStrings questStrings;
    public String name;
    public String description;
    public String author;
    public float width = 0;
    protected float titleScale = 1.2f; // change as needed for longer titles
    public boolean needHoverTip = false;

    public boolean useDefaultReward;
    public List<QuestReward> questRewards;
    public boolean rewardScreenOnly = false;

    public boolean isAbandoning = false;

    protected int trackerTextIndex = 0;

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

    //If true, the quest will automatically complete when the player leaves the room with the conditions fulfilled.
    public boolean isAutoComplete;

    //If true, the quest will automatically fail when the player leaves the room with the fail conditions fulfilled.
    public boolean isAutoFail;
    /*
    trackers that require another tracker to be completed first

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
        isAutoComplete = false;
        isAutoFail = false;

        questStrings = QuestStringsUtils.getQuestString(id);
        if (questStrings == null) {
            throw new RuntimeException("Queststrings for the quest " + id + " not found!");
        }
        setText();
    }

    public void setCost() {
        this.hpCost = AbstractQuest.rng.random(HP_COST_MIN_RANGE, HP_COST_MAX_RANGE);
        this.goldCost = AbstractQuest.rng.random(GOLD_COST_MIN_RANGE, GOLD_COST_MAX_RANGE);

        // neow room quests only cost hp to prevent weird shit with buying quests with gold and then losing all your gold to neow
        if (AbstractDungeon.floorNum > 1) {
            this.usingGoldCost = AbstractQuest.rng.randomBoolean();
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

    public float getTitleScale() {
        return titleScale;
    }

    //override if you want to set up the text differently
    protected void setText() {
        name = questStrings.TITLE;
        description = questStrings.DESCRIPTION;
        author = questStrings.AUTHOR;
        rewardsText = questStrings.REWARD; // questStrings.REWARD will be null and set later unless you provide it in the json
    }

    //override if you want to set up the text differently
    public String getDescription() {
        return description;
    }

    //override if you want to set up the text differently
    protected String rewardsText = null;

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
     *
     * @param tipList
     */
    public void makeTooltips(List<PowerTip> tipList) {
        tipList.clear();
        for (QuestReward reward : questRewards) {
            reward.addTooltip(tipList);
        }

        boolean hasQuestboundItems = false;

        if(questboundRelics != null){
            hasQuestboundItems = true;

            for (AbstractRelic r : questboundRelics){
                tipList.add(new PowerTip(r.name, r.description));
            }
        }

        if(questboundCards != null){
            hasQuestboundItems = true;

            for (AbstractCard c : questboundCards){
                tipList.add(new CardPowerTip(c));
            }
        }

        if(hasQuestboundItems){
            tipList.add(new PowerTip(Anniv8Mod.keywords.get("Questbound").PROPER_NAME, Anniv8Mod.keywords.get("Questbound").DESCRIPTION));
        }
    }

    /**
     * This allows customizing the PowerTip that is shown if needsHoverTooltip is true and the quest is hovered in the UI
     *
     * @return PowerTip that will be displayed on hover
     */
    public PowerTip getHoverTooltip() {
        return new PowerTip(name, getDescription());
    }

    /**
     * Adds an objective tracker to a quest. Should be used in the constructor. Can also call Tracker.add
     *
     * @param questTracker
     * @return
     */
    protected final Tracker addTracker(Tracker questTracker) {
        trackers.add(questTracker);

        if (!questTracker.hidden) {
            assignTrackerText(questTracker);
        }

        if (questTracker.trigger != null) triggers.add(questTracker.trigger);
        if (questTracker.reset != null) triggers.addAll(questTracker.reset);
        if (questTracker.failTriggers != null) triggers.addAll(questTracker.failTriggers);

        return questTracker;
    }

    /**
     * Sets the text of the tracker, which by default tracks the index of each tracker and uses the TRACKER_TEXT entry
     * for that index. Override this for custom behavior.
     *
     * @param questTracker
     */
    protected void assignTrackerText(Tracker questTracker) {
        if (trackerTextIndex >= questStrings.TRACKER_TEXT.length) {
            throw new RuntimeException("Quest " + id + " needs more entries in TRACKER_TEXT for its trackers");
        }

        questTracker.text = questStrings.TRACKER_TEXT[trackerTextIndex];
        trackerTextIndex++;
    }

    protected final AbstractQuest addReward(QuestReward reward) {
        useDefaultReward = false;

        questRewards.add(reward);

        return this;
    }

    protected final AbstractQuest addGenericReward() {
        useDefaultReward = true;

        if (CardCrawlGame.isInARun()) {
            QuestReward reward = getGenericRewardWeightedList().getRandom(AbstractQuest.rng);
            questRewards.add(reward);
        }
        this.rewardsText = getRewardsText();

        return this;
    }
    
    private WeightedList<QuestReward> getGenericRewardWeightedList() {
        WeightedList<QuestReward> rewards = new WeightedList<>();
        
        // Gold rewards rounded to 5s.
        switch (this.difficulty) {
            default:
            case EASY:
                rewards.add(new QuestReward.GoldReward(((AbstractQuest.rng.random(50, 70) + 2) / 5) * 5), 3);
                rewards.add(new QuestReward.PotionReward(AbstractDungeon.returnRandomPotion(PotionRarity.COMMON, true)), 2);
                rewards.add(new QuestReward.MaxHPReward(AbstractQuest.rng.random(5, 7)), 2);
                break;
            case NORMAL:
                rewards.add(new QuestReward.GoldReward(((AbstractQuest.rng.random(90, 120) + 2) / 5) * 5), 4);
                rewards.add(new QuestReward.PotionReward(AbstractDungeon.returnRandomPotion(PotionRarity.UNCOMMON, true)), 3);
                rewards.add(new QuestReward.MaxHPReward(AbstractQuest.rng.random(8, 10)), 2);
                break;
            case HARD:
                rewards.add(new QuestReward.GoldReward(((AbstractQuest.rng.random(140, 180) + 2) / 5) * 5), 3);
                rewards.add(new QuestReward.RandomRelicReward(RelicTier.COMMON), 2);
                rewards.add(new QuestReward.RandomRelicReward(), 1);
                rewards.add(new QuestReward.MaxHPReward(AbstractQuest.rng.random(12, 14)), 2);
                break;
        }
        
        return rewards;
    }

    public boolean complete() {
        if (failed) return false;
        if (complete) {
            return true;
        }



        if(!questConditionsAreFulfilled()) return false;



        complete = true;
        trackers.clear();
        triggers.clear();
        trackers.add(new QuestCompleteTracker());
        completeSFX();
        return true;
    }

    protected boolean questConditionsAreFulfilled(){
        for (Tracker tracker : trackers) {
            if (!tracker.isComplete()) return false;
        }
        return true;
    }

    public boolean fail() {
        if (complete) return false;
        if (failed) return true;

        boolean notFailed = true;
        for (Tracker tracker : trackers) {
            if (tracker.isFailed()) {
                notFailed = false;
                break;
            }
        }
        if (notFailed) return false;

        forceFail();
        failSFX();
        return true;
    }

    public void forceFail() {
        if (complete) Anniv8Mod.logger.warn("Forcefully failed quest that was complete {}", this.id);

        failed = true;
        trackers.clear();
        triggers.clear();
        trackers.add(new QuestFailedTracker());
    }

    public void forceComplete() {
        if (failed) Anniv8Mod.logger.warn("Forcefully completed quest that was failed {}", this.id);

        complete = true;
        trackers.clear();
        triggers.clear();
        trackers.add(new QuestCompleteTracker());
    }

    //override if you want different completion SFX.
    public void completeSFX() {
        CardCrawlGame.sound.play("UNLOCK_PING");
    }

    //override if you want different failure SFX.
    public void failSFX() {
        CardCrawlGame.sound.play("DEATH_STINGER");
    }

    public boolean isCompleted() {
        return this.complete();
    }

    public boolean isFailed() {
        return this.fail();
    }

    public final void obtainRewards() {
        onComplete();
        if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.COMBAT_REWARD) {
            for (QuestReward reward : questRewards) {
                reward.obtainRewardItem();
            }
        } else {
            for (QuestReward reward : questRewards) {
                reward.obtainInstant();
            }
        }
    }

    public void update() {

    }

    public void onStart() {
        for (Tracker t : trackers) {
            t.refreshState();
        }
        for (QuestReward r : questRewards) {
            r.init();
        }
        if(this instanceof MarkNodeQuest) {
            MarkNodeQuest q = (MarkNodeQuest) this;
            q.markNodes(AbstractDungeon.map, q.rng());
        }
    }

    public void onComplete() {
        if(this instanceof MarkNodeQuest) {
            ShowMarkedNodesOnMapPatch.ImageField.ClearMarks(id);
        }
    }

    public void onFail() {
        if(this instanceof MarkNodeQuest) {
            ShowMarkedNodesOnMapPatch.ImageField.ClearMarks(id);
        }
    }

    public boolean canSpawn() {
        return true;
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

    // Most quests can have these dynamically generated using the code below, however if your
    // quest has a special reward structure or dynamic rewards, you may need to ovveride
    // this function and manually define how Quest Log displays rewards.
    public ArrayList<StatRewardBox> getStatRewardBoxes() {
        ArrayList<StatRewardBox> ret = new ArrayList<>();

        if (this.questRewards.isEmpty() || this.useDefaultReward) {
            ret.add(new StatRewardBox(this));
        } else {
            for (QuestReward r : this.questRewards) {
                ret.add(new StatRewardBox(r));
            }
        }

        return ret;
    }

    public void loadSave(String[] questData, QuestReward.QuestRewardSave[] questRewardSaves) {
        boolean loadTrackers = true;
        if (questData.length == 1) {
            if (QuestCompleteTracker.COMPLETE_STRING.equals(questData[0])) {
                complete = true;
                trackers.clear();
                triggers.clear();
                trackers.add(new QuestCompleteTracker());
                loadTrackers = false;
            } else if (QuestFailedTracker.FAIL_STRING.equals(questData[0])) {
                failed = true;
                trackers.clear();
                triggers.clear();
                trackers.add(new QuestFailedTracker());
                loadTrackers = false;
            }
        }

        if (loadTrackers) {
            for (int i = 0; i < questData.length; ++i) {
                if (i >= trackers.size()) {
                    Anniv8Mod.logger.warn("Saved tracker data for quest " + id + " does not match tracker count");
                }
                trackers.get(i).loadData(questData[i]);
            }
        }

        questRewards.clear();
        for (QuestReward.QuestRewardSave qrs : questRewardSaves) {
            questRewards.add(QuestReward.fromSave(qrs));
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
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new RuntimeException("Failed to auto-generate makeCopy for quest " + getClass().getName(), e);
        }

        if (quest.useDefaultReward) {
            //TODO: default reward roll
            //(Will add when I make quests without custom rewards)
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

    public abstract static class Tracker {
        public String text;
        protected Supplier<Boolean> isFailed = () -> false;
        protected boolean hidden = false;
        protected Supplier<Boolean> condition = null;
        protected Consumer<Trigger<?>> trigger = null;
        protected ArrayList<Consumer<Trigger<?>>> reset = new ArrayList<>();
        protected ArrayList<Consumer<Trigger<?>>> failTriggers = new ArrayList<>();

        public abstract boolean isComplete();

        public boolean isFailed() {
            return isFailed.get();
        }

        public boolean isDisabled() {
            return false;
        }

        public boolean hidden() {
            return hidden;
        }

        public final void addCondition(Supplier<Boolean> condition) {
            if (this.condition != null) {
                Supplier<Boolean> oldCondition = this.condition;
                this.condition = () -> oldCondition.get() && condition.get();
            } else {
                this.condition = condition;
            }
        }

        /**
         * Causes a tracker to not be displayed. This should be done for a subcondition, like "be in a shop" before "buy x cards"
         */
        public final Tracker hide() {
            this.hidden = true;
            return this;
        }

        /**
         * Shows a hidden tracker. This should be called once a condition has been fulfilled, like showing "buy x cards" after achieving "be in a shop"
         */
        public final Tracker show() {
            this.hidden = false;
            return this;
        }

        public final <A> void setTrigger(Trigger<A> trigger, Consumer<A> onTrigger) {
            this.trigger = trigger.getTriggerMethod((param) -> {
                if (Tracker.this.condition == null || Tracker.this.condition.get()) onTrigger.accept(param);
            });
        }

        /**
         * Sets a trigger that will fail (the quest) when the condition is met.
         *
         * @param trigger
         */
        public final <A> Tracker setFailureTrigger(Trigger<A> trigger) {
            return setFailureTrigger(trigger, (param) -> true);
        }

        /**
         * Sets a trigger that will fail (the quest) when the condition is met.
         *
         * @param trigger
         * @param condition Receives the trigger parameter and only fails the quest if true is returned.
         */
        public final <A> Tracker setFailureTrigger(Trigger<A> trigger, Function<A, Boolean> condition) {
            this.failTriggers.add(trigger.getTriggerMethod((param) -> {
                if (this.isComplete()) return;
                if (condition.apply(param)) {
                    isFailed = () -> true;
                }
            }));
            return this;
        }

        /**
         * Sets a trigger that will reset this tracker's progress. No effect by default on passive trackers, but the reset method can be overridden.
         *
         * @param trigger
         */
        public final <A> Tracker setResetTrigger(Trigger<A> trigger) {
            return setResetTrigger(trigger, (param) -> true);
        }

        /**
         * Sets a trigger that will reset this tracker's progress. No effect by default on passive trackers, but the reset method can be overridden.
         *
         * @param trigger
         * @param condition Receives the trigger parameter and only resets the tracker if true is returned and the trigger is incomplete.
         */
        public final <A> Tracker setResetTrigger(Trigger<A> trigger, Function<A, Boolean> condition) {
            return setResetTrigger(trigger, condition, true);
        }

        /**
         * Sets a trigger that will reset this tracker's progress. No effect by default on passive trackers, but the reset method can be overridden.
         *
         * @param trigger
         * @param condition      Receives the trigger parameter and only resets the tracker if true is returned.
         * @param lockCompletion If true, the reset trigger will be ignored once this tracker is completed.
         */
        public final <A> Tracker setResetTrigger(Trigger<A> trigger, Function<A, Boolean> condition, boolean lockCompletion) {
            this.reset.add(trigger.getTriggerMethod((param) -> {
                if (lockCompletion && this.isComplete()) return;
                if (condition.apply(param)) {
                    this.reset();
                }
            }));
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
         *
         * @param other
         * @return
         */
        protected final Tracker after(Tracker other) {
            addCondition(other::isComplete);
            return other;
        }

        /**
         * Add a condition for this tracker to be complete before the provided tracker begins to function.
         *
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
         * Called upon starting quest or loading save, to ensure quest displays an accurate state
         */
        public void refreshState() {

        }

        public void loadData(String data) {
        }
    }

    /**
     * A tracker checking an easily accessible value and comparing it against a target value.
     *
     * @param <T>
     */
    public static class PassiveTracker<T> extends Tracker {
        private final Supplier<T> progress;
        private final T target;
        private final BiFunction<T, T, Boolean> comparer;

        public PassiveTracker(Supplier<T> getProgress, T target) {
            this(getProgress, target, Object::equals, () -> false);
        }

        public PassiveTracker(Supplier<T> getProgress, T target, BiFunction<T, T, Boolean> comparer) {
            this(getProgress, target, comparer, () -> false);
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
        protected final int targetCount;
        private Function<T, Boolean> triggerCondition = null;

        protected int count;

        public TriggerTracker(Trigger<T> trigger, int count) {
            this(trigger, count, () -> false);
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
            } catch (Exception e) {
                Anniv8Mod.logger.error("Failed to load tracker data for '" + text + "'", e);
            }
        }
    }

    /**
     * A tracker checking a value only when a specific trigger occurs (recommended for stuff that could potentially be costly if checked every frame to display quest state and has a clear update event)
     *
     * @param <T>
     */
    public static class TriggeredUpdateTracker<T, U> extends Tracker {
        private static final Map<Class<?>, Function<String, Object>> loaders = new HashMap<>();
        static {
            loaders.put(String.class, (s)->s);
            loaders.put(Integer.class, Integer::parseInt);
        }

        protected T start, state, target;
        private final BiFunction<U, T, T> getState;
        private boolean autoRefresh;
        private final Supplier<Boolean> isFailed;

        public TriggeredUpdateTracker(Trigger<U> trigger, T start, T target, Supplier<T> getProgress) {
            this(trigger, start, target, getProgress, () -> false);
        }

        public TriggeredUpdateTracker(Trigger<U> trigger, T start, T target, Supplier<T> getState, Supplier<Boolean> isFailed) {
            this(trigger, start, target, (triggerParam, state) -> getState.get(), isFailed);
            autoRefresh = true;
        }

        public TriggeredUpdateTracker(Trigger<U> trigger, T start, T target, BiFunction<U, T, T> getState, Supplier<Boolean> isFailed) {
            this.start = start;
            this.state = start;
            this.target = target;
            this.getState = getState;
            this.isFailed = isFailed;
            autoRefresh = false;

            setTrigger(trigger, this::trigger);
        }

        public void trigger(U param) {
            state = getState.apply(param, state);
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

        @SuppressWarnings("unchecked")
        @Override
        public void loadData(String data) {
            Function<String, Object> loader = loaders.get(state.getClass());
            if (loader != null && data != null) {
                try {
                    state = (T) loader.apply(data);
                }
                catch (Exception e) {
                    Anniv8Mod.logger.warn("Exception occurred loading saved tracker data \"" + data + "\"", e);
                }
            }
        }

        @Override
        public void refreshState() {
            if (autoRefresh) {
                state = getState.apply(null, state);
            }
        }
    }

    /**
     * A tracker used to mark a quest as completed to avoid having the state change afterward
     */
    private class QuestCompleteTracker extends Tracker {
        public static final String COMPLETE_STRING = "COMPLETE";

        public QuestCompleteTracker() {

        }

        @Override
        public boolean isComplete() {
            return true;
        }

        @Override
        public String progressString() {
            return TEXT[rewardScreenOnly?1:0];
        }

        @Override
        public String toString() {
            return TEXT[rewardScreenOnly?1:0];
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
            return TEXT[2];
        }

        @Override
        public String toString() {
            return TEXT[2];
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
         *
         * @param trigger
         * @param onTrigger
         * @param triggerCount
         */
        public TriggerEvent(Trigger<T> trigger, Consumer<T> onTrigger, int triggerCount) {
            this.triggerCount = triggerCount;
            setTrigger(trigger, (param) -> {
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


    // Creates Questbound cards that are handled automatically. Just need an array and add cards to it.
    public ArrayList<AbstractCard> questboundCards;

    // This is for situations where the Questbound cards in the deck would be replaced. Basically if they're a "Random Attack" or something similar.
    public ArrayList<AbstractCard> overrideQuestboundCards() {
        return null;
    }

    // Similar to Questbound cards, but for Relics!
    public ArrayList<AbstractRelic> questboundRelics;
    // Setting removeQBDup to true will make it remove from pools when obtained.
    // Setting returnQPRelics to true adds them to the pool again once the Quest is complete. (Ignored if the first boolean is set to false)

    public boolean removeQuestboundDuplicate = true;
    public boolean returnQuestboundRelics = true;
    @SpirePatch(clz = AbstractDungeon.class, method = "nextRoomTransition", paramtypez = {SaveFile.class})
    public static class AutoCompleteQuestLater {
        @SpireInsertPatch(locator = Locator.class)
        public static void enteringRoomPatch(AbstractDungeon __instance, SaveFile file) {
            if (AbstractDungeon.currMapNode != null) {
                QuestManager.quests().stream()
                        .filter(quest -> quest.isAutoComplete && quest.isCompleted())
                        .collect(Collectors.toList())
                        .forEach(QuestManager::completeQuest);
                QuestManager.quests().stream()
                        .filter(quest -> quest.isAutoFail && quest.isFailed())
                        .collect(Collectors.toList())
                        .forEach(QuestManager::failQuest);
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "relics");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
