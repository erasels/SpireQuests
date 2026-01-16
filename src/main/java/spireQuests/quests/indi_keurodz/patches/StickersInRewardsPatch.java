package spireQuests.quests.indi_keurodz.patches;

import java.util.ArrayList;

import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardItem;

import basemod.helpers.CardModifierManager;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.indi_keurodz.modifiers.EternalModifier;
import spireQuests.quests.indi_keurodz.modifiers.FoilModifier;
import spireQuests.quests.indi_keurodz.modifiers.HoloModifier;
import spireQuests.quests.indi_keurodz.modifiers.NegativeModifier;
import spireQuests.quests.indi_keurodz.modifiers.PerishableModifier;
import spireQuests.quests.indi_keurodz.modifiers.PolychromeModifier;
import spireQuests.quests.indi_keurodz.modifiers.RentalModifier;
import spireQuests.quests.indi_keurodz.relics.GoldStakeRelic;

public class StickersInRewardsPatch {
    @SpireEnum
    public static RewardItem.RewardType BALATRO_QUEST_REWARD;

    @SpirePatch2(clz = AbstractDungeon.class, method = "getRewardCards")
    public static class StickerModifiers {
        @SpirePostfixPatch
        public static void patch(ArrayList<AbstractCard> __result) {
            GoldStakeRelic relic = (GoldStakeRelic) AbstractDungeon.player.getRelic(GoldStakeRelic.ID);
            if (relic == null) {
                return;
            }
            boolean hasAddedPerishable = false;
            for (AbstractCard c : __result) {

                float perishable_eternal_roll = AbstractQuest.rng.random();
                if (perishable_eternal_roll < 0.3 && !hasAddedPerishable) {
                    CardModifierManager.addModifier(c, new PerishableModifier());
                    hasAddedPerishable = true;
                } else if (perishable_eternal_roll >= 0.3 && perishable_eternal_roll < 0.6) {
                    CardModifierManager.addModifier(c, new EternalModifier());
                }

                float rental_roll = AbstractQuest.rng.random();

                if (rental_roll < 0.3) {
                    CardModifierManager.addModifier(c, new RentalModifier());
                }

                if (relic.descriptionUpgraded()) {
                    float editions_roll = AbstractQuest.rng.random();
                    if (editions_roll < 0.05) {
                        CardModifierManager.addModifier(c, new PolychromeModifier());
                    } else if (editions_roll < 0.1) {
                        CardModifierManager.addModifier(c, new NegativeModifier());
                    } else if (editions_roll < 0.15) {
                        CardModifierManager.addModifier(c, new HoloModifier());
                    } else if (editions_roll < 0.2) {
                        CardModifierManager.addModifier(c, new FoilModifier());
                    }

                }

            }

        }

    }

}
