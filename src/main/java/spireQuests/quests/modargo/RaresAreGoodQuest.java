package spireQuests.quests.modargo;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.modargo.relics.PeasantsTunic;

import java.util.ArrayList;

public class RaresAreGoodQuest extends AbstractQuest {
    public RaresAreGoodQuest() {
        super(QuestType.LONG, QuestDifficulty.EASY);
        new TriggerTracker<>(QuestTriggers.ADD_CARD, 4)
                .triggerCondition(c -> c.rarity == AbstractCard.CardRarity.RARE)
                .add(this);

        addGenericReward();
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.actNum == 1 || AbstractDungeon.actNum == 2;
    }
}
