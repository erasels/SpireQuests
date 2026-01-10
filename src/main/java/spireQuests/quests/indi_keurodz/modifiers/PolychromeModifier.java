package spireQuests.quests.indi_keurodz.modifiers;

import java.util.ArrayList;
import java.util.List;

import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import basemod.helpers.TooltipInfo;
import spireQuests.Anniv8Mod;

public class PolychromeModifier extends AbstractCardModifier {

    public static final String MODIFIER_ID = Anniv8Mod.makeID("Polychrome");

    @Override
    public String identifier(AbstractCard card) {
        return MODIFIER_ID;
    }

    @Override
    public List<TooltipInfo> additionalTooltips(AbstractCard card) {
        List<TooltipInfo> tips = new ArrayList<>();
        tips.add(new TooltipInfo(Anniv8Mod.keywords.get("Polychrome").PROPER_NAME,
                Anniv8Mod.keywords.get("Polychrome").DESCRIPTION));
        return tips;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (!card.purgeOnUse && AbstractDungeon.cardRandomRng.randomBoolean()) {
            GameActionManager.queueExtraCard(card.makeStatEquivalentCopy(), (AbstractMonster) target);
        }
    }

    @Override
    public boolean shouldApply(AbstractCard card) {
        return !CardModifierManager.hasModifier(card, MODIFIER_ID);
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new PolychromeModifier();
    }

}
