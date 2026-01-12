package spireQuests.quests.soytheproton;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Boot;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.soytheproton.relics.MagicBoot;

import java.util.ArrayList;

public class LuckyNumberQuest extends AbstractQuest {
    private AbstractRelic boot = new Boot();

    public LuckyNumberQuest() {
        super(QuestType.SHORT, QuestDifficulty.NORMAL);
        new TriggerTracker<>(QuestTriggers.BOOT_TRIGGER, 7)
                .setResetTrigger(QuestTriggers.COMBAT_END)
                .add(this);
        addReward(new QuestReward.RelicReward(new MagicBoot()));

        questboundRelics = new ArrayList<>();
        questboundRelics.add(boot);
        returnQuestboundRelics = false;
    }

    @Override
    public boolean canSpawn() {
        if(AbstractDungeon.getCurrRoom() instanceof ShopRoom) {
            ShopRoom shop = (ShopRoom) AbstractDungeon.getCurrRoom();
            for(AbstractRelic r : shop.relics) {
                if(Boot.ID.equals(r.relicId)) return false;
            }
        }
        return !AbstractDungeon.player.hasRelic(Boot.ID);
    }
}
