package spireQuests.quests.luaviper;

import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.Chrysalis;
import com.megacrit.cardcrawl.cards.colorless.Metamorphosis;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.cardmods.QuestboundMod;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.luaviper.cards.Ecdysis;

import java.util.ArrayList;
import java.util.Objects;

public class RapidImprovisationQuest extends AbstractQuest {
    public AbstractCard nextAddedCard;

    public RapidImprovisationQuest() {
        super(QuestType.SHORT, QuestDifficulty.NORMAL);

        titleScale = 0.9f;

        questboundCards = new ArrayList<>();
        questboundCards.add(new Metamorphosis());
        questboundCards.add(new Chrysalis());

        new TriggerTracker<>(QuestTriggers.PLAY_CARD, 1)
                .triggerCondition((card) -> Objects.equals(card.cardID, Metamorphosis.ID) && CardModifierManager.hasModifier(card, QuestboundMod.ID))
                .setFailureTrigger(QuestTriggers.COMBAT_END)
                .add(this);
        new TriggerTracker<>(QuestTriggers.PLAY_CARD, 1)
                .triggerCondition((card) -> Objects.equals(card.cardID, Chrysalis.ID) && CardModifierManager.hasModifier(card, QuestboundMod.ID))
                .setFailureTrigger(QuestTriggers.COMBAT_END)
                .add(this);
        //note that there is a delay between playing a card and adding the new trackers,
        //so we must override questConditionsAreFulfilled in order to prevent exploits

        addReward(new QuestReward.CardReward(new Ecdysis()));
    }

    @Override
    protected void assignTrackerText(Tracker questTracker) {
        if (trackerTextIndex >= questStrings.TRACKER_TEXT.length) {
            if (nextAddedCard == null) {
                throw new RuntimeException("Quest " + id + " needs more entries in TRACKER_TEXT for its trackers");
            }
            questTracker.text = questStrings.EXTRA_TEXT[0] + nextAddedCard.name + questStrings.EXTRA_TEXT[1];
            nextAddedCard = null;
        } else {
            questTracker.text = questStrings.TRACKER_TEXT[trackerTextIndex];
        }
        trackerTextIndex++;
    }

    @Override
    protected boolean questConditionsAreFulfilled() {
        return super.questConditionsAreFulfilled() && AbstractDungeon.actionManager.actions.isEmpty();
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.actNum >= 2;
    }

}