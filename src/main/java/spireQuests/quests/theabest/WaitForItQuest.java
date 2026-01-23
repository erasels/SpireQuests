package spireQuests.quests.theabest;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.theabest.relics.GlassBar;

public class WaitForItQuest extends AbstractQuest {
    private boolean progressThisCombat = false;

    public WaitForItQuest() {
        super(QuestType.LONG, QuestDifficulty.NORMAL);
        new TriggerTracker<>(QuestTriggers.BEFORE_TURN_END, 5)
                .triggerCondition((x) -> {
                    if (progressThisCombat)
                        return false;
                    if (AbstractDungeon.actionManager.cardsPlayedThisTurn.isEmpty()) {
                        progressThisCombat = true;
                        return true;
                    }
                    return false;
                })
                .add(this);
        new TriggerEvent<>(QuestTriggers.COMBAT_END, (x) -> progressThisCombat = false)
                .add(this);
        addReward(new QuestReward.RelicReward(new GlassBar()));
    }
}
