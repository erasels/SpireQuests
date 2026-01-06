package spireQuests.quests.indi_keurodz.modifiers;

import com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.abstracts.AbstractCardModifier;
import spireQuests.Anniv8Mod;

public class HoloModifier extends AbstractCardModifier {

        public static final String MODIFIER_ID = Anniv8Mod.makeID("HoloBalatro");


    @Override
    public String identifier(AbstractCard card) {
        return MODIFIER_ID;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new HoloModifier();
    }

}
