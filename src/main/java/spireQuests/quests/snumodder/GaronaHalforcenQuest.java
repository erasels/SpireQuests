package spireQuests.quests.snumodder;

import basemod.helpers.CardPowerTip;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import spireQuests.Anniv8Mod;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.snumodder.cards.KingLlane;
import spireQuests.quests.snumodder.relics.TheKingslayer;

import java.util.ArrayList;
import java.util.List;

public class GaronaHalforcenQuest extends AbstractQuest {
    public GaronaHalforcenQuest() {
        super(QuestType.SHORT, QuestDifficulty.HARD);
        new TriggerTracker<>(QuestTriggers.COMBAT_END, 3)
                .triggerCondition(t ->
                        AbstractDungeon.player.exhaustPile.group.stream()
                                .noneMatch(c -> c.cardID.equals(KingLlane.ID)))
                .add(this);

        questboundCards = new ArrayList<>();
        questboundCards.add(new KingLlane());

        addReward(new QuestReward.RelicReward(new TheKingslayer()));
        titleScale = 1.0f;
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.floorNum < 43;
    }

    @Override
    public void makeTooltips(List<PowerTip> tipList) {
        super.makeTooltips(tipList);
        tipList.add(new CardPowerTip(new KingLlane()));
        tipList.add(new PowerTip(Anniv8Mod.keywords.get("Questbound").PROPER_NAME, Anniv8Mod.keywords.get("Questbound").DESCRIPTION));
    }
}
