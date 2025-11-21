package spireQuests.commands;

import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static spireQuests.Anniv8Mod.makeID;
import static spireQuests.Anniv8Mod.modID;

public class AddQuestCommand extends ConsoleCommand {
    public AddQuestCommand() {
        minExtraTokens = 1;
        simpleCheck = true;
    }

    @Override
    protected void execute(String[] tokens, int depth) {
        if (tokens.length != 2) {
            DevConsole.log("Specify the id of the quest to add to your quest log");
            return;
        }
        AbstractQuest quest = QuestManager.getAllQuests().stream()
                .filter(q -> (!tokens[1].contains(":") && q.id.equals(makeID(tokens[1]))) || q.id.equals(tokens[1]))
                .findFirst().orElse(null);
        if (quest != null) {
            QuestManager.startQuest(quest.makeCopy());
        } else {
            DevConsole.log("No matching quest id found");
        }
    }

    @Override
    protected ArrayList<String> extraOptions(String[] tokens, int depth) {
        return QuestManager.getAllQuests().stream().map(q -> q.id).map(s -> s.replace(modID + ":", "")).collect(Collectors.toCollection(ArrayList::new));
    }
}
