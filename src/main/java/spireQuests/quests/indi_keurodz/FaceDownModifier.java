package spireQuests.quests.indi_keurodz;

import com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.abstracts.AbstractCardModifier;
import spireQuests.Anniv8Mod;

public class FaceDownModifier extends AbstractCardModifier {

    public static String MODIFIER_ID = Anniv8Mod.makeID(FaceDownModifier.class.getSimpleName());

    @Override
    public String identifier(AbstractCard card) {
        return MODIFIER_ID;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new EternalStickerModifier();
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.isFlipped = true;
    }

}
