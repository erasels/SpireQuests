package spireQuests.quests.indi_keurodz.modifiers;

import java.util.ArrayList;
import java.util.List;

import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import basemod.helpers.TooltipInfo;
import spireQuests.Anniv8Mod;

public class NegativeModifier extends AbstractCardModifier {

    public static final String ID = Anniv8Mod.makeID("Negative");

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public List<TooltipInfo> additionalTooltips(AbstractCard card) {
        List<TooltipInfo> tips = new ArrayList<>();
        final Keyword KEYWORD = Anniv8Mod.keywords.get(ID);
        tips.add(new TooltipInfo(KEYWORD.PROPER_NAME, KEYWORD.DESCRIPTION));
        return tips;
    }

    @Override
    public void onDrawn(AbstractCard card) {
        addToBot(new DrawCardAction(1));
    }

    @Override
    public boolean shouldApply(AbstractCard card) {
        return !CardModifierManager.hasModifier(card, ID);
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new NegativeModifier();
    }

}
