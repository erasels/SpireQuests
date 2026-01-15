package spireQuests.quests.jackrenoson;

import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.rooms.*;
import spireQuests.Anniv8Mod;
import spireQuests.patches.QuestTriggers;
import spireQuests.patches.ShowMarkedNodesOnMapPatch;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.MarkNodeQuest;
import spireQuests.quests.QuestManager;
import spireQuests.quests.QuestReward;
import spireQuests.quests.jackrenoson.relics.Sail;
import spireQuests.quests.modargo.MulticlassQuest;
import spireQuests.ui.QuestBoardScreen;
import spireQuests.util.NodeUtil;
import spireQuests.util.TexLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import static spireQuests.Anniv8Mod.makeID;

public class BoatRepairQuest extends AbstractQuest implements MarkNodeQuest, CustomSavable<ArrayList<Object>> {
    private int startX, startY;
    public static final String id = makeID(BoatRepairQuest.class.getSimpleName());
    public static final ArrayList<Texture> textures = new ArrayList<>(Arrays.asList(TexLoader.getTexture(Anniv8Mod.makeContributionPath("jackrenoson", "AnchorMark.png")), TexLoader.getTexture(Anniv8Mod.makeContributionPath("jackrenoson", "HornCleatMark.png")), TexLoader.getTexture(Anniv8Mod.makeContributionPath("jackrenoson", "CaptainsWheelMark.png"))));
    public boolean needAnchor, needHornCleat, needCaptainsWheel, hasShovel;
    public MapRoomNode markedX = null;
    public int curAct = 0;

    public BoatRepairQuest() {
        super(QuestType.LONG, QuestDifficulty.NORMAL);
        startX = 0;
        startY = -1;
        if(CardCrawlGame.isInARun()) {
            MapRoomNode origin = AbstractDungeon.getCurrMapNode();
            startX = origin.x;
            startY = origin.y;
        }

        new TriggerTracker<>(QuestTriggers.OBTAIN_RELIC, 1)
                .triggerCondition(r -> r.relicId.equals(Anchor.ID))
                .add(this);
        new TriggerTracker<>(QuestTriggers.OBTAIN_RELIC, 1)
                .triggerCondition(r -> r.relicId.equals(HornCleat.ID))
                .add(this);
        new TriggerTracker<>(QuestTriggers.OBTAIN_RELIC, 1)
                .triggerCondition(r -> r.relicId.equals(CaptainsWheel.ID))
                .add(this);

        addReward(new QuestReward.RelicReward(new Sail()));

        if (CardCrawlGame.isInARun()) {
            for (AbstractQuest q : QuestManager.getAllQuests()) {
                if (q instanceof TreasureMapQuest) {
                    for (ArrayList<MapRoomNode> row : AbstractDungeon.map) {
                        for (MapRoomNode node : row) {
                            if (ShowMarkedNodesOnMapPatch.ImageField.CheckMarks(node, TreasureMapQuest.id)) {
                                markedX = node;
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean canSpawn(){
        if(AbstractDungeon.actNum > 2 || (AbstractDungeon.actNum == 2 && AbstractDungeon.getCurrMapNode().y > AbstractDungeon.map.size()/2)){
            return false;
        }
        if(AbstractDungeon.getCurrRoom() instanceof ShopRoom) {
            ShopRoom shop = (ShopRoom) AbstractDungeon.getCurrRoom();
            for(AbstractRelic r : shop.relics) {
                if(r.relicId.equals(Anchor.ID) || r.relicId.equals(HornCleat.ID) || r.relicId.equals(CaptainsWheel.ID)) return false;
            }
        }
        return !AbstractDungeon.player.hasRelic(Anchor.ID) || AbstractDungeon.player.hasRelic(HornCleat.ID) || AbstractDungeon.player.hasRelic(CaptainsWheel.ID);
    }

    @Override
    public void onStart() {
        super.onStart();
        AbstractDungeon.commonRelicPool.remove(Anchor.ID);
        AbstractDungeon.uncommonRelicPool.remove(HornCleat.ID);
        AbstractDungeon.rareRelicPool.remove(CaptainsWheel.ID);
        updateSavables();
    }

    private void updateSavables(){
        curAct = AbstractDungeon.actNum;
        needAnchor = !AbstractDungeon.player.hasRelic(Anchor.ID);
        needHornCleat = !AbstractDungeon.player.hasRelic(HornCleat.ID);
        needCaptainsWheel = !AbstractDungeon.player.hasRelic(CaptainsWheel.ID);
        hasShovel = AbstractDungeon.player.hasRelic(Shovel.ID);
    }

    private MapRoomNode findNewRoom(MapRoomNode room1, MapRoomNode room2, Random rng){
        ArrayList<MapRoomNode> validRooms = new ArrayList<>();
        ArrayList<MapRoomNode> topCheckList = new ArrayList<>();
        ArrayList<MapRoomNode> botCheckList = new ArrayList<>();
        ArrayList<MapRoomNode> middleFromTop = new ArrayList<>();
        ArrayList<MapRoomNode> middleFromBot = new ArrayList<>();
        ArrayList<MapRoomNode> middleTopCheckList = new ArrayList<>();
        ArrayList<MapRoomNode> middleBotCheckList = new ArrayList<>();
        ArrayList<MapRoomNode> checkedRooms = new ArrayList<>();
        if(room1.y>room2.y){
            topCheckList.add(room1);
            botCheckList.add(room2);
            middleTopCheckList.add(room1);
            middleBotCheckList.add(room2);
        } else {
            topCheckList.add(room2);
            botCheckList.add(room1);
            middleTopCheckList.add(room2);
            middleBotCheckList.add(room1);
        }
        while (!(topCheckList.isEmpty())){
            MapRoomNode curr = topCheckList.remove(0);
            if(curr != null && curr.getRoom() != null) {
                checkedRooms.add(curr);
                for (MapEdge edge : curr.getEdges()) {
                    MapRoomNode node = NodeUtil.getNode(edge);
                    if (node != null && node.getRoom() != null && node.getRoom().getClass() != room1.getRoom().getClass() && node.getRoom().getClass() != room2.getRoom().getClass() && !(node.getRoom() instanceof MonsterRoom || (node.getRoom() instanceof RestRoom && !(hasShovel || curr.equals(markedX))))) {
                        validRooms.add(node);
                    }
                    topCheckList.add(node);
                }
            }
        }
        while(!(botCheckList.isEmpty())){
            MapRoomNode curr = botCheckList.remove(0);
            if(curr != null && curr.getRoom() != null) {
                checkedRooms.add(curr);
                for (MapRoomNode node : curr.getParents()) {
                    if (node != null && node.getRoom() != null && node.getRoom().getClass() != room1.getRoom().getClass() && node.getRoom().getClass() != room2.getRoom().getClass() && !(node.getRoom() instanceof MonsterRoom || (node.getRoom() instanceof RestRoom && !(hasShovel || curr.equals(markedX))))) {
                        validRooms.add(node);
                    }
                    botCheckList.add(node);
                }
            }
        }
        checkedRooms = new ArrayList<>();
        while(!(middleTopCheckList.isEmpty())){
            MapRoomNode curr = middleTopCheckList.remove(0);
            if(curr != null && curr.getRoom() != null) {
                checkedRooms.add(curr);
                for (MapRoomNode node : curr.getParents()) {
                    if (node != null && node.getRoom() != null && node.getRoom().getClass() != room1.getRoom().getClass() && node.getRoom().getClass() != room2.getRoom().getClass() && !(node.getRoom() instanceof MonsterRoom || (node.getRoom() instanceof RestRoom && !(hasShovel || curr.equals(markedX))))) {
                        middleFromTop.add(node);
                    }
                    if (node.y > Math.min(room1.y, room2.y))
                        middleTopCheckList.add(node);
                }
            }
        }
        checkedRooms = new ArrayList<>();
        while(!(middleBotCheckList.isEmpty())){
            MapRoomNode curr = middleBotCheckList.remove(0);
            if(curr != null && curr.getRoom() != null) {
                checkedRooms.add(curr);
                for (MapEdge edge : curr.getEdges()) {
                    MapRoomNode node = NodeUtil.getNode(edge);
                    if (node != null && node.getRoom() != null && node.getRoom().getClass() != room1.getRoom().getClass() && node.getRoom().getClass() != room2.getRoom().getClass() && !(node.getRoom() instanceof MonsterRoom || (node.getRoom() instanceof RestRoom && !(hasShovel || curr.equals(markedX))))) {
                        middleFromBot.add(node);
                    }
                    if (node.y < Math.min(room1.y, room2.y))
                        middleBotCheckList.add(node);
                }
            }
        }
        for(MapRoomNode node : middleFromTop){
            if(middleFromBot.contains(node)){
                validRooms.add(node);
            }
        }
        return validRooms.get(rng.random(validRooms.size()-1));
    }

    @Override
    public void markNodes(ArrayList<ArrayList<MapRoomNode>> map, Random rng) {
        if(curAct != AbstractDungeon.actNum){
            updateSavables();
            markedX = null;
        }
        if (AbstractDungeon.actNum >= 3) {
            MapRoomNode room1 = null;
            while (room1 == null || room1.getRoom() == null || room1.getRoom() instanceof MonsterRoom || (room1.getRoom() instanceof RestRoom && (!hasShovel) || room1.equals(markedX))) {
                int y = rng.random(AbstractDungeon.map.size() - 1);
                room1 = AbstractDungeon.map.get(y).get(rng.random(AbstractDungeon.map.get(y).size() - 1));
            }
            if(needAnchor) {
                ShowMarkedNodesOnMapPatch.ImageField.MarkNode(room1, id, textures.get(0));
            }
            MapRoomNode room2 = findNewRoom(room1, room1, rng);
            if(needHornCleat) {
                ShowMarkedNodesOnMapPatch.ImageField.MarkNode(room2, id, textures.get(1));
            }
            MapRoomNode room3 = findNewRoom(room1, room2, rng);
            if(needCaptainsWheel) {
                ShowMarkedNodesOnMapPatch.ImageField.MarkNode(room3, id, textures.get(2));
            }
        } else {
            ArrayList<MapRoomNode> toBeChecked = new ArrayList<>();
            ArrayList<MapRoomNode> restRooms = new ArrayList<>();
            ArrayList<MapRoomNode> shopRooms = new ArrayList<>();
            ArrayList<MapRoomNode> eliteRooms = new ArrayList<>();
            ArrayList<MapRoomNode> treasureRooms = new ArrayList<>();
            ArrayList<MapRoomNode> eventRooms = new ArrayList<>();
            ArrayList<MapRoomNode> checkedRooms = new ArrayList<>();
            toBeChecked.add(NodeUtil.getNode(startX, startY));
            while (!toBeChecked.isEmpty()) {
                MapRoomNode curr = toBeChecked.remove(0);
                if (curr == null || curr.y == -1) { //Neow room
                    toBeChecked.addAll(map.get(0));
                } else {
                    if (!checkedRooms.contains(curr) && curr.getRoom() != null) {
                        switch (curr.getRoom().getClass().toString()) {
                            case "class com.megacrit.cardcrawl.rooms.RestRoom":
                                if (hasShovel || curr.equals(markedX))
                                    restRooms.add(curr);
                                break;
                            case "class com.megacrit.cardcrawl.rooms.TreasureRoom":
                                treasureRooms.add(curr);
                                break;
                            case "class com.megacrit.cardcrawl.rooms.EventRoom":
                                eventRooms.add(curr);
                                break;
                            case "class com.megacrit.cardcrawl.rooms.MonsterRoomElite":
                                eliteRooms.add(curr);
                                break;
                            case "class com.megacrit.cardcrawl.rooms.ShopRoom":
                                shopRooms.add(curr);
                                break;
                        }
                        if (curr.hasEdges()) {
                            for (MapEdge edge : curr.getEdges()) {
                                MapRoomNode node = NodeUtil.getNode(edge);
                                if (node != null) {
                                    toBeChecked.add(node);
                                }
                            }
                        }
                        checkedRooms.add(curr);
                    }
                }
            }
            ArrayList<ArrayList<MapRoomNode>> lists = new ArrayList<>();
            lists.add(restRooms);
            lists.add(treasureRooms);
            lists.add(eventRooms);
            lists.add(eliteRooms);
            lists.add(shopRooms);

            ArrayList<ArrayList<MapRoomNode>> shuffledLists = new ArrayList<>();
            while (!lists.isEmpty()) {
                ArrayList<MapRoomNode> tempList = lists.remove(rng.random(lists.size() - 1));
                if (!tempList.isEmpty()) {
                    shuffledLists.add(tempList);
                }
            }
            for (int i = 0; i < Math.min(3, shuffledLists.size()); i++) {
                ArrayList<MapRoomNode> list = shuffledLists.get(i);
                MapRoomNode targetRoom = list.get(rng.random(0, list.size() - 1));
                if (Arrays.asList(needAnchor, needHornCleat, needCaptainsWheel).get(i)) {
                    ShowMarkedNodesOnMapPatch.ImageField.MarkNode(targetRoom, id, textures.get(i));
                }
            }
        }
    }

    @Override
    public Random rng() {
        return new Random(Settings.seed ^ (long) AbstractDungeon.actNum * 31 ^ (long) (startY + 1) * 37 ^ (long) startX * 41 ^ id.hashCode());
    }

    @Override
    public ArrayList<Object> onSave() {
        return new ArrayList<>(Arrays.asList(needAnchor, needHornCleat, needCaptainsWheel, hasShovel, markedX, startX, startY));
    }

    @Override
    public void onLoad(ArrayList<Object> list) {
        needAnchor = (boolean) list.get(0);
        needHornCleat = (boolean) list.get(1);
        needCaptainsWheel = (boolean) list.get(2);
        hasShovel = (boolean) list.get(3);
        markedX = (MapRoomNode) list.get(4);
        startX = (int) list.get(5);
        startY = (int) list.get(6);
    }
}
