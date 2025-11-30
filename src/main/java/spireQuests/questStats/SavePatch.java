package spireQuests.questStats;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;

@SpirePatch2 (clz = SaveAndContinue.class, method = "save")
public class SavePatch {
    @SpirePrefixPatch
    public static void prefix() {
        QuestStatManager.commitStats();
    }
}
