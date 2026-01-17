package spireQuests.quests.indi_keurodz.modifiers;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import spireQuests.Anniv8Mod;
import spireQuests.util.Wiz;

public class DebuffedModifier extends AbstractCardModifier {

    public static final String MODIFIER_ID = Anniv8Mod.makeID("DebuffedBalatro");
    private static final UIStrings uiStrings;

    static {
        uiStrings = CardCrawlGame.languagePack.getUIString(MODIFIER_ID);
    }

    @Override
    public String identifier(AbstractCard card) {
        return MODIFIER_ID;
    }

    @Override
    // happens after the card is used
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
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

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return String.format(uiStrings.TEXT[1], rawDescription);
    }

    @Override
    public boolean shouldApply(AbstractCard card) {
        return !CardModifierManager.hasModifier(card, MODIFIER_ID);
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new DebuffedModifier();
    }

}
