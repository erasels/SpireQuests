package spireQuests.quests.maybelaterx.relics;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.abstracts.AbstractSQRelic;

import static spireQuests.Anniv8Mod.makeID;

public class BalancingStonesRelic extends AbstractSQRelic {

    public static final String ID = makeID(BalancingStonesRelic.class.getSimpleName());
    public BalancingStonesRelic() {
        super(ID, "maybelaterx", RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    public void onTrigger() {
        flash();
        this.addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
        this.addToBot(new DrawCardAction(AbstractDungeon.player, 1));
    }
}
