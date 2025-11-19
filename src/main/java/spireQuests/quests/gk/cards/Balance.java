package spireQuests.quests.gk.cards;

import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.stances.CalmStance;

import static spireQuests.Anniv8Mod.makeID;

public class Balance extends AbstractBPCard {
    public static final String ID = makeID(Balance.class.getSimpleName());

    public Balance() {
        super(ID, "gk", 1, CardType.SKILL, CardRarity.BASIC, CardTarget.SELF, CardColor.PURPLE);

        this.baseBlock = block = 8;
    }

    @Override
    public void use(AbstractPlayer abstractPlayer, AbstractMonster abstractMonster) {
        blck();
        addToBot(new ChangeStanceAction(CalmStance.STANCE_ID));
    }

    @Override
    public void upp() {
        upgradeBlock(3);
    }
}
