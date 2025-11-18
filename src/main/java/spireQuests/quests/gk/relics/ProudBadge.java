package spireQuests.quests.gk.relics;

import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import spireQuests.abstracts.AbstractSQRelic;
import spireQuests.util.Wiz;

import static spireQuests.Anniv8Mod.makeID;

public class ProudBadge extends AbstractSQRelic {
    private static final int STR_AMT = 1;

    public static final String ID = makeID(ProudBadge.class.getSimpleName());

    public ProudBadge() {
        super(ID, "gk", RelicTier.SPECIAL, LandingSound.CLINK);
    }

    @Override
    public void onUsePotion() {
        if(Wiz.isInCombat()) {
            flash();
            Wiz.applyToSelf(new StrengthPower(Wiz.p(), STR_AMT));
            Wiz.applyToSelf(new LoseStrengthPower(Wiz.p(), STR_AMT));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return String.format(DESCRIPTIONS[0], STR_AMT);
    }
}
