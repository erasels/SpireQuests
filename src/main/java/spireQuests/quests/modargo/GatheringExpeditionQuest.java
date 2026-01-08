package spireQuests.quests.modargo;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import com.megacrit.cardcrawl.rooms.TreasureRoom;
import spireQuests.Anniv8Mod;
import spireQuests.patches.QuestTriggers;
import spireQuests.questStats.StatRewardBox;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.MarkNodeQuest;
import spireQuests.quests.QuestReward;
import spireQuests.patches.ShowMarkedNodesOnMapPatch;
import spireQuests.quests.modargo.relics.FloralGarland;
import spireQuests.quests.modargo.relics.JewelledBauble;
import spireQuests.quests.modargo.relics.SpireAlloyHelmet;
import spireQuests.util.TexLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class GatheringExpeditionQuest extends AbstractQuest implements MarkNodeQuest {
    private static final Texture FLOWERS_IMAGE = TexLoader.getTexture(Anniv8Mod.makeContributionPath("modargo", "FlowersMapIcon.png"));
    private static final Texture MINERALS_IMAGE = TexLoader.getTexture(Anniv8Mod.makeContributionPath("modargo", "MineralsMapIcon.png"));
    private static final Texture GEMS_IMAGE = TexLoader.getTexture(Anniv8Mod.makeContributionPath("modargo", "GemsMapIcon.png"));

    public GatheringExpeditionQuest() {
        super(QuestType.LONG, QuestDifficulty.NORMAL);

        Tracker tracker = new TriggerTracker<>(QuestTriggers.ENTER_ROOM, 9)
                .triggerCondition(r -> ShowMarkedNodesOnMapPatch.ImageField.CheckMarks(r, id))
                .add(this);
        tracker.text = this.getTrackerText();

        addReward(new QuestReward.RelicReward(this.getRelic()));
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.floorNum == 0;
    }

    @Override
    public String getDescription() {
        ExpeditionFlavor flavor = getFlavor();
        if (flavor == null) {
            return super.getDescription();
        }
        switch (flavor) {
            case Flowers: return questStrings.EXTRA_TEXT[0];
            case Minerals: return questStrings.EXTRA_TEXT[1];
            default: return questStrings.EXTRA_TEXT[2];
        }
    }

    @Override
    public ArrayList<StatRewardBox> getStatRewardBoxes() {
        ArrayList<StatRewardBox> ret = new ArrayList<>();

        ret.add(new StatRewardBox(new QuestReward.RelicReward(new FloralGarland())));
        ret.add(new StatRewardBox(new QuestReward.RelicReward(new SpireAlloyHelmet())));
        ret.add(new StatRewardBox(new QuestReward.RelicReward(new JewelledBauble())));

        return ret;
    }

    private String getTrackerText() {
        ExpeditionFlavor flavor = getFlavor();
        if (flavor == null) {
            return questStrings.TRACKER_TEXT[0];
        }
        switch (flavor) {
            case Flowers: return questStrings.TRACKER_TEXT[1];
            case Minerals: return questStrings.TRACKER_TEXT[2];
            default: return questStrings.TRACKER_TEXT[3];
        }
    }

    private AbstractRelic getRelic() {
        ExpeditionFlavor flavor = getFlavor();
        if (flavor == null) {
            return new Circlet();
        }
        switch (flavor) {
            case Flowers: return new FloralGarland();
            case Minerals: return new SpireAlloyHelmet();
            default: return new JewelledBauble();
        }
    }

    private static Texture getTexture() {
        ExpeditionFlavor flavor = getFlavor();
        if (flavor == null) {
            throw new RuntimeException("Gathering Expedition flavor cannot be null when determining which texture to use.");
        }
        switch (flavor) {
            case Flowers: return FLOWERS_IMAGE;
            case Minerals: return MINERALS_IMAGE;
            default: return GEMS_IMAGE;
        }
    }

    public void markNodes(ArrayList<ArrayList<MapRoomNode>> map, Random rng) {
        List<MapRoomNode> possibleNodes = new ArrayList<>();
        for (int i = 0; i < map.size(); i++) {
            for (int j = 0; j < map.get(i).size(); j++) {
                MapRoomNode node = map.get(i).get(j);
                if (node.hasEdges() && !(AbstractDungeon.actNum == 1 && node.y == 0)) {
                    if (!(node.getRoom() instanceof ShopRoom) && !(node.getRoom() instanceof TreasureRoom)) {
                        possibleNodes.add(node);
                    }
                }
            }
        }

        Collections.shuffle(possibleNodes, new java.util.Random(rng.randomLong()));
        Supplier<Integer> f = () -> (rng.randomBoolean() ? 0 : 1);
        int n = 4 + (AbstractDungeon.actNum == 3 ? 1 : 0) + (AbstractDungeon.actNum == 2 ? f.get() : 0)  + f.get() + f.get();
        n = AbstractDungeon.actNum > 3 ? 1 : n;
        n = Math.min(n, possibleNodes.size());
        for (int i = 0; i < n; i++) {
            MapRoomNode node = possibleNodes.get(i);
            ShowMarkedNodesOnMapPatch.ImageField.MarkNode(node, id, getTexture());
        }
    }

    private enum ExpeditionFlavor {
        Flowers,
        Minerals,
        Gems
    }

    private static ExpeditionFlavor getFlavor() {
        if (!CardCrawlGame.isInARun()) {
            return null;
        }
        switch ((int) (Settings.seed % 3)) {
            case 0: return ExpeditionFlavor.Flowers;
            case 1: return ExpeditionFlavor.Minerals;
            default: return ExpeditionFlavor.Gems;
        }
    }
}
