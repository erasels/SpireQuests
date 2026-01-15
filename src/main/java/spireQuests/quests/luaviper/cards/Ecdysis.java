package spireQuests.quests.luaviper.cards;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import spireQuests.abstracts.AbstractSQCard;

import static spireQuests.Anniv8Mod.makeID;

public class Ecdysis extends AbstractSQCard {
    public static final String ID = makeID(Ecdysis.class.getSimpleName());

    public Ecdysis() {
        super(ID, "luaviper", 2, CardType.SKILL, CardRarity.SPECIAL, CardTarget.NONE);
        exhaust = true;
        baseMagicNumber = 3;
        magicNumber = baseMagicNumber;
        setDisplayRarity(CardRarity.RARE);
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        for (int i = 0; i < this.magicNumber; ++i) {
            AbstractCard card = AbstractDungeon.returnTrulyRandomCardInCombat(CardType.POWER).makeCopy();
//            if (card.cost > 0) {
//                card.cost = 0;            // i wish
//                card.costForTurn = 0;
//                card.isCostModified = true;
//            }
            this.addToBot(new MakeTempCardInDrawPileAction(card, 1, true, true));
        }
    }

    @Override
    public void upp() {
        this.upgradeMagicNumber(2);
    }


}
