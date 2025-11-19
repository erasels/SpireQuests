package spireQuests.quests.soytheproton;

import basemod.helpers.CardModifierManager;
import basemod.helpers.CardPowerTip;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.RitualDagger;
import com.megacrit.cardcrawl.cards.green.Accuracy;
import com.megacrit.cardcrawl.cards.green.GlassKnife;
import com.megacrit.cardcrawl.cards.green.GrandFinale;
import com.megacrit.cardcrawl.characters.TheSilent;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.potions.AttackPotion;
import com.megacrit.cardcrawl.potions.StrengthPotion;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import spireQuests.Anniv8Mod;
import spireQuests.cardmods.QuestboundMod;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.Trigger;
import spireQuests.quests.darkglade.ImpendingDay;
import spireQuests.util.Wiz;
import spireQuests.vfx.ShowCardandFakeObtainEffect;

import java.util.*;

public class GrandFinaleQuest extends AbstractQuest {
    public GrandFinaleQuest() {
        super(QuestType.SHORT, QuestDifficulty.HARD);

        new TriggerTracker<>(QuestTriggers.PLAY_CARD, 1)
                .triggerCondition((card) -> Objects.equals(card.cardID, GrandFinale.ID))
                .add(this);
        addReward(new QuestReward.GoldReward(150));

        questboundCards = new ArrayList<>();
        questboundCards.add(new GrandFinale());
    }

    @Override
    public void makeTooltips(List<PowerTip> tipList) {
        super.makeTooltips(tipList);
        tipList.add(new CardPowerTip(new GrandFinale()));
        tipList.add(new PowerTip(Anniv8Mod.keywords.get("Questbound").PROPER_NAME, Anniv8Mod.keywords.get("Questbound").DESCRIPTION));
    }
    @Override
    public boolean canSpawn() {
        return AbstractDungeon.player instanceof TheSilent;
    }
}
