package spireQuests.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.quests.Trigger;

public class QuestTriggers {
    public static final Trigger<Void> DECK_CHANGE = new Trigger<>();
    public static final Trigger<AbstractCard> REMOVE_CARD = new Trigger<>();
    public static final Trigger<AbstractCard> ADD_CARD = new Trigger<>();

    private static boolean disabled() {
        return CardCrawlGame.mode != CardCrawlGame.GameMode.GAMEPLAY;
    }

    @SpirePatch(
            clz = CardGroup.class,
            method = "removeCard",
            paramtypez = { AbstractCard.class }
    )
    public static class OnRemoveCard {
        @SpireInsertPatch(
                rloc=2
        )
        public static void OnRemove(CardGroup __instance, AbstractCard c) {
            if (disabled()) return;

            DECK_CHANGE.trigger();
            REMOVE_CARD.trigger(c);
        }
    }

    @SpirePatch(
            clz = CardGroup.class,
            method = "addToTop"
    )
    public static class OnAddCard {
        @SpirePostfixPatch
        public static void OnAdd(CardGroup __instance, AbstractCard c) {
            if (disabled()) return;

            if (__instance.type == CardGroup.CardGroupType.MASTER_DECK) {
                DECK_CHANGE.trigger();
                ADD_CARD.trigger(c);
            }
        }
    }


}
