package spireQuests.quests.indi_keurodz.BossBlinds;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import spireQuests.patches.ShowMarkedNodesOnMapPatch;
import spireQuests.quests.indi_keurodz.BalatroQuest;
import spireQuests.util.Wiz;

public class TheSerpent {
    @SpirePatch2(clz = AbstractPlayer.class, method = "preBattlePrep")
    public static class SetInitialHandSizePatch {
        @SpirePostfixPatch
        public static void SetHandSize() {
            if (ShowMarkedNodesOnMapPatch.ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.ID,
                    BalatroQuest.BossBlind.Serpent.frames)) {
                Wiz.p().gameHandSize = 3;
            }
        }
    }

    @SpirePatch2(clz = AbstractPlayer.class, method = "useCard")
    public static class PatchUseCard {
        @SpirePostfixPatch
        public static void DrawThreeCardsAfterUse() {
            if (ShowMarkedNodesOnMapPatch.ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.ID,
                    BalatroQuest.BossBlind.Serpent.frames)) {
                Wiz.atb(new DrawCardAction(3));
            }
        }
    }
}
