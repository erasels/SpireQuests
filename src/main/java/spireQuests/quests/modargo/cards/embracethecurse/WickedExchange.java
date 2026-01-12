package spireQuests.quests.modargo.cards.embracethecurse;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DiscardSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.List;
import java.util.stream.Collectors;

import static spireQuests.Anniv8Mod.makeID;

public class WickedExchange extends EmbraceTheCurseCard {
    public static final String ID = makeID(WickedExchange.class.getSimpleName());

    public WickedExchange() {
        super(ID, 0, CardType.SKILL, CardTarget.SELF);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                List<AbstractCard> curses = AbstractDungeon.player.hand.group.stream().filter(c -> c.color == CardColor.CURSE).collect(Collectors.toList());
                if (!curses.isEmpty()) {
                    this.addToTop(new DrawCardAction(curses.size()));
                    for (AbstractCard c : curses) {
                        this.addToTop(new DiscardSpecificCardAction(c));
                    }
                }
                this.isDone = true;
            }
        });
    }

    @Override
    public void upp() {
        this.retain = true;
    }
}
