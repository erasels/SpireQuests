package spireQuests.quests.indi_keurodz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.BurningBlood;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;

import spireQuests.Anniv8Mod;
import spireQuests.patches.QuestTriggers;
import spireQuests.patches.ShowMarkedNodesOnMapPatch;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.MarkNodeQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.indi_keurodz.relics.GoldStakeRelic;

import static spireQuests.Anniv8Mod.makeID;

public class BalatroQuest extends AbstractQuest implements MarkNodeQuest {
    private static TextureAtlas BossBlindsAtlas;
    public static final String BLIND_STRINGS_ID = makeID("BalatroBlinds");
    public static final String AUTHOR = "indi_keurodz";
    public static final String id = makeID("BalatroQuest");

    private static final Map<String, String> blindStrings = CardCrawlGame.languagePack
            .getUIString(BLIND_STRINGS_ID).TEXT_DICT;

    public static enum BossBlind {
        Arm, // done
        Club,
        Eye, // done
        Fish, // done
        Flint,
        Goad,
        Head,
        Hook, // done
        House, // done
        Manacle, // done
        Mark, // done
        Mouth,
        Needle, // done
        Ox,
        Pillar,
        Plant,
        Psychic, // done
        Serpent,
        Tooth, // done
        Wall, // done
        Water,
        Wheel, // done
        Window;

        public final PowerTip tooltip;
        public final Array<AtlasRegion> frames;

        BossBlind() {
        
            this.frames = BossBlindsAtlas.findRegions(this.toString());

            this.tooltip = new PowerTip(blindStrings.get(this.toString()),
                    blindStrings.get(this.toString() + "_Description"));
        }

    }

    public static int BLIND_FIGHTS_COMPLETED = 0;
    public static MapRoomNode[] markednodes;

    public BalatroQuest() {
        super(QuestType.LONG, QuestDifficulty.CHALLENGE);

        BossBlindsAtlas = new TextureAtlas(
                Gdx.files.internal(Anniv8Mod.makeContributionPath(AUTHOR, "BossBlinds.atlas")));

        new TriggeredUpdateTracker<>(QuestTriggers.VICTORY, 0, 8, BalatroQuest::getBlindBattlesCompleted).add(this);

        addReward(new QuestReward.RelicReward(new BurningBlood()));
        needHoverTip = true;
    }

    @Override
    public void onStart() {
        super.onStart();
        (new GoldStakeRelic()).instantObtain();
        BLIND_FIGHTS_COMPLETED = 0;
    }

    public static int getBlindBattlesCompleted() {
        MapRoomNode node = AbstractDungeon.currMapNode;
        if (node != null && ShowMarkedNodesOnMapPatch.ImageField.CheckMarks(node, id)) {
            BLIND_FIGHTS_COMPLETED++;
        }

        return BLIND_FIGHTS_COMPLETED;
    }

    @Override
    public void markNodes(ArrayList<ArrayList<MapRoomNode>> map, Random rng) {

        List<MapRoomNode> possibleNodes = new ArrayList<>();

        map.stream().flatMap(Collection::stream).forEach(node -> {
            if (node.hasEdges() && !(AbstractDungeon.actNum == 1 && node.y == 0)) {
                boolean valid = node.getRoom() instanceof MonsterRoom || node.getRoom() instanceof MonsterRoomElite;
                if (valid) {
                    possibleNodes.add(node);
                }
            }
        });

        Collections.shuffle(possibleNodes, new java.util.Random(rng.randomLong()));
        int n = possibleNodes.size() / 2;

        List<BossBlind> blindsList = new ArrayList<>(Arrays.asList(BossBlind.values()));
        Collections.shuffle(blindsList, new java.util.Random(rng.randomLong()));
        blindsList = blindsList.subList(0, Math.max(n, blindsList.size() - 1));

        for (int i = 0; i < n; i++) {
            try {
                MapRoomNode node = possibleNodes.get(i);
                BossBlind nextBlind = blindsList.remove(0);
                ShowMarkedNodesOnMapPatch.ImageField.MarkNode(node, id, nextBlind.frames, 12, nextBlind.tooltip);
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
    }

}
