package spireQuests.quests.indi_keurodz.BossBlinds;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import spireQuests.patches.ShowMarkedNodesOnMapPatch.ImageField;
import spireQuests.quests.indi_keurodz.BalatroQuest;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;

public class TheNeedlePsychic {

    private static boolean finishedDrawingStartingHand = false;

    @SpirePatch2(clz = AbstractPlayer.class, method = "applyStartOfTurnPostDrawRelics")
    public static class FinishedStartingHand {
        @SpirePostfixPatch
        public static void finished() {
            if (!ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.ID, BossBlind.Needle.frames))
                return;

            if (!finishedDrawingStartingHand) {
                AbstractDungeon.player.draw(5);
                AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(3));
                finishedDrawingStartingHand = true;
            }

        }
    }

    @SpirePatch2(clz = GameActionManager.class, method = "callEndOfTurnActions")
    public static class DeductHealthPerEnergy {
        @SpirePostfixPatch
        public static void DeductHealth() {
            AbstractPlayer player = AbstractDungeon.player;

            if (ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.ID, BossBlind.Needle.frames)) {
                AbstractDungeon.actionManager.addToBottom(new LoseHPAction(
                        player,
                        player,
                        10));
            } else if (ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.ID, BossBlind.Psychic.frames)) {
                for (int i = 0; i < EnergyPanel.getCurrentEnergy(); i++) {
                    AbstractDungeon.actionManager.addToBottom(new LoseHPAction(
                            player,
                            player,
                            3));
                }
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
}
