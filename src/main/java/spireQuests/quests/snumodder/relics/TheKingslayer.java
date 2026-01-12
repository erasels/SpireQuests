package spireQuests.quests.snumodder.relics;

import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.abstracts.AbstractSQRelic;
import spireQuests.quests.snumodder.actions.DrawRareCardAction;

import static spireQuests.Anniv8Mod.makeID;

public class TheKingslayer extends AbstractSQRelic {
    private static final String ID = makeID(TheKingslayer.class.getSimpleName());

    public TheKingslayer() {
        super(ID, "snumodder", RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public void atBattleStart() {
        this.flash();
        this.addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
        this.addToBot(new DrawRareCardAction(1));
    }
}
