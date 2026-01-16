package spireQuests.quests.dayvig.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.shop.ShopScreen;
import spireQuests.quests.dayvig.relics.Binder;

import java.util.ArrayList;

public class BinderPatch {
    @SpirePatch(clz = ShopScreen.class, method = "init")

    public static class BinderCostReducePatch {

        @SpirePostfixPatch
        public static void Postfix(ShopScreen __instance, ArrayList<AbstractCard> coloredCards, ArrayList<AbstractCard> colorlessCards){
            if (AbstractDungeon.player.hasRelic(Binder.ID) && AbstractDungeon.player.masterDeck.size() > 25) {
                ShopScreen.actualPurgeCost = 25;
            }
        }
    }
}









