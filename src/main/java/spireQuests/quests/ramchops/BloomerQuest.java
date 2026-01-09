package spireQuests.quests.ramchops;

import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.MarkOfTheBloom;
import com.megacrit.cardcrawl.relics.NlothsMask;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;

import java.util.ArrayList;

import static spireQuests.util.Wiz.adp;

public class BloomerQuest extends AbstractQuest {
    public BloomerQuest() {
        super(QuestType.SHORT, QuestDifficulty.HARD);

        new TriggerTracker<>(QuestTriggers.LEAVE_ROOM, 10).add(this).setFailureTrigger(QuestTriggers.LEAVE_ROOM, (node)->
                !adp().hasRelic(MarkOfTheBloom.ID));

        this.isAutoComplete = true;

        questboundRelics = new ArrayList<>();
        questboundRelics.add(new MarkOfTheBloom());

        addReward(new QuestReward.RandomRelicReward(AbstractRelic.RelicTier.RARE));
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.actNum <= 2;
    }
}
