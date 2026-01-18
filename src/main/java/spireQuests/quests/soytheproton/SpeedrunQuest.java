package spireQuests.quests.soytheproton;

import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.colorless.MasterOfStrategy;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;

public class SpeedrunQuest extends AbstractQuest {
    public SpeedrunQuest() {
        super(QuestType.SHORT, QuestDifficulty.HARD);

        new TriggerTracker<>(QuestTriggers.COMBAT_END, 3)
                .triggerCondition((x) -> GameActionManager.turn <= 4)
                .add(this);
        addReward(new QuestReward.CardReward(new MasterOfStrategy()));
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.actNum <= 2;
    }
}
