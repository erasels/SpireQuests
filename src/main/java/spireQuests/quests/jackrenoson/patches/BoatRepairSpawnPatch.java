package spireQuests.quests.jackrenoson.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
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
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Anchor;
import com.megacrit.cardcrawl.relics.CaptainsWheel;
import com.megacrit.cardcrawl.relics.HornCleat;
import spireQuests.patches.ShowMarkedNodesOnMapPatch;
import spireQuests.quests.jackrenoson.BoatRepairQuest;

import java.util.ArrayList;

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

    @SpirePatch2(clz = AbstractDungeon.class, method = "generateEvent")
    public static class spawnRelicGivingEvent {
        @SpirePrefixPatch
        public static SpireReturn<AbstractEvent> spawnRelicGivingEventFirst(Random rng) {
            MapRoomNode curr = AbstractDungeon.getCurrMapNode();
            if (ShowMarkedNodesOnMapPatch.ImageField.CheckMarks(curr, BoatRepairQuest.id)) {
                ArrayList<AbstractEvent> relicGivingEvents = new ArrayList();
                ArrayList<String> eventStrings = new ArrayList();
                if(AbstractDungeon.actNum==1){
                    if(AbstractDungeon.eventList.contains(BigFish.ID)) {
                        relicGivingEvents.add(new BigFish());
                        eventStrings.add(BigFish.ID);
                    }
                    if(AbstractDungeon.eventList.contains(DeadAdventurer.ID)){
                        relicGivingEvents.add(new DeadAdventurer());
                        eventStrings.add(DeadAdventurer.ID);
                    }
                    if(AbstractDungeon.eventList.contains(ScrapOoze.ID)){
                        relicGivingEvents.add(new ScrapOoze());
                        eventStrings.add(ScrapOoze.ID);
                    }
                }
                if(AbstractDungeon.actNum==2){
                    if(AbstractDungeon.eventList.contains(Colosseum.ID)){
                        relicGivingEvents.add(new Colosseum());
                        eventStrings.add(Colosseum.ID);
                    }
                    if(AbstractDungeon.eventList.contains(TheMausoleum.ID)){
                        relicGivingEvents.add(new TheMausoleum());
                        eventStrings.add(TheMausoleum.ID);
                    }
                    if(AbstractDungeon.eventList.contains(Addict.ID)){
                        relicGivingEvents.add(new Addict());
                        eventStrings.add(Addict.ID);
                    }
                }
                if(AbstractDungeon.actNum==3){
                    if(AbstractDungeon.eventList.contains(MindBloom.ID)){
                        relicGivingEvents.add(new MindBloom());
                        eventStrings.add(MindBloom.ID);
                    }
                } else {
                    if(AbstractDungeon.eventList.contains(GremlinWheelGame.ID)){
                        relicGivingEvents.add(new GremlinWheelGame());
                        eventStrings.add(GremlinWheelGame.ID);
                    }
                }
                if(AbstractDungeon.eventList.contains(WeMeetAgain.ID) || relicGivingEvents.isEmpty()) {
                    relicGivingEvents.add(new WeMeetAgain());
                    eventStrings.add(WeMeetAgain.ID);
                }
                int r = rng.random(relicGivingEvents.size() - 1);
                AbstractEvent chosenEvent = relicGivingEvents.get(r);
                AbstractDungeon.eventList.remove(eventStrings.get(r));
                return SpireReturn.Return(chosenEvent);
            }
            return SpireReturn.Continue();
        }
    }


}
