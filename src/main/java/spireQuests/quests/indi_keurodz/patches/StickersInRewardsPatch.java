package spireQuests.quests.indi_keurodz.patches;

import java.util.ArrayList;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import basemod.helpers.CardModifierManager;
import spireQuests.quests.indi_keurodz.EternalStickerModifier;
import spireQuests.quests.indi_keurodz.PerishableStickerModifier;
import spireQuests.quests.indi_keurodz.RentalStickerModifier;
import spireQuests.quests.indi_keurodz.relics.GoldStakeRelic;

public class StickersInRewardsPatch {

    @SpirePatch2(clz = AbstractDungeon.class, method = "getRewardCards")
    public static class StickerModifiers {
        @SpirePostfixPatch
        public static void patch(ArrayList<AbstractCard> __result) {
            if (!AbstractDungeon.player.hasRelic(GoldStakeRelic.ID)) {
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

}
