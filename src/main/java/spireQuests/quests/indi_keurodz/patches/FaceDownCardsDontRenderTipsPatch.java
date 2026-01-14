
package spireQuests.quests.indi_keurodz.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.helpers.CardModifierManager;
import spireQuests.quests.indi_keurodz.modifiers.FaceDownModifier;

@SpirePatch2(clz = AbstractCard.class, method = "renderCardTip")
public class FaceDownCardsDontRenderTipsPatch {

    @SpirePrefixPatch
    public static SpireReturn<Void> NoTipForFaceDown(AbstractCard __instance) {
        if (CardModifierManager.hasModifier(__instance, FaceDownModifier.MODIFIER_ID)) {
            return SpireReturn.Return();
        }

        return SpireReturn.Continue();

    }

}
