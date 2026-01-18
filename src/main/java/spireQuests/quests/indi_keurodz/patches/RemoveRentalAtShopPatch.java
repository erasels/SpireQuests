package spireQuests.quests.indi_keurodz.patches;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;

import basemod.ReflectionHacks;
import basemod.helpers.CardModifierManager;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import spireQuests.Anniv8Mod;
import spireQuests.quests.indi_keurodz.modifiers.RentalModifier;

public class RemoveRentalAtShopPatch {

    static final float SCALE = 0.8f * Settings.scale;
    static final float HOVERED_SCALE = 1.2f * Settings.scale;
    static final float w = 65f;
    static final float h = 65f;

    static final float x = 1554f * Settings.xScale + 145f * Settings.scale;
    static float y;

    static float buttonScale = SCALE;

    static boolean SCREEN_OPENED = false;
    static boolean hovered = false;

    private static final UIStrings RENTAL_STRINGS = CardCrawlGame.languagePack
            .getUIString(Anniv8Mod.makeID("RentalShop"));

    public static CardGroup getRentalCards() {
        CardGroup group = new CardGroup(CardGroupType.UNSPECIFIED);
        for (AbstractCard card : AbstractDungeon.player.masterDeck.getPurgeableCards().group) {
            if (CardModifierManager.hasModifier(card, RentalModifier.ID)) {
                group.addToBottom(card);
            }
        }
        return group;
    }

    @SpirePatch2(clz = ShopScreen.class, method = "update")
    public static class Update {
        @SpireInsertPatch(locator = Locator.class)
        public static void updateGridSelectScreen(ShopScreen __instance) {

            y = (float) ReflectionHacks.getPrivate(__instance, ShopScreen.class, "rugY")
                    + (float) ReflectionHacks.getPrivateStatic(ShopScreen.class, "BOTTOM_ROW_Y") + 70f;
            if (getRentalCards().size() > 0) {

                if (InputHelper.mX > x && InputHelper.mX < x + w && InputHelper.mY > y
                        && InputHelper.mY < y + h) {
                    __instance.moveHand(x - w / 2f - 160f * Settings.scale, y);
                    ReflectionHacks.setPrivate(__instance, ShopScreen.class, "somethingHovered", true);
                    hovered = true;
                    buttonScale = HOVERED_SCALE;
                } else {
                    hovered = false;
                }

                if (!hovered) {
                    buttonScale = MathHelper.cardScaleLerpSnap(buttonScale, SCALE);
                } else {
                    if (InputHelper.justReleasedClickLeft) {
                        hovered = false;
                        AbstractDungeon.previousScreen = AbstractDungeon.CurrentScreen.SHOP;
                        AbstractDungeon.gridSelectScreen.open(getRentalCards(),
                                1, ShopScreen.NAMES[13], false, false, true, true);
                        SCREEN_OPENED = true;
                    }
                    TipHelper.renderGenericTip(InputHelper.mX - 360.0f * Settings.scale,
                            InputHelper.mY - 70.0f * Settings.scale, RENTAL_STRINGS.TEXT[0], RENTAL_STRINGS.TEXT[1]);
                }

            } else {
                buttonScale = MathHelper.cardScaleLerpSnap(buttonScale, SCALE);
            }

            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                for (AbstractCard card : AbstractDungeon.gridSelectScreen.selectedCards) {
                    AbstractDungeon.topLevelEffects.add(
                            new PurgeCardEffect(card, (float) Settings.WIDTH / 2.0f, (float) Settings.HEIGHT / 2.0f));
                    AbstractDungeon.player.masterDeck.removeCard(card);
                }
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
                SCREEN_OPENED = false;
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ShopScreen.class,
                        "updateRelics");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
            }
        }

    }

    @SpirePatch2(clz = ShopScreen.class, method = "updatePurge")
    public static class PreventBaseGamePurge {
        @SpirePrefixPatch
        public static SpireReturn<Void> skip() {
            if (SCREEN_OPENED) {
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz = ShopScreen.class, method = "renderPurge")
    public static class Render {
        @SpirePostfixPatch
        public static void render(SpriteBatch sb) {
            if (getRentalCards().size() > 0) {
                sb.draw(RentalModifier.icon, x, y, w / 2, h / 2, w, h, buttonScale, buttonScale, 0f, 0, 0, (int) w,
                        (int) h,
                        false, false);

            }

        }

    }

    @SpirePatch2(clz = ShopScreen.class, method = "open")
    public static class AvoidPopIn {
        @SpirePrefixPatch
        public static void reset() {
            y = -1000f;
        }
    }

}
