package spireQuests.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.ui.QuestBoardScreen;

@SpirePatch2(cls = "downfall.vfx.TopLevelInfiniteSpeechBubble", method = "render", requiredModId = "downfall")
@SpirePatch2(cls = "downfall.vfx.CustomSpeechTextEffect", method = "render", requiredModId = "downfall")
public class DownfallPreventHeartDialogCoveringQuests {
    @SpirePrefixPatch
    public static SpireReturn<Void> fix() {
        if (AbstractDungeon.screen == QuestBoardScreen.Enum.QUEST_BOARD) {
            return SpireReturn.Return();
        }
        return SpireReturn.Continue();
    }
}
