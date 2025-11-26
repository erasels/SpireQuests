package spireQuests.quests.modargo.cards.bloodfire;

import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static spireQuests.Anniv8Mod.makeID;

public class BloodInTheChalice extends BloodfireRitualCard {
    public static final String ID = makeID(BloodInTheChalice.class.getSimpleName());
    private static final int HP_LOSS = 1;

    public BloodInTheChalice() {
        super(ID);
        this.magicNumber = this.baseMagicNumber = HP_LOSS;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new LoseHPAction(p, p, this.magicNumber));
    }
}
