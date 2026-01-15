package spireQuests.patches;

import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.screens.MasterDeckViewScreen;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import spireQuests.actions.ShowTempCardInDrawPileAction;
import spireQuests.cardmods.QuestboundMod;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;
import spireQuests.util.Wiz;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static spireQuests.Anniv8Mod.questboundEnabled;

public class QuestboundModPatch {
    private static Stream<AbstractQuest> getQuestbound() {
        return QuestManager.quests().stream().filter(q -> q.questboundCards != null && !q.isCompleted() && !q.isFailed());
    }

    @SpirePatch2(clz = AbstractPlayer.class, method = SpirePatch.CLASS)
    public static class QuestboundCardsToShowField {
        public static SpireField<List<AbstractCard>> cards = new SpireField<>(ArrayList::new);
    }

    @SpirePatch2(clz = CardGroup.class, method = "renderMasterDeck")
    public static class renderQuestboundCards {
        @SpirePrefixPatch
        public static void render(SpriteBatch sb) {
            if (!questboundEnabled()) return;
            getQuestbound().forEach(q ->
                    q.questboundCards.forEach(c -> c.render(sb))
            );
        }
    }

    @SpirePatch2(clz = CardGroup.class, method = "renderMasterDeckExceptOneCard")
    public static class renderQuestboundCardsExceptOneCard {
        @SpirePrefixPatch
        public static void render(SpriteBatch sb, AbstractCard card) {
            if (!questboundEnabled()) return;
            getQuestbound().forEach(q ->
                    q.questboundCards.stream().filter(c -> c != card).forEach(c -> c.render(sb))
            );
        }
    }

    @SpirePatch2(clz = MasterDeckViewScreen.class, method = "updatePositions")
    public static class updateQuestboundCardPositions {
        @SpireInsertPatch(rloc = 3, localvars = {"cards"})
        public static void render(ArrayList<AbstractCard> cards) {
            if (!questboundEnabled()) return;
            getQuestbound().forEach(q ->
                    cards.addAll(0, q.questboundCards)
            );
        }

        @SpirePostfixPatch
        public static void remove() {
            if (!questboundEnabled()) return;
            getQuestbound().forEach(q ->
                    Wiz.adp().masterDeck.group.removeAll(q.questboundCards)
            );
        }
    }

    @SpirePatch2(clz = MasterDeckViewScreen.class, method = "hideCards")
    public static class hideQuestboundCards {
        @SpireInsertPatch(rloc = 2, localvars = {"cards"})
        public static void render(ArrayList<AbstractCard> cards) {
            if (!questboundEnabled()) return;
            getQuestbound().forEach(q ->
                    cards.addAll(0, q.questboundCards)
            );
        }

        @SpirePostfixPatch
        public static void remove() {
            if (!questboundEnabled()) return;
            getQuestbound().forEach(q ->
                    Wiz.adp().masterDeck.group.removeAll(q.questboundCards)
            );
        }
    }

    @SpirePatch2(clz = MasterDeckViewScreen.class, method = "render")
    public static class renderQuestboundTooltips {
        @SpireInsertPatch(locator = Locator.class)
        public static void render(SpriteBatch sb) {
            if (!questboundEnabled()) return;
            getQuestbound().forEach(q ->
                    q.questboundCards.forEach(c -> c.renderCardTip(sb))
            );
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(FontHelper.class, "renderDeckViewTip");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = CardGroup.class, method = "initializeDeck")
    public static class initializeQuestboundCards {
        @SpireInsertPatch(locator = Locator.class, localvars = {"copy"})
        public static void initialize(CardGroup copy) {
            getQuestbound().forEach(q -> {
                List<AbstractCard> overrides = q.overrideQuestboundCards();
                if (overrides != null) {
                    overrides = overrides.stream().map(AbstractCard::makeSameInstanceOf).collect(Collectors.toList());
                    overrides.forEach(c -> {
                        CardModifierManager.addModifier(c, new QuestboundMod(q));
                        copy.group.add(c);
                    });
                    QuestboundCardsToShowField.cards.get(AbstractDungeon.player).addAll(overrides);
                }
                else {
                    List<AbstractCard> questboundCards = q.questboundCards.stream().map(AbstractCard::makeSameInstanceOf).collect(Collectors.toList());
                    copy.group.addAll(questboundCards);
                    if (!questboundEnabled()) {
                        QuestboundCardsToShowField.cards.get(AbstractDungeon.player).addAll(questboundCards);
                    }
                }
            });
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(CardGroup.class, "shuffle");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = TopPanel.class, method = "renderTopRightIcons")
    public static class questboundCountTopPanel {
        @SpireInsertPatch(locator = Locator.class)
        public static void render(SpriteBatch sb, float ___DECK_X, float ___ICON_Y) {
            AtomicInteger i = new AtomicInteger(0);
            getQuestbound().forEach(q -> i.addAndGet(q.questboundCards.size()));
            if (i.get() > 0 && questboundEnabled())
                FontHelper.renderFontRightTopAligned(sb, FontHelper.topPanelAmountFont,
                        Integer.toString(i.get()),
                        ___DECK_X + 58.0F * Settings.scale,
                        ___ICON_Y + 50.0F * Settings.scale,
                        Settings.GOLD_COLOR.cpy());
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(FontHelper.class, "renderFontRightTopAligned");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = CardCrawlGame.class, method = "loadPlayerSave")
    public static class loadQuestboundCards {
        @SpirePostfixPatch
        public static void loadPlayerSave() {
            for (AbstractQuest q : QuestManager.currentQuests.get(AbstractDungeon.player)) {
                if (q.questboundCards != null) {
                    q.questboundCards.clear();
                }
            }
            for (Iterator<AbstractCard> iterator = Wiz.adp().masterDeck.group.iterator(); iterator.hasNext(); ) {
                AbstractCard c = iterator.next();
                if (CardModifierManager.hasModifier(c, QuestboundMod.ID)) {
                    QuestboundMod mod = (QuestboundMod) CardModifierManager.getModifiers(c, QuestboundMod.ID).get(0);
                    AbstractQuest quest = QuestManager.currentQuests.get(AbstractDungeon.player).get(mod.boundQuestIndex);
                    mod.boundQuest = quest;
                    quest.questboundCards.add(c);
                    iterator.remove();
                }
            }
        }
    }

    @SpirePatch2(clz = SaveFile.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {SaveFile.SaveType.class})
    public static class saveQuestboundCards {
        @SpirePrefixPatch
        public static void savefile() {
            getQuestbound().flatMap(q -> q.questboundCards.stream()).forEach(c -> {
                QuestboundMod mod = (QuestboundMod) CardModifierManager.getModifiers(c, QuestboundMod.ID).get(0);
                if (mod != null) {
                    mod.boundQuestIndex = QuestManager.currentQuests.get(AbstractDungeon.player).indexOf(mod.boundQuest);
                }
                AbstractDungeon.player.masterDeck.group.add(0, c);
            });
        }

        // yes this needs to be an insert patch. It will break violently if it isn't.
        @SpireInsertPatch(locator = Locator.class)
        public static void remove() {
            getQuestbound().forEach(q ->
                    Wiz.adp().masterDeck.group.removeAll(q.questboundCards)
            );
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(CardHelper.class, "obtainedCards");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = AbstractRoom.class, method = "update")
    public static class applyStartOfCombatPreDrawLogic {
        @SpireInsertPatch(locator = Locator.class)
        public static void update() {
            List<AbstractCard> questboundCards = QuestboundCardsToShowField.cards.get(AbstractDungeon.player);
            questboundCards.forEach(c -> Wiz.atb(new ShowTempCardInDrawPileAction(c, true)));
            questboundCards.clear();
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "applyStartOfCombatPreDrawLogic");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
