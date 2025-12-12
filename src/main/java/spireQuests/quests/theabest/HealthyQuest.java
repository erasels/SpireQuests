package spireQuests.quests.theabest;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;

public class HealthyQuest extends AbstractQuest {
    private int fullHealth = 0;

    public HealthyQuest() {
        super(QuestType.SHORT, QuestDifficulty.NORMAL);

        new PassiveTracker<Integer>(() -> fullHealth, 1)
                .setFailureTrigger(QuestTriggers.ENTER_ROOM, mapRoomNode -> mapRoomNode.getRoom() instanceof MonsterRoomBoss)
                .add(this);

        new TriggerEvent<Integer>(QuestTriggers.HEALTH_HEALED, (healAmount) -> {
            if (AbstractDungeon.player.currentHealth == AbstractDungeon.player.maxHealth)
                fullHealth = 1;
        }).add(this);

        new TriggerEvent<Void>(QuestTriggers.MAX_HEALTH_CHANGED, (x) -> {
            if (AbstractDungeon.player.currentHealth == AbstractDungeon.player.maxHealth)
                fullHealth = 1;
        }).add(this);
    }

    @Override
    public boolean canSpawn() {
        return ((float) AbstractDungeon.player.currentHealth) / AbstractDungeon.player.maxHealth <= .5;
    }

    @Override
    public void onComplete() {
        AbstractDungeon.player.increaseMaxHp(15, false);
    }

    @Override
    public String getRewardsText() {
        return super.getRewardsText() + "Increase your Max HP by #b15.";
    }
}
