package spireQuests.quests.jackrenoson.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.beyond.MindBloom;
import com.megacrit.cardcrawl.events.city.Addict;
import com.megacrit.cardcrawl.events.city.Colosseum;
import com.megacrit.cardcrawl.events.city.TheMausoleum;
import com.megacrit.cardcrawl.events.exordium.BigFish;
import com.megacrit.cardcrawl.events.exordium.DeadAdventurer;
import com.megacrit.cardcrawl.events.exordium.ScrapOoze;
import com.megacrit.cardcrawl.events.shrines.GremlinWheelGame;
import com.megacrit.cardcrawl.events.shrines.WeMeetAgain;
import com.megacrit.cardcrawl.helpers.EventHelper;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Anchor;
import com.megacrit.cardcrawl.relics.CaptainsWheel;
import com.megacrit.cardcrawl.relics.HornCleat;
import spireQuests.patches.ShowMarkedNodesOnMapPatch;
import spireQuests.quests.jackrenoson.BoatRepairQuest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BoatRepairSpawnPatch {

    @SpirePatch2(clz = AbstractDungeon.class, method = "returnRandomRelicKey")
    public static class checkBoatMarker {
        @SpirePrefixPatch
        public static SpireReturn<String> checkForBoatRelicsFirst(AbstractRelic.RelicTier tier) {
            MapRoomNode curr = AbstractDungeon.getCurrMapNode();
            if (ShowMarkedNodesOnMapPatch.ImageField.CheckMarks(curr, BoatRepairQuest.id, BoatRepairQuest.textures.get(0))) {
                ShowMarkedNodesOnMapPatch.ImageField.ClearMark(curr, BoatRepairQuest.id, BoatRepairQuest.textures.get(0));
                return SpireReturn.Return(Anchor.ID);
            } else if (ShowMarkedNodesOnMapPatch.ImageField.CheckMarks(curr, BoatRepairQuest.id, BoatRepairQuest.textures.get(1))) {
                ShowMarkedNodesOnMapPatch.ImageField.ClearMark(curr, BoatRepairQuest.id, BoatRepairQuest.textures.get(1));
                return SpireReturn.Return(HornCleat.ID);
            } else if (ShowMarkedNodesOnMapPatch.ImageField.CheckMarks(curr, BoatRepairQuest.id, BoatRepairQuest.textures.get(2))) {
                ShowMarkedNodesOnMapPatch.ImageField.ClearMark(curr, BoatRepairQuest.id, BoatRepairQuest.textures.get(2));
                return SpireReturn.Return(CaptainsWheel.ID);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz = AbstractDungeon.class, method = "returnEndRandomRelicKey")
    public static class checkBoatMarkerShop {
        @SpirePrefixPatch
        public static SpireReturn<String> checkForBoatRelicsFirstShop(AbstractRelic.RelicTier tier) {
            MapRoomNode curr = AbstractDungeon.getCurrMapNode();
            if (ShowMarkedNodesOnMapPatch.ImageField.CheckMarks(curr, BoatRepairQuest.id, BoatRepairQuest.textures.get(0))) {
                ShowMarkedNodesOnMapPatch.ImageField.ClearMark(curr, BoatRepairQuest.id, BoatRepairQuest.textures.get(0));
                return SpireReturn.Return(Anchor.ID);
            } else if (ShowMarkedNodesOnMapPatch.ImageField.CheckMarks(curr, BoatRepairQuest.id, BoatRepairQuest.textures.get(1))) {
                ShowMarkedNodesOnMapPatch.ImageField.ClearMark(curr, BoatRepairQuest.id, BoatRepairQuest.textures.get(1));
                return SpireReturn.Return(HornCleat.ID);
            } else if (ShowMarkedNodesOnMapPatch.ImageField.CheckMarks(curr, BoatRepairQuest.id, BoatRepairQuest.textures.get(2))) {
                ShowMarkedNodesOnMapPatch.ImageField.ClearMark(curr, BoatRepairQuest.id, BoatRepairQuest.textures.get(2));
                return SpireReturn.Return(CaptainsWheel.ID);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz = EventHelper.class, method = "roll", paramtypez = { Random.class })
    public static class forceRoomThatCanGiveRelicPatch {
        @SpirePostfixPatch
        public static EventHelper.RoomResult forceRoomThatCanGiveRelic(Random eventRng, EventHelper.RoomResult __result) {
            if (ShowMarkedNodesOnMapPatch.ImageField.CheckMarks(AbstractDungeon.getCurrMapNode(), BoatRepairQuest.id)) {
                List<EventHelper.RoomResult> relicGivingResults = Arrays.asList(EventHelper.RoomResult.EVENT, EventHelper.RoomResult.ELITE, EventHelper.RoomResult.TREASURE, EventHelper.RoomResult.SHOP);
                if(!relicGivingResults.contains(__result)) {
                    return EventHelper.RoomResult.EVENT;
                }
            }
            return __result;
        }
    }

    @SpirePatch2(clz = AbstractDungeon.class, method = "generateEvent")
    public static class spawnRelicGivingEvent {
        @SpirePrefixPatch
        public static SpireReturn<AbstractEvent> spawnRelicGivingEventFirst(Random rng) {
            MapRoomNode curr = AbstractDungeon.getCurrMapNode();
            if (ShowMarkedNodesOnMapPatch.ImageField.CheckMarks(curr, BoatRepairQuest.id)) {
                ArrayList<String> eventStrings = new ArrayList<>();
                if(AbstractDungeon.actNum==1){
                    if(AbstractDungeon.eventList.contains(BigFish.ID)) {
                        eventStrings.add(BigFish.ID);
                    }
                    if(AbstractDungeon.eventList.contains(DeadAdventurer.ID)){
                        eventStrings.add(DeadAdventurer.ID);
                    }
                    if(AbstractDungeon.eventList.contains(ScrapOoze.ID)){
                        eventStrings.add(ScrapOoze.ID);
                    }
                }
                if(AbstractDungeon.actNum==2){
                    if(AbstractDungeon.eventList.contains(Colosseum.ID)){
                        eventStrings.add(Colosseum.ID);
                    }
                    if(AbstractDungeon.eventList.contains(TheMausoleum.ID)){
                        eventStrings.add(TheMausoleum.ID);
                    }
                    if(AbstractDungeon.eventList.contains(Addict.ID)){
                        eventStrings.add(Addict.ID);
                    }
                }
                if(AbstractDungeon.actNum==3){
                    if(AbstractDungeon.eventList.contains(MindBloom.ID)){
                        eventStrings.add(MindBloom.ID);
                    }
                } else {
                    if(AbstractDungeon.eventList.contains(GremlinWheelGame.ID)){
                        eventStrings.add(GremlinWheelGame.ID);
                    }
                }
                if(AbstractDungeon.eventList.contains(WeMeetAgain.ID) || eventStrings.isEmpty()) {
                    eventStrings.add(WeMeetAgain.ID);
                }
                int r = rng.random(eventStrings.size() - 1);
                String eventId = eventStrings.get(r);
                AbstractDungeon.eventList.remove(eventId);
                return SpireReturn.Return(EventHelper.getEvent(eventId));
            }
            return SpireReturn.Continue();
        }
    }


}
