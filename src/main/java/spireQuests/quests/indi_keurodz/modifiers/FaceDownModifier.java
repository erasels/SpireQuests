package spireQuests.quests.indi_keurodz.modifiers;

import com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.Anniv8Mod;
import spireQuests.patches.ShowMarkedNodesOnMapPatch;
import spireQuests.quests.indi_keurodz.BalatroQuest;

public class FaceDownModifier extends AbstractCardModifier {

    public static final String MODIFIER_ID = Anniv8Mod.makeID("FaceDownModifier");

    @Override
    public String identifier(AbstractCard card) {
        return MODIFIER_ID;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new EternalModifier();
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.isFlipped = true;
    }

    @Override
    public boolean removeOnCardPlayed(AbstractCard card) {
        return true;
    }

    @Override
    public void onRemove(AbstractCard card) {
        card.isFlipped = false;
    }

    @Override
    public boolean shouldApply(AbstractCard card) {
        return !CardModifierManager.hasModifier(card, MODIFIER_ID);
    }

    @Override
    public boolean removeAtEndOfTurn(AbstractCard card) {
        return ShowMarkedNodesOnMapPatch.ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.ID,
                BalatroQuest.BossBlind.Fish.frames);
    }

}
