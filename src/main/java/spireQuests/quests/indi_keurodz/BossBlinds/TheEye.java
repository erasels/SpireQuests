package spireQuests.quests.indi_keurodz.BossBlinds;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import spireQuests.patches.ShowMarkedNodesOnMapPatch.ImageField;
import spireQuests.quests.indi_keurodz.BalatroQuest;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;

public class TheEye {

    private static AbstractCard.CardType lastCardType = null;

    @SpirePatch2(clz = AbstractPlayer.class, method = "useCard")
    public static class EyePenaltyPatch {
        @SpirePostfixPatch
        public static void EyePenalty(AbstractPlayer __instance, AbstractCard c) {
            if (!ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.ID, BossBlind.Eye.frames))
                return;
            if (lastCardType != null && lastCardType == c.type) {
                AbstractDungeon.actionManager.addToBottom(new LoseHPAction(
                        __instance,
                        __instance,
                        3));
            }
            lastCardType = c.type;
        }
    }

    @SpirePatch2(clz = AbstractCard.class, method = "update")
    public static class GlowOutlinePatch {
        @SpirePostfixPatch
        public static void AddGlowIfNotValidType(AbstractCard __instance) {
            if (!ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.ID, BossBlind.Eye.frames))
                return;
            if (lastCardType != null && __instance.type == lastCardType) {
                __instance.glowColor = Color.RED.cpy();
                __instance.triggerOnGlowCheck();
            }
            else __instance.glowColor = Color.CYAN;
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
