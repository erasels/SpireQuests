package spireQuests.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.ui.panels.PotionPopUp;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import javassist.CtBehavior;
import spireQuests.quests.Trigger;

public class QuestTriggers {
    public static final Trigger<Void> DECK_CHANGE = new Trigger<>();
    public static final Trigger<AbstractCard> REMOVE_CARD = new Trigger<>();
    public static final Trigger<AbstractCard> ADD_CARD = new Trigger<>();

    public static final Trigger<MapRoomNode> ENTER_ROOM = new Trigger<>();
    public static final Trigger<MapRoomNode> LEAVE_ROOM = new Trigger<>();

    public static final Trigger<AbstractCard> PLAY_CARD = new Trigger<>();
    public static final Trigger<Integer> DAMAGE_TAKEN = new Trigger<>();
    public static final Trigger<Void> TURN_START = new Trigger<>();
    public static final Trigger<Void> VICTORY = new Trigger<>();
    public static final Trigger<AbstractPotion> USE_POTION = new Trigger<>();

    public static final Trigger<Void> IMPENDING_DAY_KILL = new Trigger<>();

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

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "nextRoomTransition",
            paramtypez = { SaveFile.class }
    )
    public static class OnEnterRoom {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void onEnterRoom(AbstractDungeon __instance, SaveFile file) {
            if (!disabled() && AbstractDungeon.currMapNode != null) {
                LEAVE_ROOM.trigger(AbstractDungeon.currMapNode);
            }
            
            if (!disabled() && AbstractDungeon.nextRoom != null) {
                ENTER_ROOM.trigger(AbstractDungeon.nextRoom);
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "relics");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch2(
            clz = UseCardAction.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = { AbstractCard.class, AbstractCreature.class }
    )
    public static class OnPlayCard {
        @SpirePostfixPatch
        public static void onPlayPatch(AbstractCard card) {
            if (disabled()) return;

            PLAY_CARD.trigger(card);
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "damage")
    public static class OnTakeDamage {
        @SpireInsertPatch(locator = DamageLocator.class, localvars = {"damageAmount"})
        public static void onDamage(AbstractPlayer __instance, DamageInfo info, int damageAmount) {
            if (disabled()) return;

            DAMAGE_TAKEN.trigger(damageAmount);
        }

        private static class DamageLocator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractDungeon.class, "effectList");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = GameActionManager.class, method = "getNextAction")
    @SpirePatch2(clz = AbstractRoom.class, method = "update")
    public static class OnTurnStart {
        @SpireInsertPatch(locator = Locator.class)
        public static void turnStartPatch() {
            if (disabled()) return;

            TURN_START.trigger();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "applyStartOfTurnRelics");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = AbstractPlayer.class, method = "onVictory")
    public static class OnVictory {
        @SpirePrefixPatch
        public static void victoryPatch() {
            if (disabled()) return;

            VICTORY.trigger();
        }
    }

    @SpirePatch2(clz= PotionPopUp.class, method = "updateInput")
    @SpirePatch2(clz= PotionPopUp.class, method = "updateTargetMode")
    public static class PotionUse {
        @SpireInsertPatch(locator = DestroyPotionLocator.class, localvars={"potion"})
        public static void generalPotionPatch(AbstractPotion potion) {
            USE_POTION.trigger(potion);
        }
    }

    @SpirePatch(clz=AbstractPlayer.class, method="damage")
    public static class FairyPotionUse {
        @SpireInsertPatch(locator=DestroyPotionLocator.class, localvars={"p"})
        public static void fairyPotPatch(AbstractPotion p) {
            USE_POTION.trigger(p);
        }
    }

    private static class DestroyPotionLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(TopPanel.class, "destroyPotion");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
