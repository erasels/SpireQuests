package spireQuests.quests.modargo.cards.bloodfire;

import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.MultiCardPreview;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import spireQuests.abstracts.AbstractSQCard;

import static spireQuests.Anniv8Mod.*;

public class BloodfireRitualQuestboundPlaceholder extends AbstractSQCard {
    public static final String ID = makeID(BloodfireRitualQuestboundPlaceholder.class.getSimpleName());

    public BloodfireRitualQuestboundPlaceholder() {
        super(ID, "modargo", 1, CardType.SKILL, CardRarity.SPECIAL, CardTarget.SELF);
        MultiCardPreview.add(this, new FocusedTrance(), new BloodInTheChalice(), new PrimordialFlux(), new EssenceOfFlame(), new PowerFromTheDark());
        setBackgroundTexture(makeContributionPath("modargo", "BloodfireCardBackground.png"), makeContributionPath("modargo", "BloodfireCardBackground_p.png"));

    }

    @Override
    public void upp() {
        this.upgradeBaseCost(1);
        for (AbstractCard card : MultiCardPreview.multiCardPreview.get(this)) {
            card.upgrade();
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {}
}
