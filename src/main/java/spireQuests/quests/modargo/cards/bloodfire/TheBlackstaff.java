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

import static spireQuests.Anniv8Mod.*;

public class TheBlackstaff extends AbstractSQCard {
    public static final String ID = makeID(TheBlackstaff.class.getSimpleName());
    private static final int IGNITE_HP = 9;
    private static final int UPGRADE_IGNITE_HP = 3;

    public TheBlackstaff() {
        super(ID, "modargo", 2, CardType.SKILL, CardRarity.SPECIAL, CardTarget.ENEMY);
        this.magicNumber = this.baseMagicNumber = IGNITE_HP;
        this.shuffleBackIntoDrawPile = true;
        setBannerTexture(makeContributionPath("modargo", "BlackstaffCardBanner.png"), makeContributionPath("modargo", "BlackstaffCardBanner_p.png"));
        setBackgroundTexture(makeContributionPath("modargo", "BlackstaffCardBackground.png"), makeContributionPath("modargo", "BlackstaffCardBackground_p.png"));
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new VFXAction(new BlackstaffSuctionEffect(m.hb.cX, m.hb.cY, p.hb.cX, p.hb.cY), 0.5F));
        this.addToBot(new AddTemporaryHPAction(p, p, this.magicNumber));
        this.addToBot(new VFXAction(new BlackstaffCurseEffect(m.hb.cX, m.hb.cY), 0.5F));
        this.addToBot(new ApplyPowerAction(m, p, IgnitePower.create(m, this.magicNumber)));
        this.addToBot(new VFXAction(p, new FlashAtkImgEffect(m.hb.cX, m.hb.cY, AbstractGameAction.AttackEffect.FIRE), 0.1F));
    }

    @Override
    public void upp() {
        this.upgradeMagicNumber(UPGRADE_IGNITE_HP);
    }
}
