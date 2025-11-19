package spireQuests.quests.modargo.relics;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.abstracts.AbstractSQRelic;

import static spireQuests.Anniv8Mod.makeID;

public class PeasantsTunic extends AbstractSQRelic {
    public static final String ID = makeID(PeasantsTunic.class.getSimpleName());

    public PeasantsTunic() {
        super(ID, "modargo", RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card.rarity == AbstractCard.CardRarity.COMMON) {
            this.addToBot(new GainBlockAction(AbstractDungeon.player, 1));
        }
    }
}
