package spireQuests.quests.indi_keurodz.BossBlinds;

import java.util.ArrayList;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.cardManip.ExhaustCardEffect;

import basemod.ReflectionHacks;
import javassist.CtBehavior;
import spireQuests.patches.ShowMarkedNodesOnMapPatch.ImageField;
import spireQuests.quests.indi_keurodz.BalatroQuest;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;

public class TheArm {

    static boolean card_transformed = false;

    @SpirePatch2(clz = UseCardAction.class, method = "update")
    public static class TranformFirstCardIntoPlainCopy {
        @SpireInsertPatch(locator = Locator.class, localvars = { "targetCard" })
        public static void HookDiscardAndDraw(UseCardAction __instance, AbstractCard targetCard) {
            if (!ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.ID, BossBlind.Arm.frames)
                    || card_transformed || !targetCard.upgraded)
                return;

            ArrayList<AbstractCard> group = AbstractDungeon.player.masterDeck.group;

            for (int i = 0; i < group.size(); i++) {
                AbstractCard c = group.get(i);
                if (c.uuid.equals(targetCard.uuid)) {
                    group.set(i, targetCard.makeCopy());
                    break;
                }
            }

            AbstractCard plainCopy = targetCard.makeCopy();
            plainCopy.current_x = targetCard.current_x;
            plainCopy.current_y = targetCard.current_y;
            plainCopy.target_x = targetCard.target_x;
            plainCopy.target_y = targetCard.target_y;
            plainCopy.drawScale = targetCard.drawScale;
            plainCopy.targetDrawScale = targetCard.targetDrawScale;
            plainCopy.angle = targetCard.angle;
            plainCopy.targetAngle = targetCard.targetAngle;

            ReflectionHacks.setPrivate(__instance, UseCardAction.class, "targetCard", plainCopy);

            AbstractDungeon.effectList.add(new ExhaustCardEffect(targetCard));
            if (AbstractDungeon.player.limbo.contains(targetCard)) {
                AbstractDungeon.player.limbo.removeCard(targetCard);
            }

            AbstractDungeon.player.cardInUse = null;

            card_transformed = true;

        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "purgeOnUse");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }

    }
}
