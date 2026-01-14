package spireQuests.quests.indi_keurodz.BossBlinds;

import basemod.BaseMod;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.patches.ShowMarkedNodesOnMapPatch;
import spireQuests.quests.indi_keurodz.BalatroQuest;
import spireQuests.util.Wiz;

import java.util.ArrayList;

import static spireQuests.Anniv8Mod.makeID;

public class TheOx {

    // Track number of plays within each card
    @SpirePatch(clz = AbstractCard.class, method = SpirePatch.CLASS)
    public static class PlayCountField {
        public static SpireField<Integer> playCount = new SpireField<>(() -> 0);
    }

    public static void addSaveFields() {
        BaseMod.addSaveField(makeID("OxPlayCounts"), new CustomSavable<ArrayList<Integer>>() {
            @Override
            public ArrayList<Integer> onSave() {
                ArrayList<Integer> playCounts = new ArrayList<>();
                if (AbstractDungeon.player == null || AbstractDungeon.player.masterDeck == null) return playCounts;

                ArrayList<AbstractCard> deck = AbstractDungeon.player.masterDeck.group;
                for (int i = 0; i < deck.size(); i++) {
                    AbstractCard card = deck.get(i);
                    Integer count = PlayCountField.playCount.get(card);
                    int playCountValue = (count != null) ? count : 0;

                    playCounts.add(playCountValue);
                    System.out.println("SAVING: [" + i + "] " + card.name + " = " + playCountValue + " plays");
                }

                return playCounts;
            }

            @Override
            public void onLoad(ArrayList<Integer> loadedCounts) {
                if (loadedCounts == null || AbstractDungeon.player == null || AbstractDungeon.player.masterDeck == null) return;

                ArrayList<AbstractCard> deck = AbstractDungeon.player.masterDeck.group;
                int cardsRestored = 0;

                for (int i = 0; i < Math.min(deck.size(), loadedCounts.size()); i++) {
                    AbstractCard card = deck.get(i);
                    Integer count = loadedCounts.get(i);
                    PlayCountField.playCount.set(card, count);
                    System.out.println("LOADED: [" + i + "] " + card.name + " = " + count + " plays");
                    cardsRestored++;
                }
            }
        });
    }

    @SpirePatch2(clz = AbstractPlayer.class, method = "useCard")
    public static class TrackCardPlays {
        @SpirePostfixPatch
        public static void TrackAndDeductPatch(AbstractPlayer __instance, AbstractCard c) {
            AbstractCard masterCard = findMasterDeckCard(c);

            if (masterCard != null) {
                Integer currentCount = PlayCountField.playCount.get(masterCard);
                int newCount = (currentCount != null ? currentCount : 0) + 1;
                PlayCountField.playCount.set(masterCard, newCount);

                System.out.println("Card Played: " + masterCard.name + " = " + newCount);
            }

            if(ShowMarkedNodesOnMapPatch.ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.id, BalatroQuest.BossBlind.Ox.frames)) {
                if (isMostPlayedCard(c) && Wiz.p().gold > 0) {
                    Wiz.p().loseGold(Wiz.p().gold);
                }
            }
        }
    }

    private static AbstractCard findMasterDeckCard(AbstractCard playedCard) {
        if (AbstractDungeon.player == null || AbstractDungeon.player.masterDeck == null) return null;

        ArrayList<AbstractCard> deck = AbstractDungeon.player.masterDeck.group;

        for (AbstractCard card : deck) {
            if (card.uuid.equals(playedCard.uuid)) {
                return card;
            }
        }

        return null;
    }

    // A nice to have helper
    public static AbstractCard getMostPlayedCard() {
        if (AbstractDungeon.player == null || AbstractDungeon.player.masterDeck == null) return null;

        AbstractCard mostPlayed = null;
        int maxPlays = 0;

        for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
            Integer count = PlayCountField.playCount.get(card);
            int plays = (count != null) ? count : 0;

            if (plays > maxPlays) {
                maxPlays = plays;
                mostPlayed = card;
            }
        }

        return mostPlayed;
    }

    public static boolean isMostPlayedCard(AbstractCard card) {
        AbstractCard mostPlayed = getMostPlayedCard();
        if (mostPlayed == null) return false;

        AbstractCard masterCard = findMasterDeckCard(card);
        if (masterCard == null) return false;

        return masterCard.uuid.equals(mostPlayed.uuid);
    }

    @SpirePatch2(clz = AbstractCard.class, method = "update")
    public static class UpdateOxGlow {
        @SpirePostfixPatch
        public static void UpdateGlow(AbstractCard __instance) {
            if(!ShowMarkedNodesOnMapPatch.ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.id, BalatroQuest.BossBlind.Ox.frames)) {
                return;
            }

            if (isMostPlayedCard(__instance)) {
                __instance.glowColor = Color.PINK.cpy();
                __instance.triggerOnGlowCheck();
            }
        }
    }
}