package spireQuests.quests.snumodder.cards;

import basemod.abstracts.CustomSavable;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.PersistFields;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import spireQuests.abstracts.AbstractSQCard;

import static spireQuests.Anniv8Mod.makeID;

public class ZilliaxDeluxe3000 extends AbstractSQCard implements CustomSavable<ZilliaxDeluxe3000.ZilliaxSaveData> {
    public static final String ID = makeID(ZilliaxDeluxe3000.class.getSimpleName());

    public AbstractModuleCard cardA;
    public AbstractModuleCard cardB;
    public boolean costReduction;
    public int playedCardsThisTurn = 0;
    public boolean isPreview = true;

    public static class ZilliaxSaveData {
        public String moduleAID;
        public String moduleBID;
        public boolean costReduction;
        public int cost;
        public int baseDamage;
        public int baseBlock;
        public boolean upgraded;
        public boolean isPreview;
    }

    public ZilliaxDeluxe3000() {
        super(ID, "snumodder", 1, CardType.ATTACK, CardRarity.SPECIAL, CardTarget.ENEMY, CardColor.COLORLESS);
        setCardState(new RecursiveModule(), new RecursiveModule(),
                1, 8, 0, true, false);
    }

    public ZilliaxDeluxe3000(AbstractModuleCard a, AbstractModuleCard b) {
        super(ID, "snumodder", a.cost + b.cost, CardType.ATTACK, CardRarity.SPECIAL, CardTarget.ENEMY, CardColor.COLORLESS);
        setCardState(a, b, a.cost + b.cost, a.baseDamage + b.baseDamage, (a.baseBlock < 0 && b.baseBlock < 0) ? -1 : Math.max(0, a.baseBlock) + Math.max(0, b.baseBlock), false, a.upgraded || b.upgraded);
    }

    public void setCardState(AbstractModuleCard a, AbstractModuleCard b, int cost, int damage, int block, boolean isPreview, boolean upgraded) {
        this.cardA = a;
        this.cardB = b;
        this.isPreview = isPreview;
        this.upgraded = upgraded;

        this.cost = cost;
        this.costForTurn = cost;
        this.baseDamage = this.damage = damage;
        this.baseBlock = this.block = block;

        this.shuffleBackIntoDrawPile = a.shuffleBackIntoDrawPile || b.shuffleBackIntoDrawPile;
        this.costReduction = a.costReduction || b.costReduction;

        if (a.persist || b.persist)
            PersistFields.setBaseValue(this, 2);

        if (this.isPreview) {
            rawDescription = cardStrings.DESCRIPTION;
        } else {
            StringBuilder sb = new StringBuilder();
            if (!a.cardStrings.EXTENDED_DESCRIPTION[0].isEmpty())
                sb.append(a.cardStrings.EXTENDED_DESCRIPTION[0]).append(" NL ");
            if (!b.cardStrings.EXTENDED_DESCRIPTION[0].isEmpty())
                sb.append(b.cardStrings.EXTENDED_DESCRIPTION[0]).append(" NL ");
            sb.append(cardStrings.EXTENDED_DESCRIPTION[0]);
            if (!a.cardStrings.EXTENDED_DESCRIPTION[1].isEmpty())
                sb.append(" NL ").append(a.cardStrings.EXTENDED_DESCRIPTION[1]);
            if (!b.cardStrings.EXTENDED_DESCRIPTION[1].isEmpty())
                sb.append(" NL ").append(b.cardStrings.EXTENDED_DESCRIPTION[1]);
            rawDescription = sb.toString();
        }

        initializeDescription();
    }

    @Override
    public ZilliaxSaveData onSave() {
        if (isPreview) return null;
        ZilliaxSaveData data = new ZilliaxSaveData();
        data.moduleAID = cardA.cardID;
        data.moduleBID = cardB.cardID;
        data.costReduction = this.costReduction;
        data.cost = this.cost;
        data.baseDamage = this.baseDamage;
        data.baseBlock = this.baseBlock;
        data.upgraded = this.upgraded;
        data.isPreview = this.isPreview;
        return data;
    }

    @Override
    public void onLoad(ZilliaxSaveData data) {
        if (data == null) {
            this.isPreview = true;
            return;
        }
        AbstractModuleCard a = (AbstractModuleCard) CardLibrary.getCopy(data.moduleAID);
        AbstractModuleCard b = (AbstractModuleCard) CardLibrary.getCopy(data.moduleBID);
        setCardState(a, b, data.cost, data.baseDamage, data.baseBlock, data.isPreview, data.upgraded);
    }

    @Override
    public void atTurnStart() {
        if (costReduction) {
            resetAttributes();
            playedCardsThisTurn = 0;
            applyPowers();
        }
    }

    @Override
    public void triggerWhenDrawn() {
        super.triggerWhenDrawn();
        if (costReduction) {
            setCostForTurn(cost - playedCardsThisTurn / 5);
        }
    }

    @Override
    public void triggerOnOtherCardPlayed(AbstractCard c) {
        if (costReduction) {
            playedCardsThisTurn++;
            setCostForTurn(cost - playedCardsThisTurn / 5);
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (baseBlock > 0) blck();
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
        if (isPreview)
            return new ZilliaxDeluxe3000();
        return new ZilliaxDeluxe3000(cardA, cardB);
    }
}