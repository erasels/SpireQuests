package spireQuests.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import spireQuests.ui.QuestBoardProp;

@SpirePatch(clz = AbstractDungeon.class, method = "nextRoomTransition", paramtypez = {SaveFile.class})
public class ResetQuestBoardPatch {
    @SpirePrefixPatch
    public static void enteringRoomPatch(AbstractDungeon __instance, SaveFile file) {
        QuestBoardProp.questBoardProp = null;
    }
}