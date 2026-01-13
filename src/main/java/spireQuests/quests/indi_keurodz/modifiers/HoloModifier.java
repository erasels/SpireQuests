package spireQuests.quests.indi_keurodz.modifiers;

import java.util.ArrayList;
import java.util.List;

import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import basemod.helpers.TooltipInfo;
import spireQuests.Anniv8Mod;

public class HoloModifier extends AbstractCardModifier {

    public static final String ID = Anniv8Mod.makeID("Holo");
    private static final Keyword KEYWORD = Anniv8Mod.keywords.get(ID);

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public List<TooltipInfo> additionalTooltips(AbstractCard card) {
        List<TooltipInfo> tips = new ArrayList<>();
        tips.add(new TooltipInfo(KEYWORD.PROPER_NAME, KEYWORD.DESCRIPTION));
        return tips;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                new StrengthPower(AbstractDungeon.player, 10)));
        addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                new LoseStrengthPower(AbstractDungeon.player, 10)));
    }

    @Override
    public boolean shouldApply(AbstractCard card) {
        return !CardModifierManager.hasModifier(card, ID);
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new HoloModifier();
    }

}
