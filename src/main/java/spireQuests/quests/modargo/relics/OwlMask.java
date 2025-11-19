package spireQuests.quests.modargo.relics;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.EventRoom;
import spireQuests.abstracts.AbstractSQRelic;

import static spireQuests.Anniv8Mod.makeID;

public class OwlMask extends AbstractSQRelic {
    public static final String ID = makeID(OwlMask.class.getSimpleName());

    public OwlMask() {
        super(ID, "modargo", RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public void onEnterRoom(AbstractRoom room) {
        if (room instanceof EventRoom) {
            this.flash();
            AbstractDungeon.player.increaseMaxHp(1, true);
        }
    }
}
