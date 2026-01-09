package spireQuests.quests;

import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import spireQuests.cardmods.QuestboundMod;
import spireQuests.util.Wiz;

import java.util.List;

public class QuestSave {
    public String[] questIds;
    public String[][] questData;
    public QuestReward.QuestRewardSave[][] questRewards;
    public Integer[][] questRelicIndex;

    public QuestSave() {

    }

    public QuestSave(List<AbstractQuest> quests) {
        questIds = new String[quests.size()];
        questData = new String[quests.size()][];
        questRewards = new QuestReward.QuestRewardSave[quests.size()][];
        questRelicIndex = new Integer[quests.size()][];

        for (int i = 0; i < quests.size(); ++i) {
            if (quests.get(i).questboundCards != null) {
                for (AbstractCard c : quests.get(i).questboundCards) {
                    QuestboundMod mod = (QuestboundMod) CardModifierManager.getModifiers(c, QuestboundMod.ID).get(0);
                    if (mod != null) mod.boundQuestIndex = i;
                }
            }
            if(quests.get(i).questboundRelics != null) {
                Integer[] index = new Integer[quests.get(i).questboundRelics.size()];
                for (int y = 0; y < quests.get(i).questboundRelics.size(); ++y) {
                    index[y] = Wiz.adp().relics.indexOf(quests.get(i).questboundRelics.get(y));
                }
                questRelicIndex[i] = index;
            }
            questIds[i] = quests.get(i).id;
            questData[i] = quests.get(i).trackerSaves();
            questRewards[i] = quests.get(i).rewardSaves();
        }
    }
}
