package spireQuests.quests.modargo.cards.bloodfire;

import spireQuests.abstracts.AbstractSQCard;

import static spireQuests.Anniv8Mod.makeImagePath;

public abstract class BloodfireRitualCard extends AbstractSQCard {
    public BloodfireRitualCard(String ID) {
        super(ID, "modargo", 1, CardType.SKILL, CardRarity.SPECIAL, CardTarget.SELF);
        this.isEthereal = true;
        this.exhaust = true;
        setBackgroundTexture(makeImagePath("modargo/BloodfireCardBackground.png"), makeImagePath("modargo/BloodfireCardBackground_p.png"));
    }

    @Override
    public void upp() {
        this.upgradeBaseCost(0);
    }
}
