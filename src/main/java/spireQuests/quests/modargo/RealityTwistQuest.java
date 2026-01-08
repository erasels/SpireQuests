package spireQuests.quests.modargo;

import basemod.BaseMod;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.monsters.beyond.Exploder;
import com.megacrit.cardcrawl.monsters.beyond.Repulsor;
import com.megacrit.cardcrawl.monsters.beyond.SnakeDagger;
import com.megacrit.cardcrawl.monsters.beyond.Spiker;
import com.megacrit.cardcrawl.monsters.city.*;
import com.megacrit.cardcrawl.monsters.exordium.*;
import spireQuests.Anniv8Mod;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;
import spireQuests.quests.QuestReward;
import spireQuests.quests.modargo.monsters.realitytwist.*;
import spireQuests.util.ActUtil;

import java.util.*;

import static spireQuests.Anniv8Mod.makeID;

public class RealityTwistQuest extends AbstractQuest {
    private static final String ID = makeID(RealityTwistQuest.class.getSimpleName());
    public static final String[] fightNames = CardCrawlGame.languagePack.getUIString(makeID("TwistedFightNames")).TEXT;
    public static final String[] enemyNames = CardCrawlGame.languagePack.getUIString(makeID("TwistedEnemyNames")).TEXT;
    private static final HashMap<Integer, List<String>> fightsByAct = new HashMap<>();
    private boolean justFought = false;

    public RealityTwistQuest() {
        super(QuestType.SHORT, QuestDifficulty.HARD);

        new TriggerTracker<>(QuestTriggers.COMBAT_END, 4)
                .triggerCondition((x) -> isRealityTwistCombat(AbstractDungeon.lastCombatMetricKey))
                .add(this);
        new TriggerEvent<>(QuestTriggers.LEAVE_ROOM, (room) -> {
            // We only remove the fight from the planned list on leaving the room, so that saving and reloading after
            // combat produces a stable outcome (following the same general approach as the base game).
            if (this.justFought) {
                this.justFought = false;
                PlannedFightsByActField.plannedFightsByAct.get(AbstractDungeon.player).get(ActUtil.getRealActNum()).remove(0);
            }
        }).add(this);

        addReward(new QuestReward.RandomRelicReward());
        isAutoComplete = true;
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.floorNum > 0 && (AbstractDungeon.floorNum < 40 || Settings.isEndless);
    }

    @Override
    public void onStart() {
        super.onStart();
        this.initializePlannedFights();
    }

    @Override
    public void onComplete() {
        super.onComplete();
        PlannedFightsByActField.plannedFightsByAct.set(AbstractDungeon.player, null);
    }

    public void initializePlannedFights() {
        // Following the base game, we plan out the fight order ahead of time, which helps with save/load stability
        HashMap<Integer, List<String>> map = new HashMap<>();
        for (int actNum : fightsByAct.keySet()) {
            List<String> plannedFights = new ArrayList<>(fightsByAct.get(actNum));
            Collections.shuffle(plannedFights, new Random(Settings.seed + AbstractDungeon.floorNum + actNum * 3637L));
            map.put(actNum, plannedFights);
        }
        PlannedFightsByActField.plannedFightsByAct.set(AbstractDungeon.player, map);
    }

    private static boolean isRealityTwistCombat(String id) {
        return fightsByAct.values().stream().flatMap(Collection::stream).anyMatch(x -> x.equals(id));
    }

    public static void addMonsters() {
        addMonster(1, makeID("CITY_THUGS"), fightNames[0], () -> new MonsterGroup(new AbstractMonster[] { new LouseNormal(randomXOffset(-160.0F), randomYOffset(20.0F)), new Mugger(randomXOffset(130.0F), randomYOffset(20.0F)) }));
        addMonster(1, makeID("BEYOND_CULTIST"), fightNames[1], () -> new MonsterGroup(new AbstractMonster[] { new Cultist(-230.0F, -5.0F, false), new Repulsor(100.0F, 15.0F) }));
        addMonster(1, makeID("BYRD_AND_WORM"), fightNames[2], () -> new MonsterGroup(new AbstractMonster[] { new Byrd(-260.0F, MathUtils.random(25.0F, 70.0F)),  new JawWorm(75.0F, 5.0F) }));
        addMonster(1, makeID("BURNING_ORB_WALKER"), fightNames[3], () -> new BurningOrbWalker(-30.0F, 20.0F));
        addMonster(1, makeID("BOOK_OF_POKING"), fightNames[4], BookOfPoking::new);
        addMonster(1, makeID("NEARLY_UNSHELLED_PARASITE"), fightNames[5], NearlyUnshelledParasite::new);
        addMonster(1, makeID("POISONED_SLAVERS"), fightNames[6], () -> new MonsterGroup(new AbstractMonster[] { new PoisonedSlaverBlue(-270.0F, 15.0F), new PoisonedSlaverRed(130.0F, 0.0F) }));
        addMonster(2, makeID("JAW_NOB"), fightNames[7], () -> new MonsterGroup(new AbstractMonster[] { new JawWorm(-300.0F, 15.0F),  new GremlinNob(150.0F, 0.0F) }));
        addMonster(2, makeID("EXPLODER_AND_SHELLED_PARASITE"), fightNames[8], () -> new MonsterGroup(new AbstractMonster[] { new Exploder(-280.0F, 15.0F), new ShelledParasite(80.0F, 0.0F) }));
        addMonster(2, makeID("EXTRA_FUN_SNECKO"), fightNames[9], () -> new MonsterGroup(new AbstractMonster[] { new Snecko(-230.0F, 0.0F), new FungiBeast(110.0F, 0.0F) }));
        addMonster(2, makeID("MISMATCHED_HORDE"), fightNames[10], () -> new MonsterGroup(new AbstractMonster[] { new Spiker(-440.0F, 6.0F), new GremlinTsundere(-240.0F, 0.0F), new Taskmaster(0.0F, -10.0F), new SpikeSlime_M(240.0F, 0.0F) }));
        addMonster(2, makeID("TINY_TROUPE"), fightNames[11], () -> new MonsterGroup(new AbstractMonster[] { new GremlinFat(-400.0F, 0.0F), new GremlinWarrior(-250.0F, 0.0F), new TinyHead() }));
        addMonster(2, makeID("BARRICADE_BUDDIES"), fightNames[12], () -> new MonsterGroup(new AbstractMonster[] { new ArmoredGremlinWizard(-230.0F, 0.0F), new SphericGuardian(50.0F, 0.0F) }));
        addMonster(2, makeID("MALFUNCTIONING_SHAPES"), fightNames[13], () -> new MonsterGroup(new AbstractMonster[] { new DudExploder(-480.0F, 5.0F), new SoftSpiker(-240.0F, -6.0F), new CrackedRepulsor(0.0F, -12.0F) }));
        addMonster(3, makeID("DOUBLE_TROUBLE"), fightNames[14], () -> new MonsterGroup(new AbstractMonster[] { new ShelledParasite(-310.0F, 0.0F),  new SnakePlant(130.0F, 0.0F) }));
        addMonster(3, makeID("SNECKO_CULTISTS"), fightNames[15], () -> new MonsterGroup(new AbstractMonster[] { new Snecko(-475.0F, 0.0F),  new SneckoCultist(-130.0F, 0.0F), new SneckoCultist(200.0F, 0.0F) }));
        addMonster(3, makeID("DAGGAVULIN"), fightNames[16], () -> new MonsterGroup(new AbstractMonster[] { new SnakeDagger(-270.0F, 335.0F),  new SnakeDagger(-240.0F, 115.0F), new Lagavulin(false) }));
        addMonster(3, makeID("CHOSEN_OF_THE_SLIMES"), fightNames[17], () -> new MonsterGroup(new AbstractMonster[] { new AcidSlime_M(-475.0F, 0.0F),  new SpikeSlime_L(-170.0F, 0.0F), new Chosen(200.0F, 0.0F) }));
        addMonster(3, makeID("SUPER_TRIO"), fightNames[18], () -> new MonsterGroup(new AbstractMonster[] { new SuperSentry(-430.0F, 25.0F), new ToughMugger(-135.0F, 10.0F), new OvergrownFungiBeast(150.0F, 10.0F) }));
        addMonster(3, makeID("MAW_AND_JAW_AND_MYSTIC"), fightNames[19], () -> new MonsterGroup(new AbstractMonster[] { new MiniMaw(-400.0F, 20.0F), new JawWorm(-115.0F, 10.0F, true), new Healer(160.0F, 0.0F) }));
        addMonster(3, makeID("BIG_BYRDS"), fightNames[20], () -> new MonsterGroup(new AbstractMonster[]{new BigByrd(-440.0F, MathUtils.random(25.0F, 70.0F)), new BigByrd(-120.0F, MathUtils.random(25.0F, 70.0F)), new BigByrd(200.0F, MathUtils.random(25.0F, 70.0F))}));
    }

    private static void addMonster(int actNum, String id, String encounterName, BaseMod.GetMonster monster) {
        addMonster(actNum, id, encounterName, () -> new MonsterGroup(monster.get()));
    }

    private static void addMonster(int actNum, String id, String encounterName, BaseMod.GetMonsterGroup monster) {
        if (!fightsByAct.containsKey(actNum)) {
            fightsByAct.put(actNum, new ArrayList<>());
        }
        fightsByAct.get(actNum).add(id);
        BaseMod.addMonster(id, encounterName, monster);
    }

    private static float randomYOffset(float y) {
        return y + MathUtils.random(-20.0F, 20.0F);
    }

    private static float randomXOffset(float x) {
        return x + MathUtils.random(-20.0F, 20.0F);
    }

    @SpirePatch(clz = AbstractPlayer.class, method = SpirePatch.CLASS)
    public static class PlannedFightsByActField {
        public static SpireField<HashMap<Integer, List<String>>> plannedFightsByAct = new SpireField<>(() -> null);
    }

    public static void addSaveFields() {
        // This is set up as its own save field (instead of having the quest implement CustomSavable) in order to ensure
        // it loads before ReplaceNormalCombatsPatch below runs. That patch executes when the current act is initialized,
        // and the current room and the path taken to get to it are rebuilt, which is early in the loading process.
        BaseMod.addSaveField(makeID(ID + "PlannedFightsByAct"), new CustomSavable<HashMap<Integer, List<String>>>() {
            @Override
            public HashMap<Integer, List<String>> onSave() {
                return PlannedFightsByActField.plannedFightsByAct.get(AbstractDungeon.player);
            }

            @Override
            public void onLoad(HashMap<Integer, List<String>> plannedFightsByAct) {
                PlannedFightsByActField.plannedFightsByAct.set(AbstractDungeon.player, plannedFightsByAct);
            }
        });
    }

    @SpirePatch2(clz = AbstractDungeon.class, method = "getMonsterForRoomCreation")
    public static class ReplaceNormalCombatsPatch {
        @SpirePrefixPatch
        public static SpireReturn<MonsterGroup> replacementPatch() {
            RealityTwistQuest q = (RealityTwistQuest) QuestManager.quests().stream()
                    .filter(quest -> ID.equals(quest.id) && !quest.isCompleted() && !quest.isFailed())
                    .findAny()
                    .orElse(null);
            HashMap<Integer, List<String>> plannedFightsByAct = PlannedFightsByActField.plannedFightsByAct.get(AbstractDungeon.player);
            if (q != null && plannedFightsByAct != null) {
                int actNum = ActUtil.getRealActNum();
                if (fightsByAct.containsKey(actNum)) {
                    Anniv8Mod.logger.info("Replacing fight with Reality Twist fight");
                    q.justFought = true;
                    if (plannedFightsByAct.get(actNum).isEmpty()) {
                        q.initializePlannedFights();
                    }
                    AbstractDungeon.lastCombatMetricKey = plannedFightsByAct.get(actNum).get(0);
                    return SpireReturn.Return(MonsterHelper.getEncounter(AbstractDungeon.lastCombatMetricKey));
                }
            }
            return SpireReturn.Continue();
        }
    }
}
