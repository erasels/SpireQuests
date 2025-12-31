package spireQuests.quests.coda;

import static spireQuests.Anniv8Mod.makeID;

import java.util.ArrayList;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

import spireQuests.Anniv8Mod;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;
import spireQuests.quests.QuestReward.RelicReward;
import spireQuests.quests.coda.monsters.CharadeMonster;
import spireQuests.quests.coda.monsters.CharadeMonster.OrbColor;
import spireQuests.quests.coda.relics.CharadeOrbRelic;
import spireQuests.util.NodeUtil;

public class BountyCharadeQuest extends AbstractQuest {

    public static final Object ID = makeID(BountyCharadeQuest.class.getSimpleName());
    public static ArrayList<OrbColor> orbOrder = null;

    public BountyCharadeQuest() {
        super(QuestType.SHORT, QuestDifficulty.HARD);

        new TriggerTracker<>(QuestTriggers.VICTORY, 1)
            .triggerCondition((x) -> AbstractDungeon.getCurrRoom().eliteTrigger &&
                CharadeMonster.ID.equals(AbstractDungeon.lastCombatMetricKey))
            .setFailureTrigger(QuestTriggers.ACT_CHANGE)
            .add(this);
    
        addReward(new RelicReward(new CharadeOrbRelic()));
    }
    

    @Override
    public float getTitleScale() {
        return 0.8f;
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.actNum == 3 && NodeUtil.canPathToElite();
    }

    @SpirePatch2(clz = AbstractDungeon.class, method = "getEliteMonsterForRoomCreation")
    public static class SpawnElite {
        @SpirePrefixPatch
        public static SpireReturn<MonsterGroup> replacementPatch() {
            // if this quest exists
            BountyCharadeQuest q = (BountyCharadeQuest) QuestManager.quests().stream()
                    .filter(quest -> ID.equals(quest.id) && !quest.isCompleted() && !quest.isFailed())
                    .findAny()
                    .orElse(null);
            if(q != null) {
                Anniv8Mod.logger.info("Replacing ELITE with CharadeMonster");
                AbstractDungeon.lastCombatMetricKey = CharadeMonster.ID;
                return SpireReturn.Return(new MonsterGroup(new CharadeMonster()));
            }
            return SpireReturn.Continue();
        }
    }
    
}
