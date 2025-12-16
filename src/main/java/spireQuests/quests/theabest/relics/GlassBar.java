package spireQuests.quests.theabest.relics;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.abstracts.AbstractSQRelic;

import static spireQuests.Anniv8Mod.makeID;

public class GlassBar extends AbstractSQRelic {
    public static final String ID = makeID(GlassBar.class.getSimpleName());
    public GlassBar() {
        super(ID, "theabest", RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public void onPlayerEndTurn() {
        int block = 12 - 2 * AbstractDungeon.actionManager.cardsPlayedThisTurn.size();
        if (block > 0)
            addToBot(new GainBlockAction(AbstractDungeon.player, block));
    }
}
