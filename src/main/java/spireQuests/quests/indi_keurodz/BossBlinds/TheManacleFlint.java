package spireQuests.quests.indi_keurodz.BossBlinds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;

import basemod.Pair;
import javassist.CtBehavior;
import spireQuests.patches.ShowMarkedNodesOnMapPatch.ImageField;
import spireQuests.quests.QuestManager;
import spireQuests.quests.indi_keurodz.BalatroQuest;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;

public class TheManacleFlint {
    static ArrayList<Pair<Integer, AbstractRelic>> disabledRelics = new ArrayList<>();
    static int energyMasterToAdd;
    static int masterHandSizeToAdd;

    @SpirePatch2(clz = AbstractPlayer.class, method = "preBattlePrep")
    public static class preBattle {

        @SpirePrefixPatch
        public static void BeforehandSizeSet() {
            BalatroQuest q = (BalatroQuest) QuestManager.quests().stream()
                    .filter(quest -> BalatroQuest.ID.equals(quest.id) && !quest.isCompleted())
                    .findAny()
                    .orElse(null);
            if (q == null) {
                return;
            }
            if (ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.ID, BossBlind.Flint.frames)) {
                int masterHandSize = AbstractDungeon.player.masterHandSize;
                int energyMaster = AbstractDungeon.player.energy.energyMaster;
                ArrayList<Integer> indices = new ArrayList<>(AbstractDungeon.player.relics.size());
                for (int i = 0; i < AbstractDungeon.player.relics.size(); i++) {
                    indices.add(i);
                }
                Collections.shuffle(indices, new Random(AbstractDungeon.cardRandomRng.randomLong()));

                for (int i = 0; i < AbstractDungeon.player.relics.size() / 2; i++) {
                    disabledRelics.add(new Pair<>(indices.get(i), AbstractDungeon.player.relics.get(indices.get(i))));
                    AbstractDungeon.player.relics.get(indices.get(i)).onUnequip();
                    AbstractDungeon.player.relics.set(indices.get(i), new Circlet());
                }

                energyMasterToAdd = energyMaster - AbstractDungeon.player.energy.energyMaster;
                masterHandSizeToAdd = masterHandSize - AbstractDungeon.player.masterHandSize;

            }
        }

        @SpireInsertPatch(locator = Locator.class)
        public static void AfterhandSizeSet() {
            BalatroQuest q = (BalatroQuest) QuestManager.quests().stream()
                    .filter(quest -> BalatroQuest.ID.equals(quest.id) && quest.isCompleted())
                    .findAny()
                    .orElse(null);
            if (q == null) {
                return;
            }
            if (ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.ID, BossBlind.Manacle.frames)) {
                AbstractDungeon.player.gameHandSize--;
            }

        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "isDraggingCard");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = AbstractPlayer.class, method = "onVictory")
    public static class EndOfBattle {
        @SpirePrefixPatch
        public static void End() {
            if (disabledRelics.isEmpty()) {
                return;
            }
            for (Pair<Integer, AbstractRelic> pair : disabledRelics) {
                AbstractDungeon.player.relics.set(pair.getKey(), pair.getValue());
                AbstractDungeon.player.reorganizeRelics();
                AbstractDungeon.player.energy.energyMaster += energyMasterToAdd;
                AbstractDungeon.player.masterHandSize += masterHandSizeToAdd;
            }
            disabledRelics = new ArrayList<>();
        }

    }

}
