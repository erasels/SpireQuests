package spireQuests.cardmods;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import spireQuests.Anniv8Mod;

public class DamageAndBlockMod extends AbstractCardModifier {
    public static String ID = Anniv8Mod.makeID(DamageAndBlockMod.class.getSimpleName());
    public int damageChange, blockChange;

    public DamageAndBlockMod(int damageChange, int blockChange) {
        this.damageChange = damageChange;
        this.blockChange = blockChange;
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.baseDamage += damageChange;
        card.baseBlock += blockChange;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new DamageAndBlockMod(damageChange, blockChange);
    }
}