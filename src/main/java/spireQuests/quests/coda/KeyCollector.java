package spireQuests.quests.coda;

import com.megacrit.cardcrawl.core.Settings;

import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward.RelicReward;
import spireQuests.quests.coda.relics.KeyringRelic;

public class KeyCollector extends AbstractQuest {

    public KeyCollector() {
        super(QuestType.SHORT, QuestDifficulty.CHALLENGE);

        new PassiveTracker<Boolean>(() -> Settings.hasRubyKey, true)
            {
                @Override
                public String progressString() {
                    return "";
                }

                @Override
                public boolean hidden() {
                    return Settings.hasRubyKey;
                };
            }
            .setFailureTrigger(QuestTriggers.ACT_CHANGE)
            .add(this);
        new PassiveTracker<Boolean>(() -> Settings.hasEmeraldKey, true)
            {
                @Override
                public String progressString() {
                    return "";
                }

                @Override
                public boolean hidden() {
                    return Settings.hasEmeraldKey;
                };
            }
            .setFailureTrigger(QuestTriggers.ACT_CHANGE)
            .add(this);
        new PassiveTracker<Boolean>(() -> Settings.hasSapphireKey, true)
            {
                @Override
                public String progressString() {
                    return "";
                }

                @Override
                public boolean hidden() {
                    return Settings.hasSapphireKey;
                };
            }
            .setFailureTrigger(QuestTriggers.ACT_CHANGE)
            .add(this);

        addReward(new RelicReward(new KeyringRelic()));

        this.isAutoComplete = true;
    }

    @Override
    public boolean canSpawn() {
        return Settings.isFinalActAvailable;      
    }

}
