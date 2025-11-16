package spireQuests.quests;

import basemod.BaseMod;
import basemod.abstracts.CustomSavable;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
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

        for (AbstractQuest.QuestDifficulty difficulty : rollDifficulties(fromNeow)) {
            AbstractQuest quest = rollQuestForDifficulty(difficulty, seenQuests.get(AbstractDungeon.player));
            if (quest != null) {
                quest.setCost();
                generatedQuests.add(quest);
                seenQuests.get(AbstractDungeon.player).add(quest.id);
            }
        }

        return generatedQuests;
    }

    private static AbstractQuest.QuestDifficulty[] rollDifficulties(boolean fromNeow) {
        AbstractQuest.QuestDifficulty[] difficulties = new AbstractQuest.QuestDifficulty[] {
                AbstractQuest.QuestDifficulty.EASY,
                AbstractQuest.QuestDifficulty.NORMAL,
                AbstractQuest.QuestDifficulty.HARD
        };

        if (fromNeow) {
            boolean challenge = AbstractDungeon.miscRng.randomBoolean();
            if (challenge) {
                difficulties[2] = AbstractQuest.QuestDifficulty.CHALLENGE;
            }
        }

        return difficulties;
    }

    private static AbstractQuest rollQuestForDifficulty(AbstractQuest.QuestDifficulty difficulty, Set<String> seenQuestIds) {
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
                .filter(AbstractQuest::canSpawn)
                .collect(Collectors.toCollection(ArrayList::new));

        if (!possible.isEmpty()) {
            AbstractQuest rolled = Wiz.getRandomItem(possible, AbstractDungeon.miscRng);
            return rolled.makeCopy();
        }

        if (difficulty != null) {
            return rollQuestForDifficulty(null, seenQuestIds);
        }
        return null;
    }
}
