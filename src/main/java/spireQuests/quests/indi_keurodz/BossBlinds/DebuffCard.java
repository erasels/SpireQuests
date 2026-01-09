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
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;

import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;
import spireQuests.quests.indi_keurodz.BalatroQuest;

import spireQuests.patches.ShowMarkedNodesOnMapPatch.ImageField;

import javassist.CtBehavior;
import spireQuests.util.Wiz;

public class DebuffCard {
    // @SpirePatch2(clz = AbstractPlayer.class, method = "useCard")
    // public static class RandomDebuffPatch {
    // @SpirePostfixPatch
    // public static void ApplyRandomDebuffAfterUseCard(AbstractPlayer __instance,
    // AbstractCard c) {
    //
    // }
    //
    //
    // }

    @SpirePatch2(clz = UseCardAction.class, method = "update")
    public static class OnAfterUseCardPatch {
        @SpireInsertPatch(locator = Locator.class, localvars = { "targetCard" })
        public static void Insert(UseCardAction __instance, AbstractCard targetCard) {
            if (ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.id, BossBlind.Plant.frames)) {
                if (targetCard.type == AbstractCard.CardType.POWER)
                    ApplyRandomDebuffToPlayer();

            } else if (ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.id, BossBlind.Head.frames)) {
                if (targetCard.rarity == AbstractCard.CardRarity.UNCOMMON)
                    ApplyRandomDebuffToPlayer();

            } else if (ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.id, BossBlind.Club.frames)) {
                if (targetCard.rarity == AbstractCard.CardRarity.BASIC)
                    ApplyRandomDebuffToPlayer();

            } else if (ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.id, BossBlind.Goad.frames)) {
                if (targetCard.rarity == AbstractCard.CardRarity.COMMON)
                    ApplyRandomDebuffToPlayer();

            } else if (ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.id, BossBlind.Window.frames)) {
                if (targetCard.rarity == AbstractCard.CardRarity.RARE)
                    ApplyRandomDebuffToPlayer();
            }

        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractDungeon.class, "getMonsters");
                return LineFinder.findInOrder(ctBehavior, new ArrayList<>(), finalMatcher);
            }
        }

        private static void ApplyRandomDebuffToPlayer() {
            int random = AbstractDungeon.cardRandomRng.random(2);

            switch (random) {
                case 0:
                    Wiz.applyToSelf(new WeakPower(Wiz.p(), 1, false));
                    break;
                case 1:
                    Wiz.applyToSelf(new VulnerablePower(Wiz.p(), 1, false));
                    break;
                case 2:
                    Wiz.applyToSelf(new FrailPower(Wiz.p(), 1, false));
                    break;
            }

        }
    }
}
