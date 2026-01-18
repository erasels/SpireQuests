package spireQuests.quests.indi_keurodz.BossBlinds;

import java.util.function.Predicate;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import basemod.helpers.CardModifierManager;
import spireQuests.patches.ShowMarkedNodesOnMapPatch.ImageField;
import spireQuests.quests.indi_keurodz.BalatroQuest;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;
import spireQuests.quests.indi_keurodz.modifiers.DebuffedModifier;

public class ThePlantHeadClubGoadWindow {

    @SpirePatch2(clz = AbstractPlayer.class, method = "onCardDrawOrDiscard")
    public static class OnDrawCardPatch {
        @SpirePrefixPatch
        public static void onDrawCard() {

            if (ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.ID, BossBlind.Plant.frames)) {
                applyDebuffsToHand(card -> card.type == AbstractCard.CardType.POWER);

            } else if (ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.ID, BossBlind.Head.frames)) {
                applyDebuffsToHand(card -> card.rarity == AbstractCard.CardRarity.UNCOMMON);

            } else if (ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.ID, BossBlind.Club.frames)) {
                applyDebuffsToHand(card -> card.rarity == AbstractCard.CardRarity.BASIC);

            } else if (ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.ID, BossBlind.Goad.frames)) {
                applyDebuffsToHand(card -> card.rarity == AbstractCard.CardRarity.COMMON);

            } else if (ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.ID, BossBlind.Window.frames)) {
                applyDebuffsToHand(card -> card.rarity == AbstractCard.CardRarity.RARE);

            }

        }

        private static void applyDebuffsToHand(Predicate<AbstractCard> filter) {
            for (AbstractCard card : AbstractDungeon.player.hand.group) {
                if (filter.test(card)) {
                    CardModifierManager.addModifier(card, new DebuffedModifier());
                }
            }
        }

    }

    @SpirePatch2(clz = AbstractPlayer.class, method = "applyStartOfTurnPostDrawRelics")
    public static class onBattleStart {

        @SpirePrefixPatch()
        public static void Insert() {
            if (ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.ID, BossBlind.Plant.frames)) {
                applyDebuffsToExistingCards(card -> card.type == AbstractCard.CardType.POWER);

            } else if (ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.ID, BossBlind.Head.frames)) {
                applyDebuffsToExistingCards(card -> card.rarity == AbstractCard.CardRarity.UNCOMMON);

            } else if (ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.ID, BossBlind.Club.frames)) {
                applyDebuffsToExistingCards(card -> card.rarity == AbstractCard.CardRarity.BASIC);

            } else if (ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.ID, BossBlind.Goad.frames)) {
                applyDebuffsToExistingCards(card -> card.rarity == AbstractCard.CardRarity.COMMON);

            } else if (ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.ID, BossBlind.Window.frames)) {
                applyDebuffsToExistingCards(card -> card.rarity == AbstractCard.CardRarity.RARE);

            }

        }

        private static void applyDebuffsToExistingCards(Predicate<AbstractCard> filter) {
            for (AbstractCard card : AbstractDungeon.player.hand.group) {
                if (filter.test(card)) {
                    CardModifierManager.addModifier(card, new DebuffedModifier());
                }
            }
            for (AbstractCard card : AbstractDungeon.player.discardPile.group) {
                if (filter.test(card)) {
                    CardModifierManager.addModifier(card, new DebuffedModifier());
                }
            }
            for (AbstractCard card : AbstractDungeon.player.drawPile.group) {
                if (filter.test(card)) {
                    CardModifierManager.addModifier(card, new DebuffedModifier());
                }
            }
            for (AbstractCard card : AbstractDungeon.player.exhaustPile.group) {
                if (filter.test(card)) {
                    CardModifierManager.addModifier(card, new DebuffedModifier());
                }
            }
        }

    }

}
