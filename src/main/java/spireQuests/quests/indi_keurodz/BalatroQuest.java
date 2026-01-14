package spireQuests.quests.indi_keurodz;

import java.util.*;

import basemod.BaseMod;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.mod.stslib.icons.CustomIconHelper;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;

import basemod.helpers.TooltipInfo;
import spireQuests.Anniv8Mod;
import spireQuests.patches.QuestTriggers;
import spireQuests.patches.ShowMarkedNodesOnMapPatch;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.MarkNodeQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.QuestReward.QuestRewardSave;
import spireQuests.quests.indi_keurodz.BossBlinds.TheOx;
import spireQuests.quests.indi_keurodz.icons.*;
import spireQuests.quests.indi_keurodz.modifiers.EternalModifier;
import spireQuests.quests.indi_keurodz.modifiers.PerishableModifier;
import spireQuests.quests.indi_keurodz.modifiers.RentalModifier;
import spireQuests.quests.indi_keurodz.relics.GoldStakeRelic;
import spireQuests.util.TexLoader;

import static spireQuests.Anniv8Mod.makeID;

public class BalatroQuest extends AbstractQuest implements MarkNodeQuest {
    public static TextureAtlas BossBlindsAtlas;
    public static final String BLIND_STRINGS_ID = makeID("BalatroBlinds");
    public static final String AUTHOR = "indi_keurodz";
    public static final String id = makeID("BalatroQuest");

    private static final Map<String, String> blindStrings = CardCrawlGame.languagePack
            .getUIString(BLIND_STRINGS_ID).TEXT_DICT;


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
        Ox, // TODO
        Pillar,
        Plant,
        Psychic,
        Serpent,
        Tooth,
        Wall,
        Water,
        Wheel,
        Window;

        public final TooltipInfo tooltip;
        public final Array<AtlasRegion> frames;

        BossBlind() {

            this.frames = BossBlindsAtlas.findRegions(this.toString());

            this.tooltip = new TooltipInfo(blindStrings.get(this.toString()),
                    blindStrings.get(this.toString() + "_Description"));
        }

    }

    public static int BLIND_FIGHTS_COMPLETED = 0;
    public static MapRoomNode[] markednodes;

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

        new TriggeredUpdateTracker<>(QuestTriggers.VICTORY, 0, 8, BalatroQuest::getBlindBattlesCompleted).add(this);

        addReward(new BalatroReward());
        needHoverTip = true;

    }

    public static class BalatroReward extends QuestReward {
        static {
            addRewardSaver(new RewardLoader(BalatroReward.class, (save) -> new BalatroReward()));
        }

        private static final UIStrings EDITIONS = CardCrawlGame.languagePack
                .getUIString(makeID("BalatroEditions"));

        private static final Keyword ETERNAL = Anniv8Mod.keywords.get(EternalModifier.ID);
        private static final Keyword PERISHABLE = Anniv8Mod.keywords.get(PerishableModifier.ID);
        private static final Keyword RENTAL = Anniv8Mod.keywords.get(RentalModifier.ID);

        private static final String REWARD_DESC = CardCrawlGame.languagePack
                .getUIString(makeID("BalatroQuestReward")).TEXT[0];
        private static final TextureRegion REWARD_ICON = TexLoader
                .getTextureAsAtlasRegion(Anniv8Mod.modID + "Resources/images/indi_keurodz/Aura.png");

        public BalatroReward() {
            super(REWARD_DESC);
        }

        @Override
        public TextureRegion icon() {
            return REWARD_ICON;
        }

        @Override
        protected String saveParam() {
            return null;
        }

        @Override
        public void addTooltip(List<PowerTip> tips) {
            tips.add(new PowerTip(ETERNAL.PROPER_NAME, ETERNAL.DESCRIPTION));
            tips.add(new PowerTip(PERISHABLE.PROPER_NAME, PERISHABLE.DESCRIPTION));
            tips.add(new PowerTip(RENTAL.PROPER_NAME, RENTAL.DESCRIPTION));
            tips.add(new PowerTip(EDITIONS.TEXT[0], EDITIONS.TEXT[1]));
        }

        @Override
        public void obtainRewardItem() {
            AbstractDungeon.combatRewardScreen.rewards.add(0, new RewardItem(10));
            AbstractDungeon.combatRewardScreen.positionRewards();
        }

        @Override
        public void obtainInstant() {
        }

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
