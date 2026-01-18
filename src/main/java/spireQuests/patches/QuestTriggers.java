package spireQuests.patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
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
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Boot;
import com.megacrit.cardcrawl.relics.Ectoplasm;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rewards.chests.AbstractChest;
import com.megacrit.cardcrawl.rewards.chests.BossChest;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import com.megacrit.cardcrawl.ui.panels.PotionPopUp;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import com.megacrit.cardcrawl.vfx.campfire.CampfireSmithEffect;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import javassist.CtBehavior;
import spireQuests.quests.Trigger;
import spireQuests.quests.luaviper.actions.EnemyDiesDelayedCheckAction;
import spireQuests.util.Wiz;

import java.util.ArrayList;

public class QuestTriggers {
    public static final Trigger<Void> DECK_CHANGE = new Trigger<>();
    public static final Trigger<AbstractCard> REMOVE_CARD = new Trigger<>();
    public static final Trigger<AbstractCard> ADD_CARD = new Trigger<>();

    public static final Trigger<MapRoomNode> ENTER_ROOM = new Trigger<>();
    public static final Trigger<MapRoomNode> LEAVE_ROOM = new Trigger<>();

    public static final Trigger<AbstractCard> PLAY_CARD = new Trigger<>();
    public static final Trigger<Integer> DAMAGE_TAKEN = new Trigger<>();
    public static final Trigger<Void> BEFORE_COMBAT_START = new Trigger<>();
    public static final Trigger<Void> TURN_START = new Trigger<>();
    public static final Trigger<Void> TURN_END = new Trigger<>();
    public static final Trigger<Void> VICTORY = new Trigger<>(); //Excludes Smoke Bomb and other ways of escaping
    public static final Trigger<Void> COMBAT_END = new Trigger<>();
    public static final Trigger<AbstractPotion> USE_POTION = new Trigger<>();
    public static final Trigger<Integer> POTION_CHANGE = new Trigger<>();

    public static final Trigger<Void> BOOT_TRIGGER = new Trigger<>();
    public static final Trigger<AbstractOrb> CHANNEL_ORB = new Trigger<>();
    public static final Trigger<AbstractOrb> EVOKE_ORB = new Trigger<>();
    public static final Trigger<Integer> BEFORE_ACT_CHANGE = new Trigger<>();
    public static final Trigger<Integer> ACT_CHANGE = new Trigger<>();
    public static final Trigger<AbstractChest> CHEST_OPENED = new Trigger<>(); //NOTE: This includes both normal and boss chests.

    public static final Trigger<Integer> HEALTH_HEALED = new Trigger<>();
    public static final Trigger<Void> MAX_HEALTH_CHANGED = new Trigger<>();
    public static final Trigger<Integer> MAX_HEALTH_INCREASED = new Trigger<>();
    public static final Trigger<Integer> MAX_HEALTH_DECREASED = new Trigger<>();

    public static final Trigger<Integer> GAIN_MONEY = new Trigger<>(); //NOTE: Will not trigger if you have Ectoplasm.
    public static final Trigger<Integer> LOSE_MONEY = new Trigger<>(); //NOTE: This counts all instances of losing money, including events
    public static final Trigger<Integer> MONEY_SPENT_AT_SHOP = new Trigger<>(); //NOTE: This counts only money spent at shop and not money lost through events.

    public static final Trigger<AbstractRelic> OBTAIN_RELIC = new Trigger<>(); //NOTE: This is triggered by both obtain() and instantObtain().
    public static final Trigger<Void> EXACT_KILL = new Trigger<>();
    public static final Trigger<AbstractCard> UPGRADE_CARD_AT_CAMPFIRE = new Trigger<>();
    public static final Trigger<AbstractCard> FATAL_CARD = new Trigger<>(); //NOTE: This is in the DamageAction and only triggers if the source is a card.

    public static final Trigger<Void> NO_STARTER_STRIKES = new Trigger<>();//
    public static final Trigger<AbstractMonster> ENEMY_DIES_DELAYED_CHECK = new Trigger<>();    //NOTE: This does not trigger until after the action queue has processed.
    public static final Trigger<AbstractPotion> DISCARD_POTION = new Trigger<>();
    public static final Trigger<AbstractPotion> SKIP_POTION = new Trigger<>();
    public static final Trigger<AbstractGameAction.AttackEffect> ATTACK_ANIMATION = new Trigger<>();    //NOTE: This specifically checks for AbstractGameAction.AttackEffect animations. Other animations will not trigger this event.
    public static final Trigger<Integer> UNBLOCKED_ATTACK_DAMAGE_TAKEN = new Trigger<>();

    private static boolean disabled() {
        return CardCrawlGame.mode != CardCrawlGame.GameMode.GAMEPLAY;
    }

    @SpirePatch(
            clz = CardGroup.class,
            method = "removeCard",
            paramtypez = {AbstractCard.class}
    )
    public static class OnRemoveCard {
        @SpireInsertPatch(
                rloc = 2
        )
        public static void OnRemove(CardGroup __instance, AbstractCard c) {
            if (disabled()) return;

            DECK_CHANGE.trigger();
            REMOVE_CARD.trigger(c);

            if(AbstractDungeon.player.masterDeck.group.stream().noneMatch(card -> card.tags.contains(AbstractCard.CardTags.STARTER_STRIKE)))
                NO_STARTER_STRIKES.trigger();
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
            paramtypez = {SaveFile.class}
    )
    public static class OnLeaveRoom {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void onLeaveRoom(AbstractDungeon __instance, SaveFile file) {
            if (!disabled() && AbstractDungeon.currMapNode != null) {
                LEAVE_ROOM.trigger(AbstractDungeon.currMapNode);
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractDungeon.class, "incrementFloorBasedMetrics");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "nextRoomTransition",
            paramtypez = {SaveFile.class}
    )
    public static class OnEnterRoom {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void onEnterRoom(AbstractDungeon __instance, SaveFile file) {
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
            paramtypez = {AbstractCard.class, AbstractCreature.class}
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

    @SpirePatch2(clz = AbstractPlayer.class, method = "preBattlePrep")
    public static class BeforeCombatStart {
        @SpirePostfixPatch
        public static void beforeCombatStart() {
            if (disabled()) return;

            BEFORE_COMBAT_START.trigger();
        }
    }

    @SpirePatch2(clz = AbstractPlayer.class, method = "applyStartOfTurnRelics")
    public static class OnTurnStart {
        @SpirePrefixPatch
        public static void turnStartPatch() {
            if (disabled()) return;

            TURN_START.trigger();
        }
    }

    @SpirePatch2(clz = AbstractRoom.class, method = "endTurn")
    public static class OnTurnEnd {
        @SpirePostfixPatch()
        public static void turnEndPatch() {
            if (disabled()) return;

            TURN_END.trigger();
        }
    }

    @SpirePatch2(clz = AbstractPlayer.class, method = "onVictory")
    public static class OnCombatEndOrVictory {
        @SpirePrefixPatch
        public static void combatEndOrVictoryPatch() {
            if (disabled()) return;

            COMBAT_END.trigger();
            if (!AbstractDungeon.getCurrRoom().smoked) {
                VICTORY.trigger();
            }
        }
    }

    @SpirePatch2(clz = AbstractPlayer.class, method = "evokeOrb")
    public static class evokeOrb {
        @SpirePrefixPatch
        public static void evokePatch(AbstractPlayer __instance) {
            if (disabled()) return;

            if (!__instance.orbs.isEmpty() && !(__instance.orbs.get(0) instanceof EmptyOrbSlot)) {
                EVOKE_ORB.trigger(__instance.orbs.get(0));
            }

        }
    }
    
    @SpirePatch2(clz = AbstractPlayer.class, method = "channelOrb")
    public static class channelOrb {
        @SpirePrefixPatch
        public static void channelPatch(AbstractPlayer __instance, AbstractOrb orbToSet) {
            if (disabled()) return;

            if (__instance.maxOrbs > 0){
                CHANNEL_ORB.trigger(orbToSet);
            }
        }
    }

    @SpirePatch2(
            clz = AbstractDungeon.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = { String.class, String.class, AbstractPlayer.class, ArrayList.class }
    )
    public static class BeforeActChange {
        @SpirePostfixPatch
        public static void beforeActChange(){
            if (disabled()) return;
            BEFORE_ACT_CHANGE.trigger(AbstractDungeon.actNum);
        }
    }

    @SpirePatch2(
            clz = AbstractDungeon.class,
            method = "dungeonTransitionSetup"
    )
    public static class dungeonTransitionSetup {
        @SpirePostfixPatch
        public static void dungeonTransitionPostfix(){
            if (disabled()) return;
            ACT_CHANGE.trigger(AbstractDungeon.actNum);
        }
    }

    @SpirePatch2(
            clz = AbstractChest.class,
            method = "open",
            paramtypez = {boolean.class}
    )

    public static class Open{
        @SpirePostfixPatch
        public static void OpenChestPostfix(AbstractChest __instance, boolean bossChest){
            if (disabled()) return;
            CHEST_OPENED.trigger(__instance);
        }
    }

    @SpirePatch2(
            clz = BossChest.class,
            method = "open",
            paramtypez = {boolean.class}
    )
    public static class OpenBoss {
        @SpirePostfixPatch
        public static void OpenChestPostfix(AbstractChest __instance, boolean bossChest) {
            if (disabled()) return;
            CHEST_OPENED.trigger(__instance);
        }
    }

    @SpirePatch2(clz = PotionPopUp.class, method = "updateInput")
    @SpirePatch2(clz = PotionPopUp.class, method = "updateTargetMode")
    public static class PotionUse {
        @SpireInsertPatch(locator = DestroyPotionLocator.class, localvars = {"potion"})
        public static void generalPotionPatch(AbstractPotion potion) {
            USE_POTION.trigger(potion);
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "damage")
    public static class FairyPotionUse {
        @SpireInsertPatch(locator = DestroyPotionLocator.class, localvars = {"p"})
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

    @SpirePatch(clz = TopPanel.class, method = "destroyPotion")
    public static class DestroyPotion {
        @SpirePostfixPatch
        public static void destroyPotionPatch(TopPanel __instance, int slot) {
            int count = 0;
            for (AbstractPotion p : AbstractDungeon.player.potions) {
                if (!(p instanceof PotionSlot)) {
                    count++;
                }
            }
            POTION_CHANGE.trigger(count);
        }
    }

    @SpirePatch2(clz = AbstractPlayer.class, method = "removePotion")
    @SpirePatch2(clz = AbstractPlayer.class, method = "obtainPotion", paramtypez = {AbstractPotion.class})
    @SpirePatch2(clz = AbstractPlayer.class, method = "obtainPotion", paramtypez = {int.class, AbstractPotion.class})
    public static class RemoveObtainPotion {
        @SpireInsertPatch(locator = PotionLocator.class)
        public static void removeObtainPotionPatch() {
            int count = 0;
            for (AbstractPotion p : AbstractDungeon.player.potions) {
                if (!(p instanceof PotionSlot)) {
                    count++;
                }
            }
            POTION_CHANGE.trigger(count);
        }

        private static class PotionLocator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "set");
                int[] lines = LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
                lines[0]++;
                return lines;
            }
        }
    }

    @SpirePatch(clz = Boot.class, method = "onAttackToChangeDamage")
    public static class BootTracker {
        @SpireInsertPatch(locator = BootLocator.class)
        public static void bootin() {
            BOOT_TRIGGER.trigger();
        }
    }

    private static class BootLocator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(Boot.class, "flash");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }

    @SpirePatch(clz = AbstractCreature.class, method = "heal", paramtypez = {int.class, boolean.class})
    public static class OnHealHealth {
        @SpirePostfixPatch
        public static void onHeal(AbstractCreature __instance, int healAmount) {
            if (disabled()) return;

            if (__instance instanceof AbstractPlayer && healAmount > 0)
                HEALTH_HEALED.trigger(healAmount);
        }
    }

    @SpirePatch(clz = AbstractCreature.class, method = "increaseMaxHp")
    public static class OnIncreaseMaxHealth {
        @SpirePostfixPatch
        public static void onIncrease(AbstractCreature __instance, int amount) {
            if (disabled()) return;

            if (__instance instanceof AbstractPlayer && amount > 0) {
                MAX_HEALTH_INCREASED.trigger(amount);
                MAX_HEALTH_CHANGED.trigger();
            }
        }
    }

    @SpirePatch(clz = AbstractCreature.class, method = "decreaseMaxHealth")
    public static class OnDecreaseMaxHealth {
        @SpirePostfixPatch
        public static void onDecrease(AbstractCreature __instance, int amount) {
            if (disabled()) return;

            if (__instance instanceof AbstractPlayer && amount > 0) {
                MAX_HEALTH_DECREASED.trigger(amount);
                MAX_HEALTH_CHANGED.trigger();
            }
        }
    }

    @SpirePatch2(
            clz = AbstractPlayer.class,
            method = "gainGold",
            paramtypez = int.class)
    public static class GainGoldPatch{
        @SpirePrefixPatch
        public static void GainGoldPatch(AbstractPlayer __instance, int amount){
            if (disabled()) return;

            if (!__instance.hasRelic(Ectoplasm.ID)) {
                GAIN_MONEY.trigger(amount);
            }
        }
    }

    @SpirePatch2(
            clz = AbstractPlayer.class,
            method = "loseGold",
            paramtypez = int.class)
    public static class SpendGoldPatch{
        @SpirePrefixPatch
        public static void LoseGoldPatch(AbstractPlayer __instance, int goldAmount){
            if (disabled()) return;

            LOSE_MONEY.trigger(goldAmount);

            if (AbstractDungeon.getCurrRoom() instanceof ShopRoom) {
                MONEY_SPENT_AT_SHOP.trigger(goldAmount);
            }

        }
    }

    @SpirePatch2(
            clz = AbstractRelic.class,
            method = "obtain"
    )
    public static class ObtainRelicHook {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert(AbstractRelic __instance) {
            if (disabled()) return;
            OBTAIN_RELIC.trigger(__instance);
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher matcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "relics");
                return LineFinder.findInOrder(ctBehavior, matcher);
            }
        }
    }

    @SpirePatch(
            clz = AbstractRelic.class,
            method = "instantObtain",
            paramtypez = {}
    )
    public static class InstantObtainRelicGetHook2 {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert(AbstractRelic __instance) {
            if (disabled()) return;
            OBTAIN_RELIC.trigger(__instance);
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher matcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "relics");
                return LineFinder.findInOrder(ctBehavior, matcher);
            }
        }
    }

    @SpirePatch(
            clz = AbstractRelic.class,
            method = "instantObtain",
            paramtypez = {AbstractPlayer.class, int.class, boolean.class}
    )
    public static class InstantObtainRelicGetHook {
        @SpireInsertPatch(
                locator = Locator.class
         )
        public static void Insert(AbstractRelic __instance, AbstractPlayer p, int slot, boolean callOnEquip) {
            if (disabled()) return;
            OBTAIN_RELIC.trigger(__instance);
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher matcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "relics");
                return LineFinder.findInOrder(ctBehavior, matcher);
            }
        }
    }

    @SpirePatch(
            clz = CampfireSmithEffect.class,
            method = "update"
    )
    public static class SmithCardHook {
        @SpireInsertPatch(rloc = 13, localvars = {"c"})
        public static void Insert(CampfireSmithEffect __instance, AbstractCard c) {
            UPGRADE_CARD_AT_CAMPFIRE.trigger(c);
        }
    }

    @SpirePatch2(
            clz = AbstractMonster.class,
            method = "die",
            paramtypez = {}
    )
    public static class OnEnemyDies{
        @SpirePostfixPatch
        public static void postfix(AbstractMonster __instance){
            Wiz.atb(new EnemyDiesDelayedCheckAction(__instance));
        }
    }

    @SpirePatch2(
            clz = CombatRewardScreen.class,
            method = "clear")
    public static class SkipPotionPatch{
        @SpirePrefixPatch
        public static void prefix(CombatRewardScreen __instance){
            for(RewardItem r : __instance.rewards){
                if(r.type == RewardItem.RewardType.POTION){
                    SKIP_POTION.trigger(r.potion);
                }
            }
        }
    }

    @SpirePatch(
            //can't patch TopPanel.destroyPotion because of FairyPotion interaction. patch PotionPopUp.updateInput instead
            clz = PotionPopUp.class,
            method = "updateInput"
    )
    public static class OnDiscardPotion {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void onDiscardPotion(PotionPopUp __instance) {
            int slot = ReflectionHacks.getPrivate(__instance, PotionPopUp.class, "slot");
            AbstractPotion potion = Wiz.p().potions.get(slot);
            DISCARD_POTION.trigger(potion);
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(TopPanel.class, "destroyPotion");
                int[] found = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                return new int[]{found[found.length-1]};
            }
        }
    }

    @SpirePatch2(
            clz = FlashAtkImgEffect.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {float.class, float.class, AbstractGameAction.AttackEffect.class, boolean.class}
    )
    public static class OnAttackAnimation {
        @SpirePostfixPatch
        public static void postfix(AbstractGameAction.AttackEffect effect) {
            ATTACK_ANIMATION.trigger(effect);
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "damage")
    public static class OnTakeUnblockedAttackDamage {
        @SpireInsertPatch(locator = Locator.class, localvars = {"damageAmount"})
        public static void onDamage(AbstractPlayer __instance, DamageInfo info, int damageAmount) {
            if (disabled()) return;

            UNBLOCKED_ATTACK_DAMAGE_TAKEN.trigger(damageAmount);
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(GameActionManager.class, "damageReceivedThisTurn");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

}
