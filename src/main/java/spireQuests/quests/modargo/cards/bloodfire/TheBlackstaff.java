package spireQuests.quests.modargo.cards.bloodfire;

import com.evacipated.cardcrawl.mod.stslib.actions.tempHp.AddTemporaryHPAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import spireQuests.abstracts.AbstractSQCard;
import spireQuests.quests.modargo.powers.IgnitePower;
import spireQuests.quests.modargo.vfx.BlackstaffCurseEffect;
import spireQuests.quests.modargo.vfx.BlackstaffSuctionEffect;

import static spireQuests.Anniv8Mod.makeID;
import static spireQuests.Anniv8Mod.makeImagePath;

public class TheBlackstaff extends AbstractSQCard {
    public static final String ID = makeID(TheBlackstaff.class.getSimpleName());
    private static final int IGNITE_HP = 9;
    private static final int INCREASE = 1;
    private static final int UPGRADE_INCREASE = 1;

    public TheBlackstaff() {
        super(ID, "modargo", 1, CardType.SKILL, CardRarity.SPECIAL, CardTarget.ENEMY);
        this.magicNumber = this.baseMagicNumber = IGNITE_HP;
        this.secondMagic = this.baseSecondMagic = INCREASE;
        setBannerTexture(makeImagePath("modargo/BlackstaffCardBanner.png"), makeImagePath("modargo/BlackstaffCardBanner_p.png"));
        setBackgroundTexture(makeImagePath("modargo/BlackstaffCardBackground.png"), makeImagePath("modargo/BlackstaffCardBackground_p.png"));
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new VFXAction(new BlackstaffSuctionEffect(m.hb.cX, m.hb.cY, p.hb.cX, p.hb.cY), 0.5F));
        this.addToBot(new AddTemporaryHPAction(p, p, this.magicNumber));
        this.addToBot(new VFXAction(new BlackstaffCurseEffect(m.hb.cX, m.hb.cY), 0.5F));
        this.addToBot(new ApplyPowerAction(m, p, IgnitePower.create(m, this.magicNumber)));
        this.addToBot(new VFXAction(p, new FlashAtkImgEffect(m.hb.cX, m.hb.cY, AbstractGameAction.AttackEffect.FIRE), 0.1F));
        this.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                TheBlackstaff.this.magicNumber = TheBlackstaff.this.baseMagicNumber += TheBlackstaff.this.secondMagic;
                this.isDone = true;
            }
        });
    }

    @Override
    public void upp() {
        this.upgradeSecondMagic(UPGRADE_INCREASE);
    }
}
