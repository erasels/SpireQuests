package spireQuests.quests.modargo;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.*;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import javassist.CtBehavior;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.modargo.cards.embracethecurse.*;
import spireQuests.quests.modargo.relics.TheLivingCurse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EmbraceTheCurseQuest extends AbstractQuest {
    public EmbraceTheCurseQuest() {
        super(QuestType.LONG, QuestDifficulty.CHALLENGE);

        // We wrap everything up in our own custom methods and classes in order to do things that the framework didn't
        // fully anticipate (specifically, to have a quest with rewards that you get at each milestone).
        // Equally important, this structure makes adjusting milestones easy to do without other code or text changes.
        this.createCurseTracker(1, new NetherStrike());
        this.createCurseTracker(3, new WickedExchange());
        this.createCurseTracker(5, new Curseblast());
        this.createCurseTracker(7, new SkullGaze());

        needHoverTip = true;
        isAutoComplete = true;
        titleScale = 0.9f;
    }

    @Override
    public String getDescription() {
        String milestones = this.trackers.stream().filter(t -> t instanceof AddCurseTracker).map(t -> ((AddCurseTracker)t).getTarget() + "").collect(Collectors.joining("/"));
        return String.format(super.getDescription(), milestones);
    }

    @Override
    protected void assignTrackerText(Tracker questTracker) {
        questTracker.text = questStrings.TRACKER_TEXT[0];
    }

    @Override
    public void onStart() {
        super.onStart();
        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0f, Settings.HEIGHT / 2.0f, new TheLivingCurse());
    }

    @Override
    public void onComplete() {
        super.onComplete();
        // The rewards are given out after each milestone, and should be all gone by this point, but just to be sure, clear them here
        questRewards.clear();
    }

    @Override
    public void makeTooltips(List<PowerTip> tipList) {
        super.makeTooltips(tipList);
        AbstractRelic relic = new TheLivingCurse();
        tipList.add(0, relic.tips.get(0));
    }

    private void createCurseTracker(int count, AbstractCard card) {
        new AddCurseTracker(count, this, card).add(this);
        this.addReward(new QuestReward.CardReward(card));
    }

    @Override
    public void loadSave(String[] questData, QuestReward.QuestRewardSave[] questRewardSaves) {
        super.loadSave(questData, questRewardSaves);
        for (Tracker tracker : this.trackers) {
            if (tracker.isComplete() && tracker instanceof AddCurseTracker) {
                ((AddCurseTracker)tracker).cleanup();
            }
        }
    }

    private static class CurseCardReward extends QuestReward.CardReward {
        public final int count;

        public CurseCardReward(AbstractCard card, int count) {
            super(card);
            this.count = count;
        }
    }

    private static class AddCurseTracker extends TriggerTracker<AbstractCard> {
        private final AbstractQuest quest;
        private final AbstractCard card;

        public AddCurseTracker(int count, AbstractQuest quest, AbstractCard card) {
            super(QuestTriggers.ADD_CARD, count);
            this.quest = quest;
            this.card = card;
            this.triggerCondition(c -> c.color == AbstractCard.CardColor.CURSE);
        }

        @Override
        public void trigger(AbstractCard param) {
            boolean wasComplete = this.isComplete();
            super.trigger(param);
            if (!wasComplete && this.isComplete()) {
                AbstractDungeon.topLevelEffectsQueue.add(new ShowCardAndObtainEffect(card, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                this.cleanup();
            }
        }

        public int getTarget() {
            return this.targetCount;
        }

        public void cleanup() {
            this.quest.questRewards.removeIf(r -> r instanceof CurseCardReward && ((CurseCardReward)r).count == this.targetCount);
            this.hide();
        }
    }

    private static AbstractCard getCurse(AbstractCard.CardRarity rarity) {
        // We only generate the normal base game curses because they have a really nice distribution of effects for the
        // purposes of this quest, and we don't want to mix in potentially weird modded curses.
        List<AbstractCard> mostlyHarmlessCurses = Arrays.asList(new Clumsy(), new Injury(), new Parasite(), new Writhe());
        List<AbstractCard> harmfulCurses = Arrays.asList(new Decay(), new Doubt(), new Normality(), new Pain(), new Regret(), new Shame());
        List<AbstractCard> specialQuestCards = AbstractDungeon.player.masterDeck.group.stream()
                .filter(c -> c instanceof EmbraceTheCurseCard)
                .collect(Collectors.groupingBy(c -> c.cardID))
                .values().stream()
                .map(cs -> cs.get(0).makeCopy())
                .collect(Collectors.toList());
        // It's always possible for previously obtained special cards from the quest to show up, with chances increasing
        // as more cards are acquired, with an extra bonus when the player has all four special cards (since that means
        // they've completed the quest, and more payoff for the curses they have is appropriate).
        // For curses, which ones can show up depends on the rarity of the card being replaced.
        List<AbstractCard> options = new ArrayList<>(specialQuestCards);
        if (AbstractQuest.rng.randomBoolean(specialQuestCards.size() / 4.0f)) {
            options.addAll(specialQuestCards);
        }
        if (specialQuestCards.size() == 4 && AbstractQuest.rng.randomBoolean()) {
            options.addAll(specialQuestCards);
        }
        switch (rarity) {
            case RARE:
                options.addAll(mostlyHarmlessCurses);
                break;
            case UNCOMMON:
                options.addAll(mostlyHarmlessCurses);
                if (AbstractQuest.rng.randomBoolean()) {
                    options.addAll(harmfulCurses);
                }
                break;
            default:
                options.addAll(mostlyHarmlessCurses);
                options.addAll(harmfulCurses);
                break;
        }
        return options.get(AbstractQuest.rng.random(options.size() - 1));
    }

    // We target this patch between the initial creation of the rewards list and the logic for upgrading cards in it
    @SpirePatch(clz = AbstractDungeon.class, method = "getRewardCards")
    public static class AddCursesToCardRewardsPatch {
        @SpireInsertPatch(locator = Locator.class, localvars = { "retVal" })
        public static void addCurses(ArrayList<AbstractCard> retVal) {
            if (TheLivingCurse.hasRelic()) {
                // This is effectively "1 card in 8 is replaced by a curse, but never multiple in one reward"
                float chance = 1 - (float)Math.pow(0.875, retVal.size());
                boolean addCurse = AbstractQuest.rng.randomBoolean(chance);
                if (addCurse) {
                    int i = AbstractQuest.rng.random(retVal.size() - 1);
                    retVal.set(i, getCurse(retVal.get(i).rarity));
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.NewExprMatcher(ArrayList.class);
                return LineFinder.findInOrder(ctMethodToPatch, Collections.singletonList(finalMatcher), finalMatcher);
            }
        }
    }
}
