package spireQuests.quests.indi_keurodz.BossBlinds;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import basemod.helpers.CardModifierManager;
import javassist.CtBehavior;
import spireQuests.patches.ShowMarkedNodesOnMapPatch.ImageField;
import spireQuests.quests.indi_keurodz.BalatroQuest;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;
import spireQuests.quests.indi_keurodz.modifiers.FaceDownModifier;
import spireQuests.util.Wiz;

public class TheHouseWheelMarkFish {

    private static boolean finishedDrawingStartingHand = false;
    private static boolean finishedInitialDraw = false;

    @SpirePatch2(clz = AbstractPlayer.class, method = "draw", paramtypez = { int.class })
    public static class DrawCardPatch {

        @SpireInsertPatch(locator = Locator.class, localvars = { "c" })
        public static void onDraw(AbstractCard c) {
            if (ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.ID, BossBlind.Wheel.frames)) {
                // 1 in 7 chance
                if (AbstractDungeon.cardRandomRng.randomBoolean(0.143f))
                    CardModifierManager.addModifier(c, new FaceDownModifier());
            } else if (ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.ID, BossBlind.House.frames)) {
                if (!finishedDrawingStartingHand) {
                    CardModifierManager.addModifier(c, new FaceDownModifier());
                }
            } else if (ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.ID, BossBlind.Mark.frames)) {
                if (c.type == AbstractCard.CardType.POWER)
                    CardModifierManager.addModifier(c, new FaceDownModifier());
            } else if (ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.ID, BossBlind.Fish.frames)) {
                if (finishedInitialDraw) {
                    CardModifierManager.addModifier(c, new FaceDownModifier());
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(CardGroup.class, "addToHand");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = AbstractPlayer.class, method = "applyStartOfCombatPreDrawLogic")
    public static class ResetStartingHandFlag {
        @SpirePostfixPatch
        public static void reset() {
            finishedDrawingStartingHand = false;
        }
    }

    @SpirePatch2(clz = AbstractPlayer.class, method = "applyStartOfTurnRelics")
    public static class ResetInitialDrawFlag {
        @SpirePrefixPatch
        public static void reset() {
            finishedInitialDraw = false;
        }
    }

    @SpirePatch2(clz = AbstractPlayer.class, method = "applyStartOfTurnPostDrawRelics")
    public static class FinishedStartingHand {
        @SpirePrefixPatch
        public static void finished() {
            Wiz.atb(new AbstractGameAction() {

                @Override
                public void update() {
                    isDone = true;
                    finishedDrawingStartingHand = true;
                    finishedInitialDraw = true;
                }
            });
        }
    }
}
