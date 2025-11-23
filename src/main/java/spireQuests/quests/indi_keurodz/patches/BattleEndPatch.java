package spireQuests.quests.indi_keurodz.patches;

import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.quests.indi_keurodz.RentalStickerModifier;
import spireQuests.quests.indi_keurodz.RentalStickerEffect;

public class BattleEndPatch {
    @SpirePatch2(clz = AbstractPlayer.class, method = "onVictory")
    public static class OnCombatEndOrVictory {
        @SpirePrefixPatch
        public static void combatEndOrVictoryPatch() {
            if (!AbstractDungeon.getCurrRoom().smoked) {
                int stickers = 0;
                for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
                    if (CardModifierManager.hasModifier(card, RentalStickerModifier.MODIFIER_ID)) {
                        stickers++;
                    }
                }
                AbstractDungeon.topLevelEffectsQueue.add(new RentalStickerEffect(stickers));
            }
        }
    }

}
