package spireQuests.quests.indi_keurodz.Actions;

import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.quests.indi_keurodz.FaceDownModifier;

public class FlipStartingHandAction extends AbstractGameAction {
    @Override
    public void update() {
        for (AbstractCard card : AbstractDungeon.player.hand.group) {
            if (!CardModifierManager.hasModifier(card, FaceDownModifier.MODIFIER_ID)) {
                CardModifierManager.addModifier(card, new FaceDownModifier());
            }
        }
        isDone = true;
    }
}
