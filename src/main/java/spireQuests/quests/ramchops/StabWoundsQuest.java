package spireQuests.quests.ramchops;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.monsters.beyond.Reptomancer;
import com.megacrit.cardcrawl.monsters.beyond.SnakeDagger;
import spireQuests.Anniv8Mod;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;
import spireQuests.quests.QuestReward;
import spireQuests.quests.Trigger;
import spireQuests.quests.ramchops.relics.SnakeSlough;

import static spireQuests.Anniv8Mod.makeID;

public class StabWoundsQuest extends AbstractQuest {

    private static final String ID = makeID(StabWoundsQuest.class.getSimpleName());

    private final int DAGGERS_COUNT = 6;

    public StabWoundsQuest() {
        super(QuestType.SHORT, QuestDifficulty.HARD);
        addReward(new QuestReward.RelicReward(new SnakeSlough()));

        new TriggerTracker<>(DAGGER_EXPLODE, DAGGERS_COUNT)
                .setFailureTrigger(QuestTriggers.LEAVE_ROOM, (room)->
                        Reptomancer.ID.equals(AbstractDungeon.lastCombatMetricKey))
                .add(this);

        new TriggerTracker<>(QuestTriggers.VICTORY, 1)
                .triggerCondition((x) -> AbstractDungeon.getCurrRoom().eliteTrigger &&
                        Reptomancer.ID.equals(AbstractDungeon.lastCombatMetricKey))
                .setFailureTrigger(QuestTriggers.ACT_CHANGE)
                .add(this);
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.actNum == 3;
    }

    private static final Trigger<Void> DAGGER_EXPLODE = new Trigger<>();

    @SpirePatch2(clz = SnakeDagger.class, method = "takeTurn")
    public static class DaggerExplodePatch{
        @SpirePostfixPatch
        public static void takeTurnPatch(SnakeDagger __instance){
            if (__instance.nextMove == 2){
                DAGGER_EXPLODE.trigger();
            }
        }
    }


    @SpirePatch2(clz = AbstractDungeon.class, method = "getEliteMonsterForRoomCreation")
    public static class SpawnElite {
        @SpirePrefixPatch
        public static SpireReturn<MonsterGroup> replacementPatch() {
            // if this quest exists
            StabWoundsQuest q = (StabWoundsQuest) QuestManager.quests().stream()
                    .filter(quest -> ID.equals(quest.id) && !quest.isCompleted() && !quest.isFailed())
                    .findAny()
                    .orElse(null);
            if(q != null) {
                Anniv8Mod.logger.info("Replacing ELITE with Reptomancer");
                AbstractDungeon.lastCombatMetricKey = Reptomancer.ID;
                return SpireReturn.Return(MonsterHelper.getEncounter(Reptomancer.ID));
            }
            return SpireReturn.Continue();
        }
    }
}
