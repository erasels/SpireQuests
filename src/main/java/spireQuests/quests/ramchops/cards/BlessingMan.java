package spireQuests.quests.ramchops.cards;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.vfx.combat.MiracleEffect;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect;
import spireQuests.abstracts.AbstractSQCard;

import static spireQuests.Anniv8Mod.makeID;
import static spireQuests.util.Wiz.applyToEnemy;
import static spireQuests.util.Wiz.atb;

public class BlessingMan extends AbstractSQCard {
    public final static String ID = makeID("BlessingMan");
    private Random rng;

    public BlessingMan() {
        super(ID, "ramchops",0, CardType.SKILL, CardRarity.SPECIAL, CardTarget.ALL_ENEMY);
        this.exhaust = true;
        this.selfRetain = true;
        this.baseMagicNumber = this.magicNumber = 3;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new SFXAction("THUNDERCLAP"));
        atb(new VFXAction(new ShockWaveEffect(p.hb.cX, p.hb.cY, Color.RED, ShockWaveEffect.ShockWaveType.CHAOTIC)));

        for (int i = 0; i < magicNumber; i++){
            for (AbstractMonster mon2 : AbstractDungeon.getMonsters().monsters){
                applyToEnemy(mon2, new VulnerablePower(mon2, 1, false));
            }
        }
    }

    @Override
    public void upp() {
        upgradeMagicNumber(2);
    }
}

