package spireQuests.quests.modargo.cards.embracethecurse;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import spireQuests.quests.modargo.patches.TrackCardsDrawnDuringTurnPatch;

import static spireQuests.Anniv8Mod.makeID;

public class NetherStrike extends EmbraceTheCurseCard {
    public static final String ID = makeID(NetherStrike.class.getSimpleName());
    private static final int DAMAGE = 9;
    private static final int UPGRADE_DAMAGE = 4;

    public NetherStrike() {
        super(ID, 1, CardType.ATTACK, CardTarget.ENEMY);
        this.baseDamage = DAMAGE;
        this.magicNumber = this.baseMagicNumber = 1;
        this.tags.add(CardTags.STRIKE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
        if (this.hasDrawnCurseThisTurn()) {
            this.addToBot(new ApplyPowerAction(m, p, new WeakPower(m, this.magicNumber, false)));
            this.addToBot(new ApplyPowerAction(m, p, new VulnerablePower(m, this.magicNumber, false)));
        }
    }

    @Override
    public void triggerOnGlowCheck() {
        this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
        if (this.hasDrawnCurseThisTurn()) {
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
        }
    }

    private boolean hasDrawnCurseThisTurn() {
        return TrackCardsDrawnDuringTurnPatch.cardsDrawnThisTurn.stream().anyMatch(c -> c.color == CardColor.CURSE);
    }

    @Override
    public void upp() {
        this.upgradeDamage(UPGRADE_DAMAGE);
    }
}
