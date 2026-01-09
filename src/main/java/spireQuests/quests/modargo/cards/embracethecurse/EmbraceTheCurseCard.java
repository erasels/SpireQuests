package spireQuests.quests.modargo.cards.embracethecurse;

import spireQuests.abstracts.AbstractSQCard;

import static spireQuests.Anniv8Mod.makeImagePath;

public abstract class EmbraceTheCurseCard extends AbstractSQCard {
    public EmbraceTheCurseCard(String ID, int cost, CardType type, CardTarget target) {
        super(ID, "modargo", cost, type, CardRarity.SPECIAL, target);
        setBackgroundTexture(makeImagePath("modargo/EmbraceTheCurseCardBackground.png"), makeImagePath("modargo/EmbraceTheCurseCardBackground_p.png"));
    }
}
