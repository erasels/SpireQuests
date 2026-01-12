package spireQuests.quests.iry;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.iry.patches.TwosCompanyPatches;
import spireQuests.quests.iry.relics.BrokenHeartPendant;

// TwosCompanyQuest:
// Have 2 enemies alive at the start of your final turn of combat for 3 combats
// Rewards the Broken Heart Pendant
public class TwosCompanyQuest extends AbstractQuest {

    public TwosCompanyQuest() {
        super(QuestType.SHORT, QuestDifficulty.NORMAL);

        new TriggerTracker<>(QuestTriggers.COMBAT_END, 3)
                .triggerCondition(x -> TwosCompanyPatches.enemiesAliveThisTurn == 2)
                .add(this);

        AbstractRelic rewardRelic = new BrokenHeartPendant();
        addReward(new QuestReward.RelicReward(rewardRelic));

        useDefaultReward = false;
    }

    @Override
    public boolean canSpawn() {
        return (AbstractDungeon.actNum == 1 || AbstractDungeon.actNum == 2) && AbstractDungeon.floorNum > 0;
    }
}
