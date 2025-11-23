package spireQuests.quests.indi_keurodz.Patches;

import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.Anniv8Mod;
import spireQuests.quests.QuestManager;
import spireQuests.quests.indi_keurodz.RentalStickerModifier;

import static spireQuests.patches.QuestTriggers.*;

public class BattleEndPatch {
    @SpirePatch2(clz = AbstractPlayer.class, method = "onVictory")
    public static class OnCombatEndOrVictory {
        @SpirePrefixPatch
        public static void combatEndOrVictoryPatch() {
            if (!AbstractDungeon.getCurrRoom().smoked) {
                for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {

                    if (CardModifierManager.hasModifier(card, RentalStickerModifier.MODIFIER_ID)) {
                        AbstractDungeon.player.loseGold(3);
                        CardCrawlGame.sound.play(RentalStickerModifier.MODIFIER_ID);
                    }

                }
            }
        }
    }
}
