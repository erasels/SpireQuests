package spireQuests.quests.jackrenoson;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.jackrenoson.relics.Whelp;

import static spireQuests.Anniv8Mod.makeID;

public class HoardQuest extends AbstractQuest {
    public static final String id = makeID(HoardQuest .class.getSimpleName());

    public HoardQuest() {
        super(QuestType.LONG, QuestDifficulty.NORMAL);

        Tracker goldTracker = new PassiveTracker<>(() -> AbstractDungeon.player.gold >= 500, true)
                .add(this);

        addReward(new QuestReward.RelicReward(new Whelp()));
    }

    @Override
    public boolean canSpawn(){
        return AbstractDungeon.player.gold < 300;
    }
}
