package spireQuests.quests.coda;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;

import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward.RelicReward;
import spireQuests.quests.coda.relics.GrapefruitRelic;

public class UnbreakableQuest extends AbstractQuest {

    private static final int FLOOR_MAX = 17;
    private int damageGoal = -1;

    public UnbreakableQuest() {
        super(QuestType.LONG, QuestDifficulty.HARD);
        if (AbstractDungeon.player != null) {
            this.damageGoal = AbstractDungeon.player.startingMaxHP;
        }

        new TriggeredUpdateTracker<Integer, Integer>(QuestTriggers.DAMAGE_TAKEN,
            0, this.damageGoal, Integer::sum, ()->false) {
                @Override
                public boolean isComplete() {
                    return (condition == null || condition.get()) && state >= target;
                }
            }
            .add(this);

        new TriggerTracker<MapRoomNode>(QuestTriggers.LEAVE_ROOM, FLOOR_MAX)
            {
                @Override
                public boolean isComplete() {
                    return count <= targetCount;
                }
                @Override
                public boolean isFailed() {
                    return count > targetCount;
                }
            }
        .add(this);

        addReward(new RelicReward(new GrapefruitRelic()));
        
    }

    @Override
    public String getDescription() {
        if (!CardCrawlGame.isInARun()) {
            return super.getDescription();
        }
        return localization.TEXT[3] + this.damageGoal + localization.TEXT[4];
    }
}
