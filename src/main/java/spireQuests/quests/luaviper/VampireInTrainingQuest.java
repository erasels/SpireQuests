package spireQuests.quests.luaviper;

import basemod.helpers.CardPowerTip;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.Bite;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.luaviper.relics.VampireFangs;

import java.util.List;

public class VampireInTrainingQuest extends AbstractQuest {
    public VampireInTrainingQuest() {
        super(QuestType.LONG, QuestDifficulty.EASY);

        titleScale = 0.85f;

        new TriggerTracker<>(QuestTriggers.PLAY_CARD, 66)
                .triggerCondition(card -> card.tags.contains(AbstractCard.CardTags.STARTER_STRIKE))
                .setFailureTrigger(QuestTriggers.NO_STARTER_STRIKES)
                .add(this);

        addReward(new QuestReward.RelicReward(new VampireFangs()));
    }

    @Override
    public void makeTooltips(List<PowerTip> tipList) {
        super.makeTooltips(tipList);
        Bite bite = new Bite();
        tipList.add(new CardPowerTip(bite));
    }

    @Override
    public boolean canSpawn() {
        int starterStrikeCount = (int) AbstractDungeon.player.masterDeck.group.stream().filter(card -> card.tags.contains(AbstractCard.CardTags.STARTER_STRIKE)).count();

        return AbstractDungeon.actNum == 1 && starterStrikeCount >= 4;
    }
}