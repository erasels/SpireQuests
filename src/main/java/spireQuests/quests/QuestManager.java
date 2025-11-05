package spireQuests.quests;

import basemod.AutoAdd;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.Anniv8Mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spireQuests.Anniv8Mod.modID;

@SpirePatch(
        clz = AbstractPlayer.class,
        method = SpirePatch.CLASS
)
public class QuestManager {
    private static final Map<String, AbstractQuest> quests = new HashMap<>();

    public static SpireField<List<AbstractQuest>> currentQuests = new SpireField<>(() -> {
        ArrayList<AbstractQuest> quests = new ArrayList<>();
        quests.add(new TestQuest());
        return quests;
    });

    public static void initialize() {
        new AutoAdd(modID)
            .packageFilter(Anniv8Mod.class)
            .any(AbstractQuest.class, QuestManager::registerQuest);
    }

    private static void registerQuest(AutoAdd.Info info, AbstractQuest quest) {
        AbstractQuest q = quests.put(quest.id, quest);
        if (q != null) {
            throw new RuntimeException("Duplicate quest ID " + q.id + " for classes " + q.getClass().getName() + " and " + quest.getClass().getName());
        }
    }

    public static AbstractQuest getQuest(String id) {
        return quests.get(id).makeCopy();
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
        quests().add(quest);
        quests().sort(null);
        quest.onStart();
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
