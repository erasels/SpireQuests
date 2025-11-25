package spireQuests.quests.indi_keurodz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.BurningBlood;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;

import spireQuests.Anniv8Mod;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;
import spireQuests.quests.QuestReward;
import spireQuests.quests.indi_keurodz.patches.ShowBossBlindsOnMapPatch;
import spireQuests.quests.indi_keurodz.relics.GoldStakeRelic;

public class BalatroQuest extends AbstractQuest {
    private static TextureAtlas BossBlinds;
    private static String[] BlindNames = { "AmberAcorn", "Arm", "CeruleanBell", "Club", "CrimsonHeart", "Eye", "Fish",
            "Flint", "Goad", "Head", "Hook", "House", "Manacle", "Mark", "Mouth", "Needle", "Ox", "Pillar", "Plant",
            "Psychic", "Serpent", "Tooth", "VerdantLeaf", "VioletVessel", "Wall", "Water", "Wheel", "Window" };

    public static int BLIND_FIGHTS_COMPLETED = 0;

    public BalatroQuest() {
        super(QuestType.LONG, QuestDifficulty.CHALLENGE);

        BossBlinds = new TextureAtlas(
                Gdx.files.internal(Anniv8Mod.makeContributionPath("indi_keurodz", "BossBlinds.atlas")));

        new TriggeredUpdateTracker<>(QuestTriggers.VICTORY, 0, 10, () -> {
            return BLIND_FIGHTS_COMPLETED;
        }).add(this);

        addReward(new QuestReward.RelicReward(new BurningBlood()));
        needHoverTip = true;
    }

    @Override
    public void onStart() {
        super.onStart();
        (new GoldStakeRelic()).instantObtain();
        markNodes();
    }

    public static void markNodesIfQuestActive() {
        if (CardCrawlGame.isInARun() && QuestManager.quests().stream().anyMatch(q -> q instanceof BalatroQuest)) {
            markNodes();
        }
    }

    public static void markNodes() {
        Random rng = new Random(Settings.seed + AbstractDungeon.actNum * 1977L);

        List<MapRoomNode> possibleNodes = new ArrayList<>();
        for (int i = 0; i < AbstractDungeon.map.size(); i++) {
            for (int j = 0; j < AbstractDungeon.map.get(i).size(); j++) {
                MapRoomNode node = AbstractDungeon.map.get(i).get(j);
                if (node.hasEdges() && !(AbstractDungeon.actNum == 1 && node.y == 0)) {
                    boolean valid = node.getRoom() instanceof MonsterRoom || node.getRoom() instanceof MonsterRoomElite;
                    if (valid) {
                        possibleNodes.add(node);
                    }
                }
            }
        }

        Collections.shuffle(possibleNodes, new java.util.Random(rng.randomLong()));
        int n = possibleNodes.size() / 2;
        for (int i = 0; i < n; i++) {
            MapRoomNode node = possibleNodes.get(i);
            Array<AtlasRegion> frames = BossBlinds.findRegions(BlindNames[rng.random(21)]);
            ShowBossBlindsOnMapPatch.BossBlindField.frames.set(node, frames);
        }
    }

    @SpirePatch2(clz = CardCrawlGame.class, method = "getDungeon", paramtypez = { String.class, AbstractPlayer.class })
    @SpirePatch2(clz = CardCrawlGame.class, method = "getDungeon", paramtypez = { String.class, AbstractPlayer.class,
            SaveFile.class })
    public static class MarkNodesOnGetDungeonPatch {
        @SpirePostfixPatch
        public static void markNodesOnGetDungeon(CardCrawlGame __instance) {
            if (!Loader.isModLoaded("actlikeit")) {
                markNodesIfQuestActive();
            }
        }
    }

    @SpirePatch2(cls = "actlikeit.patches.GetDungeonPatches$getDungeonThroughProgression", method = "Postfix", paramtypez = {
            AbstractDungeon.class, CardCrawlGame.class, String.class,
            AbstractPlayer.class }, requiredModId = "actlikeit")
    @SpirePatch2(cls = "actlikeit.patches.GetDungeonPatches$getDungeonThroughSavefile", method = "Postfix", paramtypez = {
            AbstractDungeon.class, CardCrawlGame.class, String.class, AbstractPlayer.class,
            SaveFile.class }, requiredModId = "actlikeit")
    public static class MarkNodesOnGetDungeonActLikeIt {
        @SpirePostfixPatch
        public static void markNodesOnGetDungeonActLikeIt() {
            markNodesIfQuestActive();
        }
    }

}
