package spireQuests.quests.indi_keurodz.BossBlinds;

import com.evacipated.cardcrawl.mod.stslib.relics.OnAfterUseCardRelic;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import javassist.CtBehavior;
import spireQuests.quests.indi_keurodz.BalatroQuest;
import spireQuests.quests.indi_keurodz.patches.ShowBossBlindsOnMapPatch.BossBlindField;
import spireQuests.util.Wiz;

import java.util.ArrayList;

public class DebuffCard {
//    @SpirePatch2(clz = AbstractPlayer.class, method = "useCard")
//    public static class RandomDebuffPatch {
//        @SpirePostfixPatch
//        public static void ApplyRandomDebuffAfterUseCard(AbstractPlayer __instance, AbstractCard c) {
//
//        }
//
//
//        }

    @SpirePatch2(clz= UseCardAction.class, method="update")
    public static class OnAfterUseCardPatch
    {
        @SpireInsertPatch(locator=Locator.class, localvars={"targetCard"})
        public static void Insert(UseCardAction __instance, AbstractCard targetCard)
        {
            BalatroQuest.BossBlind blind = BossBlindField.blind.get(AbstractDungeon.getCurrMapNode());
            if (blind == null) return;

            switch (blind) {
                case Plant:
                    if (targetCard.type == AbstractCard.CardType.POWER) ApplyRandomDebuffToPlayer();
                    break;
                case Head:
                    if (targetCard.rarity == AbstractCard.CardRarity.UNCOMMON) ApplyRandomDebuffToPlayer();
                    break;
                case Club:
                    if (targetCard.rarity == AbstractCard.CardRarity.BASIC) ApplyRandomDebuffToPlayer();
                    break;
                case Goad:
                    if (targetCard.rarity == AbstractCard.CardRarity.COMMON) ApplyRandomDebuffToPlayer();
                    break;
                case Window:
                    if (targetCard.rarity == AbstractCard.CardRarity.RARE) ApplyRandomDebuffToPlayer();
                    break;
                default:
                    break;
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception
            {
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



