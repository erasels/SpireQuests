package spireQuests.quests.pandemonium;

import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;

import java.util.ArrayList;

@SpirePatch2(clz = AbstractDungeon.class, method = "getRewardCards")
public class PristinePatch {

    @SpirePostfixPatch
    public static ArrayList<AbstractCard> addPristineModifier(ArrayList<AbstractCard> __result) {
        AbstractQuest pristineQuest = null;
        for (AbstractQuest q : QuestManager.quests()) {
            if (q instanceof PristineCardsQuest) {
                pristineQuest = q;
            }
        }
        if (pristineQuest != null && !pristineQuest.isCompleted() && !pristineQuest.isFailed()) {
            int r = AbstractQuest.rng.random(99);
            if (r < PristineCardsQuest.PRISTINE_CARDS_RATE) {
                int attempts = 0;
                r = AbstractQuest.rng.random(__result.size() - 1);
                while (attempts < __result.size()) {
                    if (__result.get(r).canUpgrade()) {
                        CardModifierManager.addModifier(__result.get(r), new PristineModifier());
                        break;
                    } else {
                        attempts++;
                        r = r + 1 < __result.size() ? r + 1 : 0;
                    }
                }
            }
        }
        return __result;
    }
}
