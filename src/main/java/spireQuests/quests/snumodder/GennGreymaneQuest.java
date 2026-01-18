package spireQuests.quests.snumodder;


import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.AscendersBane;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.relics.PrismaticShard;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import javassist.CtBehavior;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class GennGreymaneQuest extends AbstractQuest {
    private static final int QUEST_EVEN = 7;

    public GennGreymaneQuest() {
        super(QuestType.LONG, QuestDifficulty.CHALLENGE);
        new TriggeredUpdateTracker<Integer, Void>(QuestTriggers.DECK_CHANGE, 0, 1, () -> getEvenCards() >= QUEST_EVEN ? 1 : 0){
            @Override
            public String progressString() {
                return String.format(
                        " (%d/%d)",
                        getEvenCards(), QUEST_EVEN
                );
            }
        }.add(this);
        new TriggeredUpdateTracker<Integer, Void>(QuestTriggers.DECK_CHANGE, 1, 1, ()-> getOddCards() > 0 ? 0 : 1){
            @Override
            public String progressString() {
                return String.format(
                        " (%d/%d)",
                        getOddCards(), getOddCards()
                );
            }
        }.add(this);

        useDefaultReward = false;
    }

    private int getEvenCards() {
        int cards = 0;
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (!Objects.equals(c.cardID, AscendersBane.ID) && c.rarity != AbstractCard.CardRarity.BASIC && c.cost % 2 != 1) {
                cards += 1;
            }
        }
        return cards;
    }

    public int getOddCards() {
        int cards = 0;
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (!Objects.equals(c.cardID, AscendersBane.ID) && c.rarity != AbstractCard.CardRarity.BASIC && c.cost % 2 == 1) {
                cards += 1;
            }
        }
        return cards;
    }

    @Override
    public void onComplete() {
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (!(c.rarity == AbstractCard.CardRarity.BASIC) || !c.canUpgrade()) continue;
            c.upgrade();
            AbstractDungeon.player.bottledCardUpgradeCheck(c);
            float x = MathUtils.random(0.1f, 0.9f) * (float) Settings.WIDTH;
            float y = MathUtils.random(0.2f, 0.8f) * (float)Settings.HEIGHT;
            AbstractDungeon.topLevelEffectsQueue.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy(), x, y));
            AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect(x, y));// 102
        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "getRewardCards"
    )
    public static class EvenCostRewardPatchFix {
        @SpireInsertPatch(locator = Locator.class, localvars = { "retVal" })
        public static void InsertFix(ArrayList<AbstractCard> retVal) {
            GennGreymaneQuest gennQuest = null;
            for (AbstractQuest q : QuestManager.quests()) {
                if (q instanceof GennGreymaneQuest) {
                    gennQuest = (GennGreymaneQuest)q;
                }
            }
            if (gennQuest == null || gennQuest.isCompleted() || gennQuest.isFailed() || gennQuest.getOddCards() > 0) return;

            if (retVal == null || retVal.isEmpty()) return;
            for (AbstractCard c : retVal) {
                if (isEven(c)) {
                    return;
                }
            }

            ArrayList<Integer> validIndices = new ArrayList<>();
            for (int i = 0; i < retVal.size(); i++) {
                if (retVal.get(i).rarity == AbstractCard.CardRarity.COMMON
                        || retVal.get(i).rarity == AbstractCard.CardRarity.UNCOMMON
                        || retVal.get(i).rarity == AbstractCard.CardRarity.RARE) {
                    validIndices.add(i);
                }
            }
            if (validIndices.isEmpty()) return;

            int replaceIndex = validIndices.get(
                    AbstractQuest.rng.random(validIndices.size() - 1)
            );
            AbstractCard replacement = rollEven(retVal.get(replaceIndex).rarity, retVal);
            if (replacement != null) {
                retVal.set(replaceIndex, replacement);
            }
        }

        private static boolean isEven(AbstractCard c) {
            return c.cost % 2 != 1;
        }

        private static AbstractCard rollEven(
                AbstractCard.CardRarity rarity,
                ArrayList<AbstractCard> list
        ) {
            for (int i = 0; i < 50; i++) {
                AbstractCard c =
                        AbstractDungeon.player.hasRelic(PrismaticShard.ID)
                                ? CardLibrary.getAnyColorCard(rarity)
                                : AbstractDungeon.getCard(rarity);

                if (c == null) continue;
                if (!isEven(c)) continue;
                if (c.canUpgrade()) {
                    AbstractCard copy = c.makeCopy();
                    copy.upgrade();
                    if (!isEven(copy)) continue;
                }
                for (AbstractCard e : list) {
                    if (e.cardID.equals(c.cardID)) {
                        c = null;
                        break;
                    }
                }
                if (c == null) continue;
                return c;
            }
            return null;
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.NewExprMatcher(ArrayList.class);
                return LineFinder.findInOrder(ctMethodToPatch, Collections.singletonList(finalMatcher), finalMatcher);
            }
        }
    }
}
