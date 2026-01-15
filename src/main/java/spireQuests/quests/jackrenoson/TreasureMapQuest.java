package spireQuests.quests.jackrenoson;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Boot;
import com.megacrit.cardcrawl.relics.Shovel;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import spireQuests.Anniv8Mod;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.patches.ShowMarkedNodesOnMapPatch;
import spireQuests.quests.MarkNodeQuest;
import spireQuests.util.NodeUtil;
import spireQuests.util.TexLoader;

import java.util.ArrayList;
import java.util.Objects;

import static spireQuests.Anniv8Mod.makeID;

public class TreasureMapQuest extends AbstractQuest implements MarkNodeQuest {
    public static final Texture X = TexLoader.getTexture(Anniv8Mod.makeContributionPath("jackrenoson", "X.png"));
    private int startX, startY;
    public static final String id = makeID(TreasureMapQuest.class.getSimpleName());

    static class SaveNodeTracker extends Tracker { //Hijacking the tracker system to save origin node.
        private int startX, startY;
        public SaveNodeTracker(int x, int y){
            super();
            startX = x;
            startY = y;
        }
        public boolean isComplete() { return true; }
        public String progressString() { return ""; }

        @Override
        public String saveData() {
            return startX+","+startY;
        }

        @Override
        public void loadData(String data) {
            String[] parts = data.split(",");
            startX = Integer.parseInt(parts[0]);
            startY = Integer.parseInt(parts[1]);
        }
    }

    public TreasureMapQuest() {
        super(QuestType.SHORT, QuestDifficulty.NORMAL);
        startX = 0;
        startY = -1;
        if(CardCrawlGame.isInARun()) {
            MapRoomNode origin = AbstractDungeon.getCurrMapNode();
            startX = origin.x;
            startY = origin.y;
        }

        new TriggerTracker<>(QuestTriggers.ENTER_ROOM, 1)
                .triggerCondition(r -> ShowMarkedNodesOnMapPatch.ImageField.CheckMarks(r, id))
                .setFailureTrigger(QuestTriggers.BEFORE_ACT_CHANGE)
                .add(this);

        new SaveNodeTracker(startX, startY).hide().add(this);

        isAutoComplete = true;
        isAutoFail = true;
    }

    @Override
    public boolean canSpawn(){
        if(AbstractDungeon.getCurrRoom() instanceof ShopRoom) {
            ShopRoom shop = (ShopRoom) AbstractDungeon.getCurrRoom();
            for(AbstractRelic r : shop.relics) {
                if(Objects.equals(r.relicId, Shovel.ID)) return false;
            }
        }
        return !AbstractDungeon.player.hasRelic(Shovel.ID);
    }

    @Override
    public void onStart() {
        super.onStart();
        AbstractDungeon.rareRelicPool.remove(Shovel.ID);
    }

    @Override
    public void onComplete() {
        super.onComplete();
        AbstractDungeon.rareRelicPool.add(rng().random(AbstractDungeon.rareRelicPool.size() - 1), Shovel.ID);
    }

    @Override
    public void onFail() {
        super.onFail();
        AbstractDungeon.rareRelicPool.add(rng().random(AbstractDungeon.rareRelicPool.size() - 1), Shovel.ID);
    }

    @Override
    public void markNodes(ArrayList<ArrayList<MapRoomNode>> map, Random rng) {
        if (this.isFailed()) {
            return;
        }
        ArrayList<MapRoomNode> toBeChecked = new ArrayList<>();
        ArrayList<MapRoomNode> validRooms = new ArrayList<>();
        ArrayList<MapRoomNode> checkedRooms = new ArrayList<>();
        ArrayList<MapRoomNode> topRests = new ArrayList<>();
        toBeChecked.add(NodeUtil.getNode(startX, startY));
        while(!toBeChecked.isEmpty()) {
            MapRoomNode curr = toBeChecked.remove(0);
            if (curr == null || curr.y == -1) { //Neow room
                toBeChecked.addAll(map.get(0));
            } else {
                if (!checkedRooms.contains(curr)) {
                    if (curr.y == 14 && curr.getRoom() instanceof RestRoom) {
                        topRests.add(curr);
                    } else {
                        if(curr.getRoom() instanceof RestRoom){
                            validRooms.add(curr);
                        }
                        if (curr.hasEdges()) {
                            for (MapEdge edge : curr.getEdges()) {
                                MapRoomNode node = NodeUtil.getNode(edge);
                                if(node!=null) {
                                    toBeChecked.add(node);
                                }
                            }
                        }
                    }
                    checkedRooms.add(curr);
                }
            }
        }
        if (!topRests.isEmpty()) {
            validRooms.add(topRests.get(rng.random(0, topRests.size() - 1)));
        }
        MapRoomNode targetRoom = validRooms.get(rng.random(0, validRooms.size()-1));
        ShowMarkedNodesOnMapPatch.ImageField.MarkNode(targetRoom, id, X);
    }

    @Override
    public Random rng() {
        return new Random(Settings.seed ^ (long) AbstractDungeon.actNum * 31 ^ (long) (startY + 1) * 37 ^ (long) startX * 41 ^ id.hashCode());
    }
}
