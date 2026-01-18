package spireQuests.quests.indi_keurodz.BossBlinds;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import spireQuests.patches.ShowMarkedNodesOnMapPatch.ImageField;
import spireQuests.quests.indi_keurodz.BalatroQuest;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;

public class TheHook {

    @SpirePatch2(clz = AbstractPlayer.class, method = "useCard")
    public static class HookEffectPatch {
        @SpirePostfixPatch
        public static void HookDiscardAndDraw(AbstractPlayer __instance, AbstractCard c) {
            if (!ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.ID, BossBlind.Hook.frames))
                return;

            int handSize = __instance.hand.size();
            int cardsToDiscard = Math.min(2, handSize);

            if (cardsToDiscard > 0) {
                AbstractDungeon.actionManager.addToBottom(new DiscardAction(
                        __instance,
                        __instance,
                        cardsToDiscard,
                        true));

                AbstractDungeon.actionManager.addToBottom(new DrawCardAction(
                        __instance,
                        cardsToDiscard));
            }
        }
    }
}
