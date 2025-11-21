package spireQuests.quests.gk.cards;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import spireQuests.abstracts.AbstractSQCard;
import spireQuests.quests.gk.BasicProficiencyQuest;
import spireQuests.util.Wiz;

import static com.megacrit.cardcrawl.core.Settings.GREEN_TEXT_COLOR;

public abstract class AbstractBPCard extends AbstractSQCard {
    public AbstractBPCard(String cardID, String packageName, int cost, CardType type, CardRarity rarity, CardTarget target, CardColor color) {
        super(cardID, packageName, cost, type, rarity, target, color);
    }

    @Override
    protected void renderTitle(SpriteBatch sb) {
        boolean inCombat = Wiz.isInCombat();
        Color save = GREEN_TEXT_COLOR.cpy();
        Color renderColor = ReflectionHacks.getPrivate(this, AbstractCard.class, "renderColor");
        Color rcSave = renderColor.cpy();
        if (inCombat) {
            renderColor.set(BasicProficiencyQuest.TITLE_PURPLE);
            GREEN_TEXT_COLOR.set(BasicProficiencyQuest.TITLE_PURPLE);
        }
        super.renderTitle(sb);
        if (inCombat) {
            GREEN_TEXT_COLOR.set(save);
            renderColor.set(rcSave);
        }
    }
}
