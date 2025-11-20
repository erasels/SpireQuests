package spireQuests.quests.modargo;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.unique.IncreaseMaxHpAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.modargo.powers.DamageUpPower;

public class SuperiorEliteQuest extends AbstractQuest {
    public SuperiorEliteQuest() {
        super(QuestType.SHORT, QuestDifficulty.HARD);

        new TriggeredUpdateTracker<>(QuestTriggers.VICTORY, 0, 1, () -> AbstractDungeon.currMapNode != null && AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().eliteTrigger ? 1 : 0)
                .add(this);

        new TriggerEvent<>(QuestTriggers.BEFORE_COMBAT_START, x -> {
            if (AbstractDungeon.getCurrRoom().eliteTrigger) {
                for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                    int healthDamagePercent = 15 + AbstractDungeon.actNum * 5;
                    AbstractDungeon.actionManager.addToBottom(new IncreaseMaxHpAction(m, healthDamagePercent / 100.0f, true));
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, m, new DamageUpPower(m, healthDamagePercent)));
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, m, new ArtifactPower(m, AbstractDungeon.actNum)));
                }
            }
        }).add(this);

        addReward(new QuestReward.RandomRelicReward());
    }
}
