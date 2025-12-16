package spireQuests.quests.snumodder;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.BlueCandle;

@SpirePatch2(clz = BlueCandle.class, method = "onUseCard", paramtypez = { AbstractCard.class, UseCardAction.class})
public class KingLlaneBlueCandlePatch {
    @SpirePrefixPatch
    public static SpireReturn<Void> PreventBlueCandleFromMakingKingLlaneQuestImpossible(AbstractRelic __instance, AbstractCard card, UseCardAction action) {
        if (card instanceof KingLlane) {
            return SpireReturn.Return();
        }
        return SpireReturn.Continue();
    }
}
