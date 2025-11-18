package spireQuests.quests.mangochicken;

import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;



public class HandicapQuest extends AbstractQuest {
    public HandicapQuest() {
        super(QuestType.SHORT, QuestDifficulty.NORMAL);

        // Trigger only on the first turn of an elite combat
        new TriggerTracker<>(QuestTriggers.TURN_END, 1)
            .triggerCondition((x)-> AbstractDungeon.actionManager.cardsPlayedThisTurn.isEmpty()
                && AbstractDungeon.getCurrRoom() instanceof MonsterRoomElite
                && GameActionManager.turn == 1)
            .setResetTrigger(QuestTriggers.PLAY_CARD)
            .setResetTrigger(QuestTriggers.VICTORY) // maybe add ENTER_ROOM and LEAVE_ROOM just to make sure
            .add(this);
    }
}
