package spireQuests.quests.indi_keurodz.patches;

import java.util.ArrayList;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;

import basemod.helpers.CardModifierManager;
import spireQuests.quests.indi_keurodz.effects.RentalStickerEffect;
import spireQuests.quests.indi_keurodz.modifiers.PerishableModifier;
import spireQuests.quests.indi_keurodz.modifiers.RentalModifier;

public class BattleEndPatch {
    @SpirePatch2(clz = AbstractPlayer.class, method = "onVictory")
    public static class OnCombatEndOrVictory {
        @SpirePrefixPatch
        public static void combatEndOrVictoryPatch() {
            if (!AbstractDungeon.getCurrRoom().smoked) {
                int stickers = 0;
                ArrayList<AbstractCard> purgedCards = new ArrayList<>();
                for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
                    if (CardModifierManager.hasModifier(card, RentalModifier.ID)) {
                        stickers++;
                    }
                    if (CardModifierManager.hasModifier(card, PerishableModifier.ID)) {
                        // Assumes we can only have one of this modifier on a card
                        if (((PerishableModifier) CardModifierManager
                                .getModifiers(card, PerishableModifier.ID).get(0))
                                .tickRemainingTurns()) {
                            purgedCards.add(card);

                        }
                    }
                }

                // Remove perishable cards from masterdeck
                for (AbstractCard card : purgedCards) {
                    AbstractDungeon.player.masterDeck.removeCard(card);
                    AbstractDungeon.topLevelEffects
                            .add(new PurgeCardEffect(card, Settings.WIDTH / 2f, Settings.HEIGHT / 2f));
                }

                AbstractDungeon.topLevelEffectsQueue.add(new RentalStickerEffect(stickers));
            }
        }
    }

}
