package spireQuests.quests.ramchops;

import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import spireQuests.abstracts.AbstractSQCard;

import static spireQuests.Anniv8Mod.makeID;

public class SelloutAdvertisementCard extends AbstractSQCard {
    public final static String ID = makeID("SelloutAdCard");
    private Random rng;

    public SelloutAdvertisementCard() {
        super(ID, "ramchops",1, CardType.SKILL, CardRarity.SPECIAL, CardTarget.SELF);

        if(this.misc == 0){
            rng = new Random((long) this.uuid.hashCode());
            this.misc = rng.random(1, cardStrings.EXTENDED_DESCRIPTION.length);

            this.rawDescription += " NL ";
            this.rawDescription += cardStrings.EXTENDED_DESCRIPTION[this.misc - 1];
        }

        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

    }

    @Override
    public boolean canUpgrade() {
        return false;
    }


    @Override
    public void upp() {

    }



}
