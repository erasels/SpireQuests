package spireQuests.quests;

import basemod.AutoAdd;
import basemod.BaseMod;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import spireQuests.Anniv8Mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spireQuests.Anniv8Mod.makeID;
import static spireQuests.Anniv8Mod.modID;

@SpirePatch(
        clz = AbstractPlayer.class,
        method = SpirePatch.CLASS
)
public class QuestManager {
    public static final int QUEST_LIMIT = 5;

    private static final Map<String, AbstractQuest> quests = new HashMap<>();

    public static SpireField<List<AbstractQuest>> currentQuests = new SpireField<>(ArrayList::new);

    //Called once in postInitialize
    public static void initialize() {
        new AutoAdd(modID)
            .packageFilter(Anniv8Mod.class)
            .any(AbstractQuest.class, QuestManager::registerQuest);

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
                    quest.loadSave(questSave.questData[i]);
                    currentQuests.get(AbstractDungeon.player).add(quest);
                }
            }
        });
    }

    private static void registerQuest(AutoAdd.Info info, AbstractQuest quest) {
        AbstractQuest q = quests.put(quest.id, quest);
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
    }

    public static void completeQuest(AbstractQuest quest) {
        if (!quest.complete() && !quest.fail()) {
            Anniv8Mod.logger.warn("completeQuest called when quest is not complete/failed!");
            return;
        }

        if (quest.fail()) {
            quests().remove(quest);
            quest.onFail();
            return;
        }

        if (AbstractDungeon.currMapNode == null) return;
        if (AbstractDungeon.currMapNode.room == null) return;
        if (AbstractDungeon.currMapNode.room.phase == AbstractRoom.RoomPhase.COMBAT) return;

        quests().remove(quest);
        quest.obtainRewards();
    }

    public static void failQuest(AbstractQuest quest) {
        quest.forceFail();
        quest.onFail();
        completeQuest(quest);
    }

    public void update() {
        if (AbstractDungeon.player == null) return;

        //remove failed quests?
        for (AbstractQuest quest : quests()) {
            quest.update();
        }

    }


    public void render(SpriteBatch sb) {
        if (AbstractDungeon.player == null) return;
        //quest ui
    }
}
