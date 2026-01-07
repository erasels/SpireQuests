package spireQuests.quests.indi_keurodz.modifiers;

import com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.abstracts.AbstractCardModifier;
import spireQuests.Anniv8Mod;

public class PolychromeModifier extends AbstractCardModifier {

    public static final String MODIFIER_ID = Anniv8Mod.makeID("PolychromeBalatro");

    @Override
    public String identifier(AbstractCard card) {
        return MODIFIER_ID;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new PolychromeModifier();
    }

}
