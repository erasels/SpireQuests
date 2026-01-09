package spireQuests.quests.modargo.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.LoseBlockAction;
import com.megacrit.cardcrawl.cards.curses.Normality;
import com.megacrit.cardcrawl.cards.curses.Pain;
import com.megacrit.cardcrawl.cards.curses.Regret;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import javassist.CtBehavior;
import spireQuests.Anniv8Mod;
import spireQuests.quests.modargo.relics.TheLivingCurse;
import spireQuests.util.Wiz;

public class ReduceCurseEffectsPatch {
    public static final int NORMALITY_CARDS = 6;
    public static final CardStrings normalityStrings = CardCrawlGame.languagePack.getCardStrings(Normality.ID);

    @SpirePatch2(clz = Normality.class, method = SpirePatch.CONSTRUCTOR)
    public static class NormalityDescriptionPatch {
        @SpirePostfixPatch
        public static void patch(Normality __instance) {
            if (TheLivingCurse.hasRelic()) {
                __instance.rawDescription = __instance.rawDescription.replace("3", NORMALITY_CARDS + "");
                __instance.initializeDescription();
            }
        }
    }

    @SpirePatch2(clz = Normality.class, method = "applyPowers")
    public static class NormalityApplyPowersPatch {
        @SpireInsertPatch(locator = Locator.class)
        public static SpireReturn<Void> patch(Normality __instance) {
            if (TheLivingCurse.hasRelic()) {
                if (AbstractDungeon.actionManager.cardsPlayedThisTurn.isEmpty()) {
                    __instance.rawDescription = normalityStrings.EXTENDED_DESCRIPTION[1] + NORMALITY_CARDS + normalityStrings.EXTENDED_DESCRIPTION[2];
                } else if (AbstractDungeon.actionManager.cardsPlayedThisTurn.size() == 1) {
                    __instance.rawDescription = normalityStrings.EXTENDED_DESCRIPTION[1] + NORMALITY_CARDS + normalityStrings.EXTENDED_DESCRIPTION[3] + AbstractDungeon.actionManager.cardsPlayedThisTurn.size() + normalityStrings.EXTENDED_DESCRIPTION[4];
                } else {
                    __instance.rawDescription = normalityStrings.EXTENDED_DESCRIPTION[1] + NORMALITY_CARDS + normalityStrings.EXTENDED_DESCRIPTION[3] + AbstractDungeon.actionManager.cardsPlayedThisTurn.size() + normalityStrings.EXTENDED_DESCRIPTION[5];
                }

                __instance.initializeDescription();
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractDungeon.class, "actionManager");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = Normality.class, method = "canPlay")
    public static class NormalityCanPlayPatch {
        @SpirePrefixPatch
        public static SpireReturn<Boolean> patch(Normality __instance) {
            if (TheLivingCurse.hasRelic()) {
                if (AbstractDungeon.actionManager.cardsPlayedThisTurn.size() >= NORMALITY_CARDS) {
                    __instance.cantUseMessage = normalityStrings.EXTENDED_DESCRIPTION[0].replace("3", NORMALITY_CARDS + "");
                    return SpireReturn.Return(false);
                } else {
                    return SpireReturn.Return(true);
                }
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz = Pain.class, method = SpirePatch.CONSTRUCTOR)
    public static class PainDescriptionPatch {
        @SpirePostfixPatch
        public static void patch(Pain __instance) {
            if (TheLivingCurse.hasRelic()) {
                __instance.rawDescription = CardCrawlGame.languagePack.getCardStrings(Anniv8Mod.makeID("Pain")).DESCRIPTION;
                __instance.initializeDescription();
            }
        }
    }

    @SpirePatch2(clz = Pain.class, method = "triggerOnOtherCardPlayed")
    public static class PainTriggerOnOtherCardPlayedPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> patch(Pain __instance) {
            if (TheLivingCurse.hasRelic()) {
                Wiz.att(new LoseBlockAction(AbstractDungeon.player, AbstractDungeon.player, 1));
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz = Regret.class, method = SpirePatch.CONSTRUCTOR)
    public static class RegretDescriptionPatch {
        @SpirePostfixPatch
        public static void patch(Regret __instance) {
            if (TheLivingCurse.hasRelic()) {
                __instance.rawDescription = CardCrawlGame.languagePack.getCardStrings(Anniv8Mod.makeID("Regret")).DESCRIPTION;
                __instance.initializeDescription();
            }
        }
    }

    @SpirePatch2(clz = Regret.class, method = "use", paramtypez = { AbstractPlayer.class, AbstractMonster.class })
    public static class RegretUseClass {
        @SpirePrefixPatch
        public static SpireReturn<Void> patch(Regret __instance, AbstractPlayer p, AbstractMonster m) {
            if (TheLivingCurse.hasRelic()) {
                if (__instance.dontTriggerOnUseCard) {
                    AbstractDungeon.actionManager.addToTop(new LoseBlockAction(p, p, __instance.magicNumber));
                    AbstractDungeon.effectList.add(new FlashAtkImgEffect(p.hb.cX, p.hb.cY, AbstractGameAction.AttackEffect.FIRE));
                }
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }
}
