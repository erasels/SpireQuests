package spireQuests.quests.snumodder;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.snumodder.cards.KingLlane;
import spireQuests.quests.snumodder.relics.TheKingslayer;

import java.util.ArrayList;

public class GaronaHalforcenQuest extends AbstractQuest {
    public GaronaHalforcenQuest() {
        super(QuestType.SHORT, QuestDifficulty.HARD);
        new TriggerTracker<>(QuestTriggers.COMBAT_END, 3)
                .triggerCondition(t ->
                        AbstractDungeon.player.exhaustPile.group.stream()
                                .noneMatch(c -> c.cardID.equals(KingLlane.ID)))
                .add(this);

        questboundCards = new ArrayList<>();
        questboundCards.add(new KingLlane());

        addReward(new QuestReward.RelicReward(new TheKingslayer()));
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.floorNum < 43;
    }
}
