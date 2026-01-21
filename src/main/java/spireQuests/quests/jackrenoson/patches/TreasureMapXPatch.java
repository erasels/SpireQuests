package spireQuests.quests.jackrenoson.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.CampfireUI;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;
import com.megacrit.cardcrawl.ui.campfire.DigOption;
import javassist.CtBehavior;
import spireQuests.quests.jackrenoson.TreasureMapQuest;
import spireQuests.patches.ShowMarkedNodesOnMapPatch;

import java.util.ArrayList;

public class TreasureMapXPatch {
    @SpirePatch2(clz = CampfireUI.class, method = "initializeButtons")
    public static class PostButtonAddingCatcher {
        @SpireInsertPatch(locator = Locator.class)
        public static void postAddingButtons(CampfireUI __instance, ArrayList<AbstractCampfireOption> ___buttons) {
            if(ShowMarkedNodesOnMapPatch.ImageField.CheckMarks(AbstractDungeon.getCurrMapNode(), TreasureMapQuest.id, TreasureMapQuest.X))
                ___buttons.add(new DigOption());
        }
    }

    public static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctBehavior) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "relics");
            return LineFinder.findInOrder(ctBehavior, finalMatcher);
        }
    }
}
