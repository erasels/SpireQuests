package spireQuests.quests.snumodder.cards;

import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.PersistFields;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import spireQuests.abstracts.AbstractSQCard;

import static spireQuests.Anniv8Mod.makeID;

public class ZilliaxDeluxe3000 extends AbstractSQCard {
    public static final String ID = makeID(ZilliaxDeluxe3000.class.getSimpleName());
    private static final int DAMAGE = 8;

    private final AbstractModuleCard cardA;
    private final AbstractModuleCard cardB;
    private final boolean costReduction;
    public int playedCardsThisTurn = 0;

    public ZilliaxDeluxe3000() {
        this(1, 12, 0,true, new RecursiveModule(), new RecursiveModule());
    }

    public ZilliaxDeluxe3000(AbstractModuleCard a, AbstractModuleCard b) {
        this(a.cost + b.cost, a.baseDamage + b.baseDamage, (a.baseBlock < 0 && b.baseBlock < 0) ? -1 : Math.max(0, a.baseBlock) + Math.max(0, b.baseBlock), false, a, b);
    }

    public ZilliaxDeluxe3000(int cost, int damage, int block, boolean isPreview, AbstractModuleCard a, AbstractModuleCard b) {
        super(ID, "snumodder", cost, CardType.ATTACK, CardRarity.SPECIAL, CardTarget.ENEMY, CardColor.COLORLESS);
        this.cardA = a;
        this.cardB = b;
        this.baseDamage = this.damage = damage;
        this.baseBlock = this.block = block;
        this.shuffleBackIntoDrawPile = a.shuffleBackIntoDrawPile || b.shuffleBackIntoDrawPile;
        this.costReduction = a.costReduction || b.costReduction;
        this.upgraded = a.upgraded || b.upgraded;
        if (a.persist || b.persist) PersistFields.setBaseValue(this, 2);
        if (isPreview) {
            rawDescription = cardStrings.DESCRIPTION;
        } else {
            StringBuilder sb = new StringBuilder();
            if (!cardA.cardStrings.EXTENDED_DESCRIPTION[0].isEmpty())
                sb.append(cardA.cardStrings.EXTENDED_DESCRIPTION[0]).append(" NL ");
            if (!cardB.cardStrings.EXTENDED_DESCRIPTION[0].isEmpty())
                sb.append(cardB.cardStrings.EXTENDED_DESCRIPTION[0]).append(" NL ");
            sb.append(cardStrings.EXTENDED_DESCRIPTION[0]);
            if (!cardA.cardStrings.EXTENDED_DESCRIPTION[1].isEmpty())
                sb.append(" NL ").append(cardA.cardStrings.EXTENDED_DESCRIPTION[1]);
            if (!cardB.cardStrings.EXTENDED_DESCRIPTION[1].isEmpty())
                sb.append(" NL ").append(cardB.cardStrings.EXTENDED_DESCRIPTION[1]);
            rawDescription = sb.toString();
        }
        initializeDescription();
    }

    @Override
    public void atTurnStart() {
        if (costReduction) {
            this.resetAttributes();
            playedCardsThisTurn = 0;
            this.applyPowers();
        }
    }

    @Override
    public void triggerWhenDrawn() {
        super.triggerWhenDrawn();
        if (costReduction) {
            this.setCostForTurn(this.cost - playedCardsThisTurn / 5);
        }
    }

    @Override
    public void triggerOnOtherCardPlayed(AbstractCard c) {
        if (costReduction) {
            this.playedCardsThisTurn++;
            this.setCostForTurn(this.cost - playedCardsThisTurn / 5);
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (baseBlock > 0)
            blck();
        dmg(m, AbstractGameAction.AttackEffect.SMASH);
        cardA.moduleUse(p, m, this);
        cardB.moduleUse(p, m, this);
    }

    @Override
    public void upp() {
        upgradeDamage(4);
        if (baseBlock > 0)
            upgradeBlock(2);
    }

    @Override
    public AbstractCard makeCopy() {
        ZilliaxDeluxe3000 tmp = new ZilliaxDeluxe3000(cardA, cardB);
        if (CardCrawlGame.dungeon != null && AbstractDungeon.currMapNode != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && costReduction) {
            this.setCostForTurn(this.cost - playedCardsThisTurn / 5);
        }
        return tmp;
    }
}
