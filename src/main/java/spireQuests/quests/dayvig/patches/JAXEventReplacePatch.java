package spireQuests.quests.dayvig.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.EventHelper;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;
import spireQuests.quests.dayvig.AddictQuest;
import spireQuests.quests.dayvig.rooms.ForcedDrugsEvent;
import spireQuests.quests.dayvig.rooms.ForcedEventRoom;

public class JAXEventReplacePatch {
    @SpirePatch(clz = AbstractDungeon.class, method = "generateRoom", paramtypez = EventHelper.RoomResult.class)

    public static class JaxEventReplace {
        public static SpireReturn Prefix(AbstractDungeon __instance, EventHelper.RoomResult r) {
            for (AbstractQuest q : QuestManager.currentQuests.get(AbstractDungeon.player)){
                if (q instanceof AddictQuest) {
                    AddictQuest addictQ = (AddictQuest) q;
                    if (!addictQ.hasReplacedEvent){
                        ForcedEventRoom newAddictEvent = new ForcedEventRoom(new ForcedDrugsEvent());
                        addictQ.hasReplacedEvent = true;
                        return SpireReturn.Return(newAddictEvent);
                    }
                }
            }
            return SpireReturn.Continue();
        }
    }
}