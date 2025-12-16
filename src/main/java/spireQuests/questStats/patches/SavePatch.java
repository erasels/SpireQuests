package spireQuests.questStats.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;

import spireQuests.questStats.QuestStatManager;

@SpirePatch2 (clz = SaveAndContinue.class, method = "save")
public class SavePatch {
    @SpirePrefixPatch
    public static void Prefix() {
        QuestStatManager.commitStats();
    }
}
