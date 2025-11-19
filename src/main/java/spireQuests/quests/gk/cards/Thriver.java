package spireQuests.quests.gk.cards;

import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static spireQuests.Anniv8Mod.makeID;

public class Thriver extends AbstractBPCard {
    public static final String ID = makeID(Thriver.class.getSimpleName());

    public Thriver() {
        super(ID, "gk", 1, CardType.SKILL, CardRarity.BASIC, CardTarget.SELF, CardColor.GREEN);

        baseBlock = block = 8;
        baseMagicNumber = magicNumber = 1;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        blck();
        addToBot(new DrawCardAction(p, magicNumber));
        addToBot(new DiscardAction(p, p, magicNumber, false));
    }

    @Override
    public void upp() {
        upgradeBlock(3);
    }
}
