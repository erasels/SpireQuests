package spireQuests.quests;

import basemod.BaseMod;
import basemod.abstracts.CustomSavable;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.random.Random;
import spireQuests.util.Wiz;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static spireQuests.Anniv8Mod.makeID;
import static spireQuests.quests.QuestManager.getQuestsByDifficulty;

@SpirePatch(clz = AbstractPlayer.class, method = SpirePatch.CLASS)
public class QuestGenerator {
    public static SpireField<Set<String>> seenQuests = new SpireField<>(HashSet::new);

    //Called once in postInitialize
    public static void initialize() {
        BaseMod.addSaveField(makeID("SeenQuests"), new CustomSavable<List<String>>() {
            @Override
            public List<String> onSave() {
                return new ArrayList<>(seenQuests.get(AbstractDungeon.player));
            }

            @Override
            public void onLoad(List<String> s) {
                if (s == null) return;
                seenQuests.get(AbstractDungeon.player).addAll(s);
            }
        });
    }

    public static ArrayList<AbstractQuest> generateRandomQuests(boolean fromNeow) {
        ArrayList<AbstractQuest> generatedQuests = new ArrayList<>();
        Random rng = new Random(Settings.seed + (9419L * (AbstractDungeon.floorNum + 1)));

        for (AbstractQuest.QuestDifficulty difficulty : rollDifficulties(fromNeow, rng)) {
            AbstractQuest quest = rollQuestForDifficulty(difficulty, fromNeow ? new HashSet<>() : seenQuests.get(AbstractDungeon.player), rng);
            if (quest != null) {
                quest.setCost();
                generatedQuests.add(quest);
                seenQuests.get(AbstractDungeon.player).add(quest.id);
            }
        }

        // For quest generation in the Neow room, we have to handle the fact that the player can save and reload after
        // picking some quests (because the game saves after picking a Neow bonus). Without special handling, this would
        // give different quests (because the previously seen quests would be loaded from the save). To prevent that, we
        // always use an empty set of seen quests for quest generation in the Neow room (above) and then filter out any
        // quests the player already has. We also adjust the number of pickable quests in QuestBoardProp.
        if (fromNeow) {
            Set<String> currentQuests = QuestManager.quests().stream().map(q -> q.id).collect(Collectors.toSet());
            generatedQuests.removeIf(q -> currentQuests.contains(q.id));
        }

        return generatedQuests;
    }

    private static AbstractQuest.QuestDifficulty[] rollDifficulties(boolean fromNeow, Random rng) {
        AbstractQuest.QuestDifficulty[] difficulties = new AbstractQuest.QuestDifficulty[]{
                AbstractQuest.QuestDifficulty.EASY,
                AbstractQuest.QuestDifficulty.NORMAL,
                AbstractQuest.QuestDifficulty.HARD
        };

        if (fromNeow) {
            boolean challenge = rng.randomBoolean();
            if (challenge) {
                difficulties[2] = AbstractQuest.QuestDifficulty.CHALLENGE;
            }
        }

        return difficulties;
    }

    private static AbstractQuest rollQuestForDifficulty(AbstractQuest.QuestDifficulty difficulty, Set<String> seenQuestIds, Random rng) {
        ArrayList<AbstractQuest> pool;
        if (difficulty != null) {
            pool = getQuestsByDifficulty(difficulty);
        } else {
            pool = new ArrayList<>();
            pool.addAll(getQuestsByDifficulty(AbstractQuest.QuestDifficulty.EASY));
            pool.addAll(getQuestsByDifficulty(AbstractQuest.QuestDifficulty.NORMAL));
            pool.addAll(getQuestsByDifficulty(AbstractQuest.QuestDifficulty.HARD));
        }

        ArrayList<AbstractQuest> possible = pool.stream()
                .filter(q -> !seenQuestIds.contains(q.id))
                .filter(q -> QuestManager.getFilterConfig(q.id))
                .filter(AbstractQuest::canSpawn)
                .collect(Collectors.toCollection(ArrayList::new));

        if (!possible.isEmpty()) {
            AbstractQuest rolled = Wiz.getRandomItem(possible, rng);
            return rolled.makeCopy();
        }

        if (difficulty != null) {
            return rollQuestForDifficulty(null, seenQuestIds, rng);
        }
        return null;
    }
}
