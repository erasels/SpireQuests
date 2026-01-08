package spireQuests.quests.mangochicken;

import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.mangochicken.relics.HeartyAnchor;
import spireQuests.util.NodeUtil;


public class HandicapQuest extends AbstractQuest {
    public HandicapQuest() {
        super(QuestType.SHORT, QuestDifficulty.NORMAL);

        needHoverTip = true;

        new TriggerTracker<>(QuestTriggers.TURN_END, 1)
            .triggerCondition((x)-> AbstractDungeon.actionManager.cardsPlayedThisTurn.isEmpty()
                && AbstractDungeon.getCurrRoom().eliteTrigger
                && GameActionManager.turn == 1)
            .setResetTrigger(QuestTriggers.PLAY_CARD)
            .setResetTrigger(QuestTriggers.VICTORY)
            .setFailureTrigger(QuestTriggers.ACT_CHANGE)
            .add(this);

        addReward(new QuestReward.RelicReward(new HeartyAnchor()));
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.actNum == 2 && NodeUtil.canPathToElite();
    }
}
