package spireQuests.quests.indi_keurodz.modifiers;

import java.util.ArrayList;
import java.util.List;

import com.megacrit.cardcrawl.actions.common.GainGoldAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import basemod.helpers.TooltipInfo;
import spireQuests.Anniv8Mod;

public class FoilModifier extends AbstractCardModifier {

    public static final String MODIFIER_ID = Anniv8Mod.makeID("Foil");
    private boolean triggeredThisCombat = false;

    @Override
    public String identifier(AbstractCard card) {
        return MODIFIER_ID;
    }

    @Override
    public List<TooltipInfo> additionalTooltips(AbstractCard card) {
        List<TooltipInfo> tips = new ArrayList<>();
        tips.add(new TooltipInfo(Anniv8Mod.keywords.get("Foil").PROPER_NAME,
                Anniv8Mod.keywords.get("Foil").DESCRIPTION));
        return tips;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (!triggeredThisCombat) {
            addToBot(new GainGoldAction(50));
            triggeredThisCombat = true;
        }
    }

    @Override
    public boolean onBattleStart(AbstractCard card) {
        triggeredThisCombat = false;
        return false;
    }

    @Override
    public boolean shouldApply(AbstractCard card) {
        return !CardModifierManager.hasModifier(card, MODIFIER_ID);
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new FoilModifier();
    }

}
