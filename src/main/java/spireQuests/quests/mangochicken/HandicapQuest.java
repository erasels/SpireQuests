package spireQuests.quests.mangochicken;

import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;



public class HandicapQuest extends AbstractQuest {

    static int turnNum = 1;

    public HandicapQuest() {
        super(QuestType.SHORT, QuestDifficulty.NORMAL);

        // Trigger only on the first turn of an elite combat,
        new TriggerTracker<>(QuestTriggers.TURN_END, 1)
                .triggerCondition((x)-> AbstractDungeon.actionManager.cardsPlayedThisTurn.isEmpty()
                        && AbstractDungeon.getCurrRoom() instanceof MonsterRoomElite
                        && turnNum == 1);

    }


}
