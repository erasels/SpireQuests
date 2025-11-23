package spireQuests.quests.indi_keurodz;

import java.util.ArrayList;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import basemod.helpers.CardModifierManager;

public class StickersInRewardsPatch {
    public static boolean ENABLED = false;

    @SpirePatch2(clz = AbstractDungeon.class, method = "getRewardCards")
    public static class StickerModifiers {
        @SpirePostfixPatch
        public static void patch(ArrayList<AbstractCard> __result) {
            if (!ENABLED) {
                return;
            }

            for (AbstractCard c : __result) {

                float perishable_eternal_roll = AbstractDungeon.cardRng.random();
                if (perishable_eternal_roll < 0.3) {
                    CardModifierManager.addModifier(c, new PerishableStickerModifier());
                } else if (perishable_eternal_roll < 0.6) {
                    CardModifierManager.addModifier(c, new EternalStickerModifier());
                }

                float rental_roll = AbstractDungeon.cardRng.random();

                if (rental_roll < 0.3) {
                    CardModifierManager.addModifier(c, new RentalStickerModifier());
                }

            }
        }

    }

    @SpirePatch2(clz = CardCrawlGame.class, method = "createCharacter")
    public static class DisableOnStartGame {
        @SpirePrefixPatch
        public static void patch() {
            ENABLED = false;
        }
    }

}
