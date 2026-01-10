package spireQuests.quests.indi_keurodz.modifiers;

import java.util.ArrayList;
import java.util.List;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import basemod.helpers.TooltipInfo;
import spireQuests.Anniv8Mod;

public class NegativeModifier extends AbstractCardModifier {

    public static final String MODIFIER_ID = Anniv8Mod.makeID("Negative");

    @Override
    public String identifier(AbstractCard card) {
        return MODIFIER_ID;
    }

    @Override
    public List<TooltipInfo> additionalTooltips(AbstractCard card) {
        List<TooltipInfo> tips = new ArrayList<>();
        tips.add(new TooltipInfo(Anniv8Mod.keywords.get("Negative").PROPER_NAME,
                Anniv8Mod.keywords.get("Negative").DESCRIPTION));
        return tips;
    }

    @Override
    public void onDrawn(AbstractCard card) {
        addToBot(new DrawCardAction(1));
    }

    @Override
    public boolean shouldApply(AbstractCard card) {
        return !CardModifierManager.hasModifier(card, MODIFIER_ID);
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new NegativeModifier();
    }

}
