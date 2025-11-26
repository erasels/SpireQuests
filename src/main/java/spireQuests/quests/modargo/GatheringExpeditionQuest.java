package spireQuests.quests.modargo;

import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import com.megacrit.cardcrawl.rooms.TreasureRoom;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import spireQuests.Anniv8Mod;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;
import spireQuests.quests.QuestReward;
import spireQuests.quests.modargo.patches.ShowMarkedNodesOnMapPatch;
import spireQuests.quests.modargo.relics.FloralGarland;
import spireQuests.quests.modargo.relics.JewelledBauble;
import spireQuests.quests.modargo.relics.SpireAlloyHelmet;
import spireQuests.util.TexLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class GatheringExpeditionQuest extends AbstractQuest {
    private static final Texture FLOWERS_IMAGE = TexLoader.getTexture(Anniv8Mod.makeContributionPath("modargo", "FlowersMapIcon.png"));
    private static final Texture MINERALS_IMAGE = TexLoader.getTexture(Anniv8Mod.makeContributionPath("modargo", "MineralsMapIcon.png"));
    private static final Texture GEMS_IMAGE = TexLoader.getTexture(Anniv8Mod.makeContributionPath("modargo", "GemsMapIcon.png"));

    public GatheringExpeditionQuest() {
        super(QuestType.LONG, QuestDifficulty.NORMAL);

        Tracker tracker = new TriggerTracker<>(QuestTriggers.ENTER_ROOM, 9)
                .triggerCondition(GatheringExpeditionQuest::isNodeMarked)
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
            case Flowers: return localization.TEXT[3];
            case Minerals: return localization.TEXT[4];
            default: return localization.TEXT[5];
        }
    }

    private String getTrackerText() {
        ExpeditionFlavor flavor = getFlavor();
        if (flavor == null) {
            return localization.EXTRA_TEXT[0];
        }
        switch (flavor) {
            case Flowers: return localization.EXTRA_TEXT[1];
            case Minerals: return localization.EXTRA_TEXT[2];
            default: return localization.EXTRA_TEXT[3];
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

    @Override
    public void onStart() {
        super.onStart();
        markNodes();
    }

    @Override
    public void onComplete() {
        clearNodes();
    }

    @Override
    public void onFail() {
        clearNodes();
    }

    public static void clearNodes() {
        for (int i = 0; i < AbstractDungeon.map.size(); i++) {
            for (int j = 0; j < AbstractDungeon.map.get(i).size(); j++) {
                MapRoomNode node = AbstractDungeon.map.get(i).get(j);
                ShowMarkedNodesOnMapPatch.ImageField.image.set(node, null);
            }
        }
    }

    public static void markNodes() {
        Texture image = getTexture();
        Random rng = new Random(Settings.seed + AbstractDungeon.actNum * 5689L);

        List<MapRoomNode> possibleNodes = new ArrayList<>();
        for (int i = 0; i < AbstractDungeon.map.size(); i++) {
            for (int j = 0; j < AbstractDungeon.map.get(i).size(); j++) {
                MapRoomNode node = AbstractDungeon.map.get(i).get(j);
                if (node.hasEdges() && !(AbstractDungeon.actNum == 1 && node.y == 0)) {
                    boolean invalid = node.getRoom() instanceof ShopRoom || node.getRoom() instanceof TreasureRoom;
                    if (!invalid) {
                        possibleNodes.add(node);
                    }
                }
            }
        }

        Collections.shuffle(possibleNodes, new java.util.Random(rng.randomLong()));
        Supplier<Integer> f = () -> (rng.randomBoolean() ? 0 : 1);
        int n = 3 + (AbstractDungeon.actNum == 3 ? 1 : 0) + (AbstractDungeon.actNum == 2 ? f.get() : 0)  + f.get() + f.get() + f.get();
        n = AbstractDungeon.actNum > 3 ? 1 : n;
        n = Math.min(n, possibleNodes.size());
        for (int i = 0; i < n; i++) {
            MapRoomNode node = possibleNodes.get(i);
            ShowMarkedNodesOnMapPatch.ImageField.image.set(node, image);
        }
    }

    public static void markNodesIfQuestActive() {
        if (CardCrawlGame.isInARun() && QuestManager.quests().stream().anyMatch(q -> q instanceof GatheringExpeditionQuest)) {
            markNodes();
        }
    }

    public static boolean isNodeMarked(MapRoomNode node) {
        return ShowMarkedNodesOnMapPatch.ImageField.image.get(node) != null;
    }

    @SpirePatch2(clz = CardCrawlGame.class, method = "getDungeon", paramtypez = {String.class, AbstractPlayer.class})
    @SpirePatch2(clz = CardCrawlGame.class, method = "getDungeon", paramtypez = {String.class, AbstractPlayer.class, SaveFile.class})
    public static class MarkNodesOnGetDungeonPatch {
        @SpirePostfixPatch
        public static void markNodesOnGetDungeon(CardCrawlGame __instance) {
            if (!Loader.isModLoaded("actlikeit")) {
                markNodesIfQuestActive();
            }
        }
    }

    @SpirePatch2(cls = "actlikeit.patches.GetDungeonPatches$getDungeonThroughProgression", method = "Postfix", paramtypez = { AbstractDungeon.class, CardCrawlGame.class, String.class, AbstractPlayer.class }, requiredModId = "actlikeit")
    @SpirePatch2(cls = "actlikeit.patches.GetDungeonPatches$getDungeonThroughSavefile", method = "Postfix", paramtypez = { AbstractDungeon.class, CardCrawlGame.class, String.class, AbstractPlayer.class, SaveFile.class }, requiredModId = "actlikeit")
    public static class MarkNodesOnGetDungeonActLikeIt {
        @SpirePostfixPatch
        public static void markNodesOnGetDungeonActLikeIt() {
            markNodesIfQuestActive();
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
