package spireQuests.quests.indi_keurodz.BossBlinds;

import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;
import spireQuests.quests.indi_keurodz.FaceDownModifier;
import spireQuests.quests.indi_keurodz.patches.ShowBossBlindsOnMapPatch.BossBlindField;

import java.util.List;

public class TheHouse {
    @SpirePatch(clz = AbstractPlayer.class, method = "draw", paramtypez = {int.class})
    public static class DrawCardPatch {
        private static boolean hasDrawnStartingHand = false;

        @SpirePostfixPatch
        public static void afterDraw(AbstractPlayer __instance, int numCards) {
            BossBlind blind = BossBlindField.blind.get(AbstractDungeon.getCurrMapNode());
            if (blind == null) return;

            Random rng = new Random(Settings.seed + AbstractDungeon.actNum * 1977L);

            for (AbstractCard card: __instance.hand.group) {
                switch (blind) {
                    case Wheel:
                        if (rng.random(7) == 1) CardModifierManager.addModifier(card, new FaceDownModifier());
                        break;
                    case House:
                        if (!hasDrawnStartingHand) {
                            CardModifierManager.addModifier(card, new FaceDownModifier());
                        }
                        break;
                    default:
                        break;
                }
            }

            if (!hasDrawnStartingHand && __instance.hand.size() >= __instance.gameHandSize) {
                hasDrawnStartingHand = true;
            }
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "applyStartOfCombatLogic")
    public static class ResetStartingHandFlag {
        @SpirePostfixPatch
        public static void reset(AbstractPlayer __instance) {
            DrawCardPatch.hasDrawnStartingHand = false;
        }
    }

    /*
        Probably an ideal entry point since it is on a per card drawn basis before it is shown
     */
//    @SpirePatch(clz = CardGroup.class, method = "moveToHand", paramtypez = {AbstractCard.class, CardGroup.class})
//    public static class MoveToHandPatch {
//        @SpirePostfixPatch
//        public static void Postfix(CardGroup __instance, AbstractCard c, CardGroup group) {
//
//        }
//    }

}