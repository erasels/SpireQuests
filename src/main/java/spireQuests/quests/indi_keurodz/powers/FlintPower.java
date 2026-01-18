package spireQuests.quests.indi_keurodz.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;

import basemod.helpers.TooltipInfo;
import spireQuests.Anniv8Mod;
import spireQuests.abstracts.AbstractSQPower;
import spireQuests.quests.indi_keurodz.BalatroQuest;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;

public class FlintPower extends AbstractSQPower {
    public static final String ID = Anniv8Mod.makeID("FlintPower");
    public static final TooltipInfo tooltip = BossBlind.Flint.tooltip;

    public FlintPower(AbstractCreature owner) {
        super(ID,
                // remove custom icon from power title
                tooltip.title.replaceFirst("\\[.+\\]", ""),
                BalatroQuest.AUTHOR, PowerType.DEBUFF, true, owner, 1);
    }

    @Override
    public void atEndOfRound() {
        addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, ID));
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, ID));
    }

    @Override
    public void updateDescription() {
        this.description = tooltip.description;
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        return type == DamageType.NORMAL ? damage * 0.5F : damage;
    }

}
