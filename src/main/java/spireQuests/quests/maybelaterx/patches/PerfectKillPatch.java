package spireQuests.quests.maybelaterx.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.maybelaterx.relics.BalancingStones;

public class PerfectKillPatch {
    @SpirePatch2(clz = AbstractMonster.class, method = SpirePatch.CLASS)
    public static class IgnoreField {
        public static SpireField<Boolean> ignore = new SpireField<>(() -> false);
    }

    @SpirePatch(
            clz = AbstractMonster.class,
            method = "damage"
    )
    public static class PerfectKillTrigger {
        @SpireInsertPatch(rloc = 77)
        public static void Insert(AbstractMonster __instance) {
            if (__instance.currentHealth == 0 && !IgnoreField.ignore.get(__instance)) {
                QuestTriggers.EXACT_KILL.trigger();
                for (AbstractRelic relic : AbstractDungeon.player.relics) {
                    if (relic instanceof BalancingStones) {
                        relic.onTrigger();
                    }
                }
            }
        }
    }

    @SpirePatch2(clz = SuicideAction.class, method = "update")
    public static class DontCountSuicideAction {
        @SpirePrefixPatch
        public static void patch(SuicideAction __instance, float ___duration, AbstractMonster ___m) {
            IgnoreField.ignore.set(___m, true);
        }

        @SpirePostfixPatch
        public static void patch2(SuicideAction __instance, float ___duration, AbstractMonster ___m) {
            IgnoreField.ignore.set(___m, false);
        }
    }

    @SpirePatch2(clz = LoseHPAction.class, method = "update")
    public static class DontCountSelfHPLoss {
        @SpirePrefixPatch
        public static void patch(LoseHPAction __instance, AbstractCreature ___target, AbstractCreature ___source) {
            if (___target == ___source && ___target instanceof AbstractMonster) {
                IgnoreField.ignore.set(___target, true);
            }
        }

        @SpirePostfixPatch
        public static void patch2(LoseHPAction __instance, AbstractCreature ___target, AbstractCreature ___source) {
            if (___target == ___source && ___target instanceof AbstractMonster) {
                IgnoreField.ignore.set(___target, false);
            }
        }
    }
}