package spireQuests.commands;

import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;
import spireQuests.ui.QuestBoardProp;
import spireQuests.ui.QuestBoardScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static spireQuests.Anniv8Mod.makeID;
import static spireQuests.Anniv8Mod.modID;

public class SpawnQuestCommand extends ConsoleCommand {
    public SpawnQuestCommand() {
        minExtraTokens = 2;
        maxExtraTokens = 2;
        requiresPlayer = true;
    }

    @Override
    protected void execute(String[] tokens, int depth) {
        if (tokens.length != 3) {
            DevConsole.log("Specify the slot of the quest on the board and the id of the quest to spawn");
            return;
        }

        int i;
        try {
            i = Integer.parseInt(tokens[1]);
            if (i > 2 || i < 0) {
                throw new Exception();
            }
        } catch (Exception e) {
            DevConsole.log("Invalid quest board slot, must be between 0 and 2");
            return;
        }

        if (AbstractDungeon.player == null) {
            DevConsole.log("Must be in a run to spawn quests");
            return;
        }

        if (QuestBoardProp.questBoardProp == null) {
            DevConsole.log("Must be in a room with a quest board to spawn quests");
            return;
        }

        ArrayList<AbstractQuest> questBoardQuests = QuestBoardProp.questBoardProp.quests;
        if (questBoardQuests.size() != 3) {
            DevConsole.log("Must still have a full quest board to spawn quests");
            return;
        }

        AbstractQuest quest = QuestManager.getAllQuests().stream()
                .filter(q -> (!tokens[2].contains(":") && q.id.equals(makeID(tokens[2]))) || q.id.equals(tokens[2]))
                .findFirst().orElse(null);
        if (quest != null) {
            questBoardQuests.set(i, quest.makeCopy());
            QuestBoardScreen.init(QuestBoardProp.questBoardProp, QuestBoardProp.questBoardProp.quests);
        } else {
            DevConsole.log("No matching quest id found");
        }
    }

    @Override
    protected ArrayList<String> extraOptions(String[] tokens, int depth) {
        ArrayList<String> result = new ArrayList<>();
        result.add("0");
        result.add("1");
        result.add("2");

        if (result.contains(tokens[depth]) && tokens.length > depth + 1) {
            result.clear();
            List<String> allQuestIds = QuestManager.getAllQuests().stream().map(q -> q.id).map(s -> s.replace(modID + ":", "")).collect(Collectors.toCollection(ArrayList::new));
            result.addAll(allQuestIds);
            if (result.contains(tokens[depth + 1])) {
                complete = true;
            }
        }

        return result;
    }

    @Override
    public void errorMsg() {
        DevConsole.couldNotParse();
        DevConsole.log("syntax is: spawnquest [slot] [id]");
        DevConsole.log("* slot is from 0 to 2");
    }
}
