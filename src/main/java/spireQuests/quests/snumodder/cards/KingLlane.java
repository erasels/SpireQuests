package spireQuests.quests.snumodder.cards;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import spireQuests.abstracts.AbstractSQCard;

import static spireQuests.Anniv8Mod.makeID;

public class KingLlane extends AbstractSQCard {
    public static final String ID = makeID(KingLlane.class.getSimpleName());
    private static final int MAGIC = 1;

    public KingLlane() {
        super(ID, "snumodder", 1, CardType.CURSE, CardRarity.SPECIAL, CardTarget.NONE, CardColor.CURSE);
        magicNumber = baseMagicNumber = MAGIC;
        shuffleBackIntoDrawPile = true;
        isEthereal = true;
        isInnate = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new DrawCardAction(p, this.magicNumber));
    }

    @Override
    public void upp() {

    }
}
