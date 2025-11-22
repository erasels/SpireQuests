package spireQuests.quests.mangochicken.relics;

import basemod.abstracts.cardbuilder.actionbuilder.ActionBuilder;
import com.evacipated.cardcrawl.mod.stslib.actions.tempHp.AddTemporaryHPAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import spireQuests.abstracts.AbstractSQRelic;
import spireQuests.util.Wiz;

import static spireQuests.Anniv8Mod.makeID;

public class HeartyAnchor extends AbstractSQRelic {
    public static final String ID = makeID(HeartyAnchor.class.getSimpleName());

    public HeartyAnchor() {
        super(ID, "mangochicken", RelicTier.SPECIAL, LandingSound.SOLID);
    }

    @Override
    public void atTurnStart() {
        if (GameActionManager.turn == 1 && Wiz.isInCombat()) {
            this.flash();
            addToBot(new AddTemporaryHPAction(AbstractDungeon.player, AbstractDungeon.player, 6));
        }
    }
}
