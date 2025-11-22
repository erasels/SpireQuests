package spireQuests.quests.modargo.cards.bloodfire;

import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.status.VoidCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import spireQuests.abstracts.AbstractSQCard;

import static spireQuests.Anniv8Mod.makeID;
import static spireQuests.Anniv8Mod.makeImagePath;

public class CallTheBlackstaff extends AbstractSQCard {
    public static final String ID = makeID(CallTheBlackstaff.class.getSimpleName());

    public CallTheBlackstaff() {
        super(ID, "modargo", 2, CardType.SKILL, CardRarity.SPECIAL, CardTarget.SELF);
        this.exhaust = true;
        this.cardsToPreview = new TheBlackstaff();
        setBannerTexture(makeImagePath("modargo/BlackstaffCardBanner.png"), makeImagePath("modargo/BlackstaffCardBanner_p.png"));
        setBackgroundTexture(makeImagePath("modargo/BlackstaffCardBackground.png"), makeImagePath("modargo/BlackstaffCardBackground_p.png"));
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                int amount = BaseMod.MAX_HAND_SIZE - AbstractDungeon.player.hand.group.size();
                this.addToTop(new MakeTempCardInHandAction(new VoidCard(), amount));
                this.isDone = true;
            }
        });
        this.addToBot(new MakeTempCardInDrawPileAction(new TheBlackstaff(), 1, false, true, false));
    }

    @Override
    public void upp() {
        this.upgradeBaseCost(1);
    }
}
