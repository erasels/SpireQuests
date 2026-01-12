package spireQuests.quests.dayvig;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;

public class CowardiceQuest extends AbstractQuest {
    public CowardiceQuest() {
        super(QuestType.SHORT, QuestDifficulty.NORMAL);

        new TriggerTracker<>(QuestTriggers.LEAVE_ROOM, 3)
                .triggerCondition(this::dodgedElite)
                .add(this);

        addReward(new QuestReward.RandomRelicReward(AbstractRelic.RelicTier.COMMON));
    }

    public boolean dodgedElite(MapRoomNode currNode){
        boolean connectedToElite = false;
        int y = currNode.y + 1;
        if (AbstractDungeon.map.size() > y) {
            for (MapRoomNode m : AbstractDungeon.map.get(y)){
                if (m.getRoom() != null && currNode.isConnectedTo(m) && m.getRoom() instanceof MonsterRoomElite && m != AbstractDungeon.nextRoom){
                    connectedToElite = true;
                    break;
                }
            }
        }
        return connectedToElite && !(AbstractDungeon.nextRoom.room instanceof MonsterRoomElite);
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.actNum < 3;
    }
}
