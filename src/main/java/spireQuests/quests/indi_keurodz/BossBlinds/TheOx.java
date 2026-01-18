package spireQuests.quests.indi_keurodz.BossBlinds;

import basemod.BaseMod;
import basemod.abstracts.CustomSavable;
import basemod.helpers.TooltipInfo;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.mod.stslib.StSLib;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
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
                if (AbstractDungeon.player == null || AbstractDungeon.player.masterDeck == null)
                    return playCounts;

                ArrayList<AbstractCard> deck = AbstractDungeon.player.masterDeck.group;
                for (int i = 0; i < deck.size(); i++) {
                    AbstractCard card = deck.get(i);
                    Integer count = PlayCountField.playCount.get(card);
                    int playCountValue = (count != null) ? count : 0;

                    playCounts.add(playCountValue);
                }

                return playCounts;
            }

            @Override
            public void onLoad(ArrayList<Integer> loadedCounts) {
                if (loadedCounts == null || AbstractDungeon.player == null || AbstractDungeon.player.masterDeck == null)
                    return;

                ArrayList<AbstractCard> deck = AbstractDungeon.player.masterDeck.group;

                for (int i = 0; i < Math.min(deck.size(), loadedCounts.size()); i++) {
                    AbstractCard card = deck.get(i);
                    Integer count = loadedCounts.get(i);
                    PlayCountField.playCount.set(card, count);
                }
            }
        });
    }

    @SpirePatch2(clz = AbstractPlayer.class, method = "useCard")
    public static class TrackCardPlays {
        @SpirePostfixPatch
        public static void TrackAndDeductPatch(AbstractPlayer __instance, AbstractCard c) {
            AbstractCard masterCard = StSLib.getMasterDeckEquivalent(c);

            if (masterCard != null) {
                Integer currentCount = PlayCountField.playCount.get(masterCard);
                int newCount = (currentCount != null ? currentCount : 0) + 1;
                PlayCountField.playCount.set(masterCard, newCount);
            }

            if (ShowMarkedNodesOnMapPatch.ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.ID,
                    BalatroQuest.BossBlind.Ox.frames)) {
                if (mostPlayedCard != null && c.uuid == mostPlayedCard.uuid && Wiz.p().gold > 0) {
                    Wiz.p().loseGold(Wiz.p().gold);
                }
            }
        }
    }

    // A nice to have helper
    public static AbstractCard getMostPlayedCard() {
        if (AbstractDungeon.player == null || AbstractDungeon.player.masterDeck == null)
            return null;

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

    @SpirePatch2(clz = AbstractCard.class, method = "update")
    public static class UpdateOxGlow {
        @SpirePostfixPatch
        public static void UpdateGlow(AbstractCard __instance) {
            if (!ShowMarkedNodesOnMapPatch.ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.ID,
                    BalatroQuest.BossBlind.Ox.frames)) {
                return;
            }

            // Only apply glow to most played card at the start of combat
            if (mostPlayedCard != null && __instance.uuid == mostPlayedCard.uuid) {
                __instance.glowColor = Color.RED.cpy();
                __instance.triggerOnGlowCheck();
            }
        }
    }

    private static AbstractCard mostPlayedCard;

    @SpirePatch2(clz = AbstractPlayer.class, method = "applyStartOfCombatPreDrawLogic")
    public static class SetMostPlayedOnCombatStart {
        @SpirePostfixPatch
        public static void SetMostPlayedCard() {
            if (!ShowMarkedNodesOnMapPatch.ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.ID,
                    BalatroQuest.BossBlind.Ox.frames))
                return;
            mostPlayedCard = getMostPlayedCard();
        }
    }

    public static void updateOxTooltip() {
        String newDescription = getTooltipDescription();

        if (getMostPlayedCard() == null) return;
        BalatroQuest.BossBlind.Ox.tooltip.description = newDescription;
    }

    @SpirePatch2(clz = MapRoomNode.class, method = "update")
    public static class UpdateOxTooltip {
        @SpirePostfixPatch
        public static void UpdateTooltip(MapRoomNode __instance) {
            if (ShowMarkedNodesOnMapPatch.ImageField.CheckMarks(__instance, BalatroQuest.ID,
                    BalatroQuest.BossBlind.Ox.frames)) {
                updateOxTooltip();
            }
        }
    }

    public static String getTooltipDescription() {
        String baseDescription = CardCrawlGame.languagePack
                .getUIString(BalatroQuest.BLIND_STRINGS_ID).TEXT_DICT.get("Ox_Description");
        AbstractCard mostPlayed = getMostPlayedCard();

        if (mostPlayed != null) {
            String extraInfo = String.format(
                    CardCrawlGame.languagePack.getUIString(BalatroQuest.BLIND_STRINGS_ID).TEXT_DICT.get("Ox_Extra"),
                    mostPlayed.name);
            return baseDescription + " NL NL " + extraInfo;
        }

        return baseDescription;
    }

}
