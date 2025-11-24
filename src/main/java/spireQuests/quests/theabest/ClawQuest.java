package spireQuests.quests.theabest;

import basemod.helpers.CardPowerTip;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.Claw;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.theabest.relics.NailPolish;

import java.util.List;
import java.util.Objects;

public class ClawQuest extends AbstractQuest {
    public ClawQuest() {
        super(QuestType.SHORT, QuestDifficulty.EASY);
        new TriggerTracker<>(QuestTriggers.PLAY_CARD, 30)
                .triggerCondition((card) -> Objects.equals(card.cardID, Claw.ID))
                .add(this);
        addReward(new QuestReward.RelicReward(new NailPolish()));
    }

    @Override
    public void makeTooltips(List<PowerTip> tipList) {
        super.makeTooltips(tipList);
        tipList.add(new CardPowerTip(new Claw()));
    }

    @Override
    public boolean canSpawn() {
        int clawCount = 0;
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (Objects.equals(c.cardID, Claw.ID)) {
                clawCount++;
                if (clawCount >= 2)
                    return true;
            }
        }
        return false;
    }
}
