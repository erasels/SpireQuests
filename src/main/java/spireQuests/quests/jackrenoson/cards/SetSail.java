package spireQuests.quests.jackrenoson.cards;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import spireQuests.abstracts.AbstractSQCard;

import static spireQuests.Anniv8Mod.makeID;

public class SetSail extends AbstractSQCard {
    public final static String ID = makeID(SetSail.class.getSimpleName());

    public SetSail() {
        super(ID, "jackrenoson", 0, CardType.SKILL, CardRarity.SPECIAL, CardTarget.SELF);
        baseBlock = block = 6;
        selfRetain = true;
        exhaust = true;
    }

    @Override
    public void upp() {
        upgradeBlock(4);
    }

    @Override
    public void use(AbstractPlayer abstractPlayer, AbstractMonster abstractMonster) {
        blck();
    }
}
