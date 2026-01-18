package spireQuests.quests.jackrenoson;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.PrismaticShard;
import com.megacrit.cardcrawl.relics.QuestionCard;
import com.megacrit.cardcrawl.relics.Vajra;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;
import spireQuests.quests.QuestReward;
import spireQuests.quests.jackrenoson.relics.Whelp;
import spireQuests.quests.modargo.MulticlassQuest;
import spireQuests.quests.modargo.relics.MulticlassEmblem;

import java.util.ArrayList;
import java.util.Objects;

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
