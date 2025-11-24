package spireQuests.quests.theabest;

import basemod.helpers.CardPowerTip;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.Claw;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.theabest.relics.GlassBar;
import spireQuests.quests.theabest.relics.NailPolish;

import java.util.List;
import java.util.Objects;

public class WaitForItQuest extends AbstractQuest {
    public WaitForItQuest() {
        super(QuestType.LONG, QuestDifficulty.NORMAL);
        new TriggerTracker<>(QuestTriggers.TURN_END, 10)
                .triggerCondition((x) -> AbstractDungeon.actionManager.cardsPlayedThisTurn.isEmpty())
                .add(this);
        addReward(new QuestReward.RelicReward(new GlassBar()));
    }
}
