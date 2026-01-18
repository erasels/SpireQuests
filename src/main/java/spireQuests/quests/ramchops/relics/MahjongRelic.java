package spireQuests.quests.ramchops.relics;

import basemod.helpers.CardPowerTip;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.tempCards.Miracle;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.abstracts.AbstractSQCard;
import spireQuests.abstracts.AbstractSQRelic;
import spireQuests.quests.ramchops.cards.BlessingEarth;
import spireQuests.quests.ramchops.cards.BlessingHeaven;
import spireQuests.quests.ramchops.cards.BlessingMan;
import spireQuests.util.Wiz;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

import static spireQuests.Anniv8Mod.makeID;

public class MahjongRelic extends AbstractSQRelic {
    public static String ID = makeID(MahjongRelic.class.getSimpleName());

    public MahjongRelic() {
        super(ID, "ramchops", RelicTier.SPECIAL, LandingSound.SOLID);
    }

    @Override
    public void atBattleStartPreDraw() {
        this.flash();

        ArrayList<AbstractCard> cardList = new ArrayList<>();

        cardList.add(new BlessingHeaven());
        cardList.add(new BlessingEarth());
        cardList.add(new BlessingMan());

        AbstractCard addThis = Wiz.getRandomItem(cardList, AbstractDungeon.cardRandomRng);

        this.addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
        this.addToBot(new MakeTempCardInHandAction(addThis, 1, false));
    }
}
