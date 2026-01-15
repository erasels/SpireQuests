package spireQuests.quests.jackrenoson.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.Anchor;
import com.megacrit.cardcrawl.relics.CaptainsWheel;
import com.megacrit.cardcrawl.relics.HornCleat;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;
import spireQuests.quests.jackrenoson.BoatRepairQuest;

import static spireQuests.util.Wiz.atb;

public class BoatRepairLessBlockPatch {

    @SpirePatch2(clz = Anchor.class, method = "atBattleStart")
    public static class BrokenAnchor {
        @SpirePrefixPatch
        public static SpireReturn<Void> CheckIfBroken(Anchor __instance) {
            if(shouldRun()) {
                __instance.flash();
                atb(new RelicAboveCreatureAction(AbstractDungeon.player, __instance));
                atb(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, 2));
                __instance.grayscale = true;
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }
    @SpirePatch2(clz = Anchor.class, method = "getUpdatedDescription")
    public static class BrokenAnchorText {
        @SpirePrefixPatch
        public static SpireReturn<String> CheckIfBroken(Anchor __instance) {
            if (shouldRun())
                return SpireReturn.Return(__instance.DESCRIPTIONS[0] + 2 + __instance.DESCRIPTIONS[1]);
            return SpireReturn.Continue();
        }
    }


    @SpirePatch2(clz = HornCleat.class, method = "atTurnStart")
    public static class BrokenCleat {
        @SpirePrefixPatch
        public static SpireReturn<Void> CheckIfBroken(HornCleat __instance) {
            if (shouldRun() && __instance.counter == 1) {
                __instance.flash();
                atb(new RelicAboveCreatureAction(AbstractDungeon.player, __instance));
                atb(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, 4));
                __instance.grayscale = true;
                __instance.counter = -1;
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }
    @SpirePatch2(clz = HornCleat.class, method = "getUpdatedDescription")
    public static class BrokenCleatText {
        @SpirePrefixPatch
        public static SpireReturn<String> CheckIfBroken(HornCleat __instance) {
            if (shouldRun())
                return SpireReturn.Return(__instance.DESCRIPTIONS[0] + 4 + __instance.DESCRIPTIONS[1]);
            return SpireReturn.Continue();
        }
    }


    @SpirePatch2(clz = CaptainsWheel.class, method = "atTurnStart")
    public static class BrokenWheel {
        @SpirePrefixPatch
        public static SpireReturn<Void> CheckIfBroken(CaptainsWheel __instance) {
            if(shouldRun() && __instance.counter == 2) {
                        __instance.flash();
                        atb(new RelicAboveCreatureAction(AbstractDungeon.player, __instance));
                        atb(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, 6));
                        __instance.grayscale = true;
                        __instance.counter = -1;
                        return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }
    @SpirePatch2(clz = CaptainsWheel.class, method = "getUpdatedDescription")
    public static class BrokenWheelText {
        @SpirePrefixPatch
        public static SpireReturn<String> CheckIfBroken(CaptainsWheel __instance) {
            if(shouldRun())
                return SpireReturn.Return(__instance.DESCRIPTIONS[0] + 6 + __instance.DESCRIPTIONS[1]);
            return SpireReturn.Continue();
        }
    }

    private static boolean shouldRun(){
        if(CardCrawlGame.isInARun())
            for (AbstractQuest quest : QuestManager.quests())
                if (quest instanceof BoatRepairQuest)
                    return true;
        return false;
    }
}
