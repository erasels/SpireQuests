package spireQuests.quests;

import basemod.AutoAdd;
import basemod.BaseMod;
import basemod.abstracts.CustomSavable;
import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;
import spireQuests.Anniv8Mod;
import spireQuests.cardmods.QuestboundMod;
import spireQuests.patches.QuestRunHistoryPatch;
import spireQuests.questStats.QuestStatManager;
import spireQuests.vfx.ShowCardandFakeObtainEffect;

import java.util.*;

import static spireQuests.Anniv8Mod.makeID;
import static spireQuests.Anniv8Mod.modID;

@SpirePatch(
        clz = AbstractPlayer.class,
        method = SpirePatch.CLASS
)
public class QuestManager {
    private static final String[] TEXT = CardCrawlGame.languagePack.getUIString(makeID("QuestManager")).TEXT;

    public static final int QUEST_LIMIT = 5;

    private static final Map<String, AbstractQuest> quests = new HashMap<>();
    private static final EnumMap<AbstractQuest.QuestDifficulty, ArrayList<AbstractQuest>> questsByDifficulty = new EnumMap<>(AbstractQuest.QuestDifficulty.class);

    public static SpireField<List<AbstractQuest>> currentQuests = new SpireField<>(ArrayList::new);

    //Called once in postInitialize
    public static void initialize() {
        for (AbstractQuest.QuestDifficulty diff : AbstractQuest.QuestDifficulty.values()) {
            questsByDifficulty.put(diff, new ArrayList<>());
        }

        new AutoAdd(modID)
                .packageFilter(Anniv8Mod.class)
                .any(AbstractQuest.class, QuestManager::registerQuest);
        Statistics.logStatistics(QuestManager.getAllQuests());

        BaseMod.addSaveField(makeID("QuestManager"), new CustomSavable<QuestSave>() {
            @Override
            public QuestSave onSave() {
                return new QuestSave(quests());
            }

            @Override
            public void onLoad(QuestSave questSave) {
                if (questSave == null) return;
                for (int i = 0; i < questSave.questIds.length; ++i) {
                    AbstractQuest quest = getQuest(questSave.questIds[i]);
                    if (quest == null) continue;
                    quest.refreshState();
                    quest.loadSave(questSave.questData[i], questSave.questRewards[i]);
                    currentQuests.get(AbstractDungeon.player).add(quest);
                }
            }
        });
    }

    private static void registerQuest(AutoAdd.Info info, AbstractQuest quest) {
        AbstractQuest q = quests.put(quest.id, quest);
        ArrayList<AbstractQuest> questOfDifficulty = questsByDifficulty.get(quest.difficulty);
        questOfDifficulty.add(quest);
        questsByDifficulty.put(quest.difficulty, questOfDifficulty);

        if (q != null) {
            throw new RuntimeException("Duplicate quest ID " + q.id + " for classes " + q.getClass().getName() + " and " + quest.getClass().getName());
        }
    }

    public static AbstractQuest getQuest(String id) {
        AbstractQuest quest = quests.get(id);
        if (quest == null) {
            Anniv8Mod.logger.error("Quest not found: " + id);
            return null;
        }
        return quest.makeCopy();
    }

    public static Collection<AbstractQuest> getAllQuests() {
        return new ArrayList<>(quests.values());
    }

    public static ArrayList<AbstractQuest> getQuestsByDifficulty(AbstractQuest.QuestDifficulty difficulty) {
        return questsByDifficulty.get(difficulty);
    }

    public static List<AbstractQuest> quests() {
        return currentQuests.get(AbstractDungeon.player);
    }


    public static <T> void triggerTrackers(Trigger<T> trigger) {
        if (AbstractDungeon.player == null) return;

        for (AbstractQuest quest : quests()) {
            quest.triggerTrackers(trigger);
        }
    }

    public static void startQuest(String id) {
        startQuest(getQuest(id));
    }

    public static void startQuest(AbstractQuest quest) {
        List<AbstractQuest> questList = quests();
        if (questList.size() >= QUEST_LIMIT) {
            AbstractQuest toRemove = questList.get(0);
            Anniv8Mod.logger.info("Removing quest {} due to quest limit ({})!", toRemove.id, QUEST_LIMIT);
            failQuest(toRemove);
        }

        questList.add(quest);
        questList.sort(null);
        quest.onStart();
        if (quest.questboundCards != null) {
            quest.questboundCards.forEach(c -> {
                CardModifierManager.addModifier(c, new QuestboundMod(quest));
                AbstractDungeon.effectList.add(new ShowCardandFakeObtainEffect(c.makeCopy(), (float) (Settings.WIDTH / 2), (float) (Settings.HEIGHT / 2)));
            });
        }
        QuestStatManager.markTaken(quest.id);
        List<List<String>> questPickupPerFloor = QuestRunHistoryPatch.questPickupPerFloorLog.get(AbstractDungeon.player);
        if (!questPickupPerFloor.isEmpty()) {
            questPickupPerFloor.get(questPickupPerFloor.size() - 1).add(quest.id);
        } else {
            Anniv8Mod.logger.error("questPickupPerFloor was empty, not adding quest to run history.");
        }
    }

    public static void completeQuest(AbstractQuest quest) {
        if (!quest.complete() && !quest.fail()) {
            Anniv8Mod.logger.warn("completeQuest called when quest is not complete/failed!");
            return;
        }

        if (quest.fail()) {
            quests().remove(quest);
            quest.onFail();

            QuestStatManager.markFailed(quest.id);
            List<List<String>> questFailurePerFloor = QuestRunHistoryPatch.questFailurePerFloorLog.get(AbstractDungeon.player);
            questFailurePerFloor.get(questFailurePerFloor.size() - 1).add(quest.id);
            return;
        }

        int complainCode = -1;
        if (AbstractDungeon.currMapNode == null) complainCode = 0;
        else if (AbstractDungeon.currMapNode.room == null) complainCode = 0;
        else if (AbstractDungeon.currMapNode.room.phase == AbstractRoom.RoomPhase.COMBAT) complainCode = 1;
        if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.COMBAT_REWARD && quest.rewardScreenOnly) complainCode = 2;
        if(complainCode > -1) {
            AbstractDungeon.effectList.add(new ThoughtBubble(AbstractDungeon.player.dialogX, AbstractDungeon.player.dialogY, 3.0F, TEXT[complainCode], true));
            return;
        }

        quests().remove(quest);
        quest.obtainRewards();
        QuestStatManager.markComplete(quest.id);
        List<List<String>> questCompletionPerFloor = QuestRunHistoryPatch.questCompletionPerFloorLog.get(AbstractDungeon.player);
        questCompletionPerFloor.get(questCompletionPerFloor.size() - 1).add(quest.id);
    }

    public static void failQuest(AbstractQuest quest) {
        quest.forceFail();
        quest.onFail();
        completeQuest(quest);

        QuestStatManager.markFailed(quest.id);
        List<List<String>> questFailurePerFloor = QuestRunHistoryPatch.questFailurePerFloorLog.get(AbstractDungeon.player);
        questFailurePerFloor.get(questFailurePerFloor.size() - 1).add(quest.id);
    }

    public void update() {
        if (AbstractDungeon.player == null) return;

        //remove failed quests?
        for (AbstractQuest quest : quests()) {
            quest.update();
        }

    }


    public void render(SpriteBatch sb) {
        if (AbstractDungeon.player == null) {
        }
        //quest ui
    }

    public static void failAllActiveQuests() {
        for (AbstractQuest q : quests()) {
            q.forceFail();
        }
    }
}
