package spireQuests.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.relics.SacredBark;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;
import spireQuests.quests.QuestReward;

@SpirePatch2(clz = SacredBark.class, method = "onEquip")
public class UpdateQuestPotionsForSacredBark {
    @SpirePostfixPatch
    public static void updateQuestPotions(SacredBark __instance) {
        for (AbstractQuest quest : QuestManager.quests()) {
            for (QuestReward reward : quest.questRewards) {
                if (reward instanceof QuestReward.PotionReward) {
                    ((QuestReward.PotionReward)reward).getPotion().initializeData();
                }
            }
        }
    }
}
