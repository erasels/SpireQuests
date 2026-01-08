package spireQuests.quests.jackrenoson.patches;

import basemod.Pair;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.CampfireUI;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;
import com.megacrit.cardcrawl.ui.campfire.DigOption;
import spireQuests.quests.jackrenoson.TreasureMapQuest;
import spireQuests.patches.ShowMarkedNodesOnMapPatch;

import java.util.ArrayList;

public class TreasureMapXPatch {

    @SpirePatch2(clz = CampfireUI.class, method = "initializeButtons")
    public static class PostButtonAddingCatcher {
        @SpireInsertPatch(rloc = 25) // 117: boolean cannotProceed = true;
        public static void postAddingButtons(CampfireUI __instance, ArrayList<AbstractCampfireOption> ___buttons) {
            if(ShowMarkedNodesOnMapPatch.ImageField.CheckMarks(AbstractDungeon.getCurrMapNode(), TreasureMapQuest.id, TreasureMapQuest.X))
                ___buttons.add(new DigOption());
        }
    }

}
