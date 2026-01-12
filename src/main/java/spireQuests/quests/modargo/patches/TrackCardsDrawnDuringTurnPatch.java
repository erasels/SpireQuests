package spireQuests.quests.modargo.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.List;

public class TrackCardsDrawnDuringTurnPatch {
    public static List<AbstractCard> cardsDrawnThisTurn = new ArrayList<>();

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "draw",
            paramtypez = {int.class}
    )
    public static class OnDrawCardPatch {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"c"}
        )
        public static void onDraw(AbstractPlayer __instance, AbstractCard c) {
            cardsDrawnThisTurn.add(c);
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "applyStartOfTurnRelics"
    )
    public static class ResetCardsDrawnThisTurnPatch {
        @SpirePrefixPatch
        public static void resetCardsDrawnThisTurn(AbstractPlayer __instance) {
            cardsDrawnThisTurn.clear();
        }
    }
}