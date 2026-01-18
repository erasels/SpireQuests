package spireQuests.quests.maybelaterx;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.maybelaterx.relics.TargetDummy;


public class HumbleSmithyQuest extends AbstractQuest {
    public HumbleSmithyQuest() {
        super(QuestType.LONG, QuestDifficulty.NORMAL);
        new TriggerTracker<>(QuestTriggers.UPGRADE_CARD_AT_CAMPFIRE, 3)
                .triggerCondition(card -> card.rarity == AbstractCard.CardRarity.BASIC)
                .add(this);
        addReward(new QuestReward.RelicReward(new TargetDummy()));
    }

    @Override
    public boolean canSpawn() {
        if (AbstractDungeon.floorNum > 33 && !Settings.isEndless)
            return false;

        int unupgradedStarterCards = 0;
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group)
            if (c.rarity == AbstractCard.CardRarity.BASIC && !c.upgraded)
                unupgradedStarterCards++;
        if (unupgradedStarterCards < 3)
            return false;

        return true;
    }
}