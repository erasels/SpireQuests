package spireQuests.quests.snumodder.actions;

import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class DrawRareCardAction extends AbstractGameAction {

    private final AbstractPlayer p;

    public DrawRareCardAction(int amount) {
        this.amount = amount;
        this.p = AbstractDungeon.player;
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_MED;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_MED) {
            CardGroup rareGroup = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            for (AbstractCard c : p.drawPile.group) {
                if (c.rarity == AbstractCard.CardRarity.RARE) {
                    rareGroup.addToTop(c);
                }
            }
            if (rareGroup.isEmpty()) {
                this.isDone = true;
                return;
            }
            rareGroup.shuffle();
            for (int i = 0; i < this.amount; i++) {
                if (rareGroup.isEmpty())
                    break;
                AbstractCard card = rareGroup.getTopCard();
                rareGroup.removeCard(card);
                p.drawPile.removeCard(card);
                if (p.hand.size() == BaseMod.MAX_HAND_SIZE) {
                    p.createHandIsFullDialog();
                    p.discardPile.addToTop(card);
                    continue;
                }
                card.unhover();
                card.lighten(true);
                card.setAngle(0.0F);
                card.drawScale = 0.12F;
                card.targetDrawScale = 0.75F;
                card.current_x = CardGroup.DRAW_PILE_X;
                card.current_y = CardGroup.DRAW_PILE_Y;

                AbstractDungeon.player.hand.addToTop(card);
                AbstractDungeon.player.hand.refreshHandLayout();
                AbstractDungeon.player.hand.applyPowers();
            }

            this.isDone = true;
        }

        this.tickDuration();
    }
}

