package spireQuests.quests.example;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.CrackedCore;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;

public class DefectOnlyTestQuest extends AbstractQuest {
    public DefectOnlyTestQuest() {
        super(QuestType.SHORT, QuestDifficulty.EASY);

        new TriggerTracker<>(QuestTriggers.ENTER_ROOM, 1)
                .triggerCondition((node) -> node.room instanceof MonsterRoomBoss)
                .add(this);

        addReward(new QuestReward.RelicReward(new CrackedCore()));
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.player.chosenClass == AbstractPlayer.PlayerClass.DEFECT;
    }
}
