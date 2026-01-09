package spireQuests.quests.indi_keurodz.BossBlinds;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import javassist.CtBehavior;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;
import spireQuests.quests.indi_keurodz.BalatroQuest;

import spireQuests.patches.ShowMarkedNodesOnMapPatch.ImageField;

@SpirePatch2(clz = AbstractPlayer.class, method = "preBattlePrep")
public class TheManacle {

    @SpireInsertPatch(locator = Locator.class)
    public static void Insert() {
        if (!ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.id, BossBlind.Manacle.frames))
            return;

        AbstractDungeon.player.gameHandSize--;
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "isDraggingCard");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }

}
