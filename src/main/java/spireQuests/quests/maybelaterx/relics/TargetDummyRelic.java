package spireQuests.quests.maybelaterx.relics;

import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.abstracts.AbstractSQRelic;
import spireQuests.cardmods.DamageAndBlockMod;

import static spireQuests.Anniv8Mod.makeID;

public class TargetDummyRelic extends AbstractSQRelic{

    private static final String ID = makeID(TargetDummyRelic.class.getSimpleName());

    public TargetDummyRelic() {
        super(ID, "maybelaterx", RelicTier.SPECIAL, LandingSound.HEAVY);
    }

    @Override
    public void onEquip() {
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group)
            if (c.rarity == AbstractCard.CardRarity.BASIC)
                CardModifierManager.addModifier(c, new DamageAndBlockMod(2, 2));
    }

    @Override
    public void onObtainCard(AbstractCard c) {
        if (c.rarity == AbstractCard.CardRarity.BASIC)
            CardModifierManager.addModifier(c, new DamageAndBlockMod(2, 2));
    }
}