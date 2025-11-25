package spireQuests.quests.indi_keurodz.patches;

import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.actions.utility.ShowCardAndPoofAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import spireQuests.quests.indi_keurodz.PerishableStickerModifier;
import spireQuests.quests.indi_keurodz.RentalStickerModifier;
import spireQuests.quests.indi_keurodz.RentalStickerEffect;

import java.util.ArrayList;

public class BattleEndPatch {
    @SpirePatch2(clz = AbstractPlayer.class, method = "onVictory")
    public static class OnCombatEndOrVictory {
        @SpirePrefixPatch
        public static void combatEndOrVictoryPatch() {
            if (!AbstractDungeon.getCurrRoom().smoked) {
                int stickers = 0;
                ArrayList<AbstractCard> purgedCards = new ArrayList<>();
                for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
                    if (CardModifierManager.hasModifier(card, RentalStickerModifier.MODIFIER_ID)) {
                        stickers++;
                    }
                    if (CardModifierManager.hasModifier(card, PerishableStickerModifier.MODIFIER_ID)) {
                        // Assumes we can only have one of this modifier on a card
                        if (((PerishableStickerModifier) CardModifierManager.getModifiers(card, PerishableStickerModifier.MODIFIER_ID).get(0)).tickRemainingTurns()) {
                            purgedCards.add(card);

                        }
                    }
                }

                // Remove perishable cards from masterdeck
                for (AbstractCard card : purgedCards) {
                    AbstractDungeon.player.masterDeck.removeCard(card);
                    AbstractDungeon.topLevelEffectsQueue.add(new PurgeCardEffect(card, Settings.WIDTH / 2f, Settings.HEIGHT / 2f));
                }

                AbstractDungeon.topLevelEffectsQueue.add(new RentalStickerEffect(stickers));
            }
        }
    }

}
