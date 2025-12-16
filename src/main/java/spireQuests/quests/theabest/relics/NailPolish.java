package spireQuests.quests.theabest.relics;

import com.evacipated.cardcrawl.mod.stslib.patches.bothInterfaces.OnCreateCardInterface;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.Claw;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.abstracts.AbstractSQRelic;

import java.util.Objects;

import static spireQuests.Anniv8Mod.makeID;

public class NailPolish extends AbstractSQRelic implements OnCreateCardInterface {
    public static final String ID = makeID(NailPolish.class.getSimpleName());
    public NailPolish() {
        super(ID, "theabest", RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public void onObtainCard(AbstractCard c) {
        if (Objects.equals(c.cardID, Claw.ID) && !c.upgraded)
            c.upgrade();
    }

    @Override
    public void onPreviewObtainCard(AbstractCard c) {
        if (Objects.equals(c.cardID, Claw.ID) && !c.upgraded)
            c.upgrade();
    }

    @Override
    public void onEquip() {
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (Objects.equals(c.cardID, Claw.ID) && !c.upgraded)
                c.upgrade();
        }
    }

    @Override
    public void onCreateCard(AbstractCard c) {
        if (Objects.equals(c.cardID, Claw.ID) && !c.upgraded)
            c.upgrade();
    }
}
