package spireQuests.quests.snumodder.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import spireQuests.quests.snumodder.ZilliaxDeluxe3000Quest;

import static spireQuests.Anniv8Mod.makeID;

public class TickingModule extends AbstractModuleCard {
    public static final String ID = makeID(TickingModule.class.getSimpleName());
    public int playedCardsThisTurn = 0;

    public TickingModule() {
        super(ID, "snumodder", 1, CardType.ATTACK, CardRarity.SPECIAL, CardTarget.ENEMY, CardColor.COLORLESS);
        baseDamage = damage = 8;
        costReduction = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        dmg(m, AbstractGameAction.AttackEffect.SLASH_DIAGONAL);
    }

    @Override
    public void upp() {
        upgradeDamage(2);
    }

    @Override
    public void atTurnStart() {
        this.resetAttributes();
        playedCardsThisTurn = 0;
        this.applyPowers();
    }

    @Override
    public void triggerWhenDrawn() {
        super.triggerWhenDrawn();
        this.setCostForTurn(this.cost - playedCardsThisTurn / 5);
    }

    @Override
    public void triggerOnOtherCardPlayed(AbstractCard c) {
        this.playedCardsThisTurn++;
        this.setCostForTurn(this.cost - playedCardsThisTurn / 5);
    }

    @Override
    public void moduleUse(AbstractPlayer p, AbstractMonster m, AbstractCard card) {

    }

    @Override
    public AbstractCard makeCopy() {
        TickingModule tmp = new TickingModule();
        if (CardCrawlGame.dungeon != null && AbstractDungeon.currMapNode != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
            this.setCostForTurn(this.cost - playedCardsThisTurn / 5);
        }
        return tmp;
    }

    @Override
    public ZilliaxDeluxe3000Quest.ZilliaxModules getModule() {
        return ZilliaxDeluxe3000Quest.ZilliaxModules.TICKING;
    }
}
