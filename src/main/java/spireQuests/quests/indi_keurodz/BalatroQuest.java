package spireQuests.quests.indi_keurodz;

import java.util.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.mod.stslib.icons.CustomIconHelper;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;

import basemod.helpers.TooltipInfo;
import spireQuests.Anniv8Mod;
import spireQuests.patches.QuestTriggers;
import spireQuests.patches.ShowMarkedNodesOnMapPatch;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.MarkNodeQuest;
import spireQuests.quests.QuestManager;
import spireQuests.quests.indi_keurodz.BossBlinds.TheOx;
import spireQuests.quests.indi_keurodz.QuestIcons.*;
import spireQuests.quests.indi_keurodz.modifiers.EternalModifier;
import spireQuests.quests.indi_keurodz.modifiers.PerishableModifier;
import spireQuests.quests.indi_keurodz.modifiers.RentalModifier;
import spireQuests.quests.indi_keurodz.relics.GoldStakeRelic;
import spireQuests.quests.indi_keurodz.rewards.BalatroQuestCombatReward;

import static spireQuests.Anniv8Mod.makeID;

public class BalatroQuest extends AbstractQuest implements MarkNodeQuest {
    public static TextureAtlas BossBlindsAtlas;
    public static final String BLIND_STRINGS_ID = makeID("BalatroBlinds");
    public static final String AUTHOR = "indi_keurodz";
    public static final String ID = makeID("BalatroQuest");

    private static final Map<String, String> blindStrings = CardCrawlGame.languagePack
            .getUIString(BLIND_STRINGS_ID).TEXT_DICT;

    private static final UIStrings EDITIONS = CardCrawlGame.languagePack
            .getUIString(makeID("BalatroEditions"));

    private static final Keyword ETERNAL = Anniv8Mod.keywords.get(EternalModifier.ID);
    private static final Keyword PERISHABLE = Anniv8Mod.keywords.get(PerishableModifier.ID);
    private static final Keyword RENTAL = Anniv8Mod.keywords.get(RentalModifier.ID);

    private Tracker tracker;

    public static void addSaveFields() {
        TheOx.addSaveFields();
    }

    public static enum BossBlind {
        Arm,
        Club,
        Eye,
        Fish,
        Flint,
        Goad,
        Head,
        Hook,
        House,
        Manacle,
        Mark,
        Mouth,
        Needle,
        Ox,
        Pillar,
        Plant,
        Psychic,
        Serpent,
        Tooth,
        Wall,
        Water,
        Wheel,
        Window;

        public TooltipInfo tooltip;
        public final Array<AtlasRegion> frames;

        BossBlind() {

            this.frames = BossBlindsAtlas.findRegions(this.toString());

            this.tooltip = new TooltipInfo(blindStrings.get(this.toString()),
                    blindStrings.get(this.toString() + "_Description"));
        }
    }

    public static int BLIND_FIGHTS_COMPLETED = 0;

    public BalatroQuest() {
        super(QuestType.LONG, QuestDifficulty.CHALLENGE);

        BossBlindsAtlas = new TextureAtlas(
                Gdx.files.internal(Anniv8Mod.makeContributionPath(AUTHOR, "BossBlinds.atlas")));

        CustomIconHelper.addCustomIcon(ArmIcon.get());
        CustomIconHelper.addCustomIcon(ClubIcon.get());
        CustomIconHelper.addCustomIcon(EternalIcon.get());
        CustomIconHelper.addCustomIcon(EyeIcon.get());
        CustomIconHelper.addCustomIcon(FishIcon.get());
        CustomIconHelper.addCustomIcon(FlintIcon.get());
        CustomIconHelper.addCustomIcon(GoadIcon.get());
        CustomIconHelper.addCustomIcon(HeadIcon.get());
        CustomIconHelper.addCustomIcon(HookIcon.get());
        CustomIconHelper.addCustomIcon(HouseIcon.get());
        CustomIconHelper.addCustomIcon(ManacleIcon.get());
        CustomIconHelper.addCustomIcon(MarkIcon.get());
        CustomIconHelper.addCustomIcon(MouthIcon.get());
        CustomIconHelper.addCustomIcon(NeedleIcon.get());
        CustomIconHelper.addCustomIcon(OxIcon.get());
        CustomIconHelper.addCustomIcon(PerishableIcon.get());
        CustomIconHelper.addCustomIcon(PillarIcon.get());
        CustomIconHelper.addCustomIcon(PlantIcon.get());
        CustomIconHelper.addCustomIcon(PsychicIcon.get());
        CustomIconHelper.addCustomIcon(RentalIcon.get());
        CustomIconHelper.addCustomIcon(SerpentIcon.get());
        CustomIconHelper.addCustomIcon(ToothIcon.get());
        CustomIconHelper.addCustomIcon(WallIcon.get());
        CustomIconHelper.addCustomIcon(WaterIcon.get());
        CustomIconHelper.addCustomIcon(WheelIcon.get());
        CustomIconHelper.addCustomIcon(WindowIcon.get());

        tracker = new TriggeredUpdateTracker<>(QuestTriggers.VICTORY, 0, 8,
                BalatroQuest::getBlindBattlesCompleted).add(this);

        this.isAutoComplete = true;
        this.needHoverTip = true;
        this.useDefaultReward = false;

    }

    @SpirePatch2(clz = CombatRewardScreen.class, method = "setupItemReward")
    public static class RewardReplacementPatch {
        @SpirePostfixPatch
        public static void patch(CombatRewardScreen __instance) {
            BalatroQuest q = (BalatroQuest) QuestManager.quests().stream()
                    .filter(quest -> ID.equals(quest.id) && quest.isCompleted())
                    .findAny()
                    .orElse(null);
            if (q != null) {
                __instance.rewards.add(0, new BalatroQuestCombatReward());
                AbstractRelic goldStakeRelic = AbstractDungeon.player.getRelic(GoldStakeRelic.ID);
                if (goldStakeRelic != null) {
                    ((GoldStakeRelic) goldStakeRelic).upgradeDescription();
                }
            }
        }
    }

    @Override
    public void makeTooltips(List<PowerTip> tipList) {
        tipList.clear();

        tipList.add(new PowerTip(ETERNAL.PROPER_NAME, ETERNAL.DESCRIPTION));
        tipList.add(new PowerTip(PERISHABLE.PROPER_NAME, PERISHABLE.DESCRIPTION));
        tipList.add(new PowerTip(RENTAL.PROPER_NAME, RENTAL.DESCRIPTION));
        tipList.add(new PowerTip(EDITIONS.TEXT[0], EDITIONS.TEXT[1]));
    }

    @Override
    public void onStart() {
        super.onStart();
        (new GoldStakeRelic()).instantObtain();
        BLIND_FIGHTS_COMPLETED = 0;
        tracker.refreshState();
    }

    public static int getBlindBattlesCompleted() {
        MapRoomNode node = AbstractDungeon.currMapNode;
        if (node != null && ShowMarkedNodesOnMapPatch.ImageField.CheckMarks(node, ID)) {
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
        if (AbstractDungeon.actNum == 1) {
            blindsList.remove(BossBlind.Ox);
        }
        Collections.shuffle(blindsList, new java.util.Random(rng.randomLong()));
        blindsList = blindsList.subList(0, Math.max(n, blindsList.size() - 1));

        for (int i = 0; i < n; i++) {
            try {
                MapRoomNode node = possibleNodes.get(i);
                BossBlind nextBlind = blindsList.remove(0);
                ShowMarkedNodesOnMapPatch.ImageField.MarkNode(node, ID, nextBlind.frames, 12, nextBlind.tooltip);
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
    }

}
