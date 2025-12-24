package spireQuests.quests.indi_keurodz.BossBlinds;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.quests.indi_keurodz.patches.ShowBossBlindsOnMapPatch.BossBlindField;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;

public class TheEye {

    private static AbstractCard.CardType lastCardType = null;

    @SpirePatch2(clz = AbstractPlayer.class, method = "useCard")
    public static class EyePenaltyPatch {
        @SpirePostfixPatch
        public static void EyePenalty(AbstractPlayer __instance, AbstractCard c) {
            BossBlind blind = BossBlindField.blind.get(AbstractDungeon.getCurrMapNode());
            if (blind != BossBlind.Eye) return;
            if (lastCardType != null && lastCardType == c.type) {
                AbstractDungeon.actionManager.addToBottom(new LoseHPAction(
                        __instance,
                        __instance,
                        3
                ));
            }
            lastCardType = c.type;
        }
    }

    @SpirePatch2(clz = AbstractPlayer.class, method = "preBattlePrep")
    public static class ResetOnCombatStart {
        @SpirePostfixPatch
        public static void InitializeLastCardType() {
            lastCardType = null;
        }
    }

    @SpirePatch2(clz = GameActionManager.class, method = "callEndOfTurnActions")
    public static class ResetOnTurnEnd {
        @SpirePostfixPatch
        public static void ResetLastCardType() {
            lastCardType = null;
        }
    }
}